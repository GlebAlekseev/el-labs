package com.glebalekseevjk.common

import dev.benedikt.math.bezier.spline.DoubleBezierSpline
import dev.benedikt.math.bezier.vector.Vector2D
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random


class Repository(initData: Data) {
    private val dataState: StateFlow<Data>
        get() = _dataState
    private val _dataState: MutableStateFlow<Data> = MutableStateFlow(initData)
    fun setDataState(data: Data) {
        CoroutineScope(Dispatchers.IO).launch {
            _dataState.emit(data)
        }
    }

    val resultDataState: StateFlow<ResultData>
        get() = _resultDataState
    private val _resultDataState: MutableStateFlow<ResultData> = MutableStateFlow(ResultData())

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _dataState.collect {
                // Линия прямой видимости
                val lineOfSightX = mutableListOf<Double>()
                val lineOfSightY = mutableListOf<Double>()

                // Нулевой уровень
                val curvatureOfEarthX = mutableListOf<Double>()
                val curvatureOfEarthY = mutableListOf<Double>()

                // Ландшафт
                val landscapeX = mutableListOf<Double>()
                val landscapeY = mutableListOf<Double>()

                // Критическая зона Френеля
                val fresnelCriticalZoneX = mutableListOf<Double>()
                val fresnelCriticalZoneY = mutableListOf<Double>()

                val maxSteps = 10
                val step = 1 / maxSteps.toDouble()
                for (i in 0..maxSteps) {
                    val k = i * step
                    curvatureOfEarthX.add(k)
                    curvatureOfEarthY.add(dataState.value.getCurvatureYOfEarthSurface(k))

                    landscapeX.add(k)
                    landscapeY.add(curvatureOfEarthY.last() + dataState.value.getLandscapeY(k))
                }

                // Найти h1 и h2
                // Пройтись по всем точкам отсчета и проверить валидность/скорректировать
                val a = Point(0.0, landscapeY.first())
                val b = Point(1.0, landscapeY.last())
                for ((index, k) in landscapeX.withIndex()) {
                    val y = Point.getY(a, b, k)
                    val criticalZone = dataState.value.getMinimumZone(k, a.y, b.y)
                    if (y - landscapeY[index] < criticalZone) {
                        val delta = (criticalZone - (y - landscapeY[index])).absoluteValue
                        a.y += delta
                        b.y += delta
                    }
                }

                for (i in 0..maxSteps) {
                    val k = i * step

                    lineOfSightX.add(k)
                    lineOfSightY.add(Point.getY(a, b, k))

                    fresnelCriticalZoneX.add(k)
                    fresnelCriticalZoneY.add(Point.getY(a, b, k) - dataState.value.getMinimumZone(k, a.y, b.y))
                }

                val resultData = ResultData(
                    Axis(lineOfSightX, lineOfSightY),
                    Axis(curvatureOfEarthX, curvatureOfEarthY),
                    Axis(landscapeX, landscapeY),
                    Axis(fresnelCriticalZoneX, fresnelCriticalZoneY),
                    h1 = a.y - landscapeY.first(),
                    h2 = b.y - landscapeY.last()
                )
                _resultDataState.emit(resultData)
            }
        }
    }
}

data class ResultData(
    val lineOfSight: Axis<Double, Double> = Axis(), // км
    val curvatureOfEarth: Axis<Double, Double> = Axis(), // км
    val landscape: Axis<Double, Double> = Axis(), // км
    val fresnelCriticalZone: Axis<Double, Double> = Axis(), // км
    val h1: Double = 0.0, // м
    val h2: Double = 0.0, // м
)

data class Point(var x: Double, var y: Double) {
    companion object {
        fun getY(a: Point, b: Point, x: Double): Double = ((x - a.x) * (b.y - a.y) - a.y * (a.x - b.x)) / (b.x - a.x)
    }
}

data class Axis<T1, T2>(
    val axisX: List<T1> = emptyList(),
    val axisY: List<T2> = emptyList(),
)

data class Data(
    val range: Double, // Диапазон, [МГц]
    val routeLength: Double, // Длина трассы, [км]
) {
    private val lambda = lightSpeed / (range * 10.0.pow(6)) // Длина волны, м

    // x = 0..routeLength
    // y = [m]
    fun getCurvatureYOfEarthSurface(k: Double): Double =
        ((routeLength * routeLength * k * (1 - k)) / (2 * equivalentRadiusOfEarth)) * 10.0.pow(3)

    // Шум Перлина, принимает
    // x = 0..routeLength
    // h 10x..100x [м]
    fun getLandscapeY(k: Double): Double {
        return landscapeSpline.getCoordinatesAt(k).y
    }

    // Минимальная зона Френеля
    // x = 0..routeLength
    // y = [м]
    // h1 и h2 = [м]
    fun getMinimumZone(k: Double, h1: Double, h2: Double): Double {
        val deltaH = (h2 - h1).absoluteValue
        val newRouteLength = sqrt(routeLength * 10.0.pow(3).pow(2) + deltaH.pow(2))
        val c = sqrt((newRouteLength * lambda * k * (1 - k)) / 3)
        // получаю проекцию
        return cos(asin(deltaH / newRouteLength)) * c
    }

    override fun equals(other: Any?): Boolean = false

    companion object {
        val equivalentRadiusOfEarth: Int = 8500 // Эквивалентный радиус земли Rэ, [км]
        val lightSpeed = 299792458 // м/c

        private val random = Random(System.currentTimeMillis())
        private val minY = 30.0
        private val maxY = 200.0
        private val landscapeSpline = DoubleBezierSpline<Vector2D>()

        init {
            refreshLandscape()
        }

        fun refreshLandscape() {
            landscapeSpline.removeKnots()
            val maxSteps = 20
            val step = 1 / maxSteps.toDouble()
            for (i in 1..maxSteps) {
                landscapeSpline.addKnots(
                    Vector2D(step * i, random.nextDouble(minY, maxY))
                )
            }
            landscapeSpline.compute()
        }

        val exampleData1 = Data(
            160.0,
            40.0
        )
        val exampleData2 = Data(
            160.0,
            50.0
        )
        val exampleData3 = Data(
            160.0,
            60.0
        )
        val exampleData4 = Data(
            450.0,
            100.0
        )
        val exampleData5 = Data(
            450.0,
            120.0
        )

        val exampleData6 = Data(
            1500.0,
            45.0
        )

        val exampleData7 = Data(
            1500.0,
            60.0
        )

        val exampleData8 = Data(
            4000.0,
            50.0
        )

        val exampleData9 = Data(
            6000.0,
            40.0
        )

        val exampleData10 = Data(
            8000.0,
            50.0
        )

        val exampleData11 = Data(
            160.0,
            80.0
        )

        val exampleData12 = Data(
            160.0,
            100.0
        )

        val exampleData13 = Data(
            160.0,
            120.0
        )

        val exampleData14 = Data(
            450.0,
            150.0
        )

        val exampleData15 = Data(
            450.0,
            150.0
        )

        val exampleData16 = Data(
            1500.0,
            100.0
        )

        val exampleData17 = Data(
            1500.0,
            80.0
        )

        val exampleData18 = Data(
            4000.0,
            75.0
        )

        val exampleData19 = Data(
            6000.0,
            60.0
        )

        val exampleData20 = Data(
            8000.0,
            70.0
        )
    }
}