package com.glebalekseevjk.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// MULTICHANNEL TRANSMISSION SYSTEMS

class MTSRepository(initData: Data) {
    private val dataState: StateFlow<Data>
        get() = _dataState
    private val _dataState: MutableStateFlow<Data> = MutableStateFlow(initData)
    fun setDataState(data: Data) {
        CoroutineScope(Dispatchers.IO).launch {
            _dataState.emit(data)
        }
    }

    val chartsDataState: StateFlow<ChartsData>
        get() = _chartsDataState
    private val _chartsDataState: MutableStateFlow<ChartsData> = MutableStateFlow(ChartsData())

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _dataState.collect {
                // K1
                val capitalInvestmentsK1AxisX = mutableListOf<Double>()
                val capitalInvestmentsK1AxisY = mutableListOf<Double>()
                // K2
                val capitalInvestmentsK2AxisX = mutableListOf<Double>()
                val capitalInvestmentsK2AxisY = mutableListOf<Double>()
                // k1
                val specificCapitalInvestmentsK1AxisX = mutableListOf<Double>()
                val specificCapitalInvestmentsK1AxisY = mutableListOf<Double>()
                // k2
                val specificCapitalInvestmentsK2AxisX = mutableListOf<Double>()
                val specificCapitalInvestmentsK2AxisY = mutableListOf<Double>()

                // delta
                val totalEconomyAxisX = mutableListOf<Double>()
                val totalEconomyAxisY = mutableListOf<Double>()
                // specific delta
                val specificEconomyAxisX = mutableListOf<Double>()
                val specificEconomyAxisY = mutableListOf<Double>()


                var maxN = 100
                var isFirst = true
                var n = 1
                while (n <= maxN) {
                    capitalInvestmentsK1AxisX.add(n.toDouble())
                    capitalInvestmentsK2AxisX.add(n.toDouble())
                    specificCapitalInvestmentsK1AxisX.add(n.toDouble())
                    specificCapitalInvestmentsK2AxisX.add(n.toDouble())
                    totalEconomyAxisX.add(n.toDouble())
                    specificEconomyAxisX.add(n.toDouble())
                    val k1 = getK1(n)
                    val k2 = getK2(n)
                    if (isFirst && k1 > k2) {
                        isFirst = false
                        maxN = 2 * n - 2
                    }
                    capitalInvestmentsK1AxisY.add(k1)
                    capitalInvestmentsK2AxisY.add(k2)

                    specificCapitalInvestmentsK1AxisY.add(getSpecificK1(n))
                    specificCapitalInvestmentsK2AxisY.add(getSpecificK2(n))

                    totalEconomyAxisY.add(getDeltaK(n))
                    specificEconomyAxisY.add(getSpecificDeltaK(n))

                    n++
                }

                val chartsData = ChartsData(
                    capitalInvestmentsK1Axis = Axis(capitalInvestmentsK1AxisX, capitalInvestmentsK1AxisY),
                    capitalInvestmentsK2Axis = Axis(capitalInvestmentsK2AxisX, capitalInvestmentsK2AxisY),
                    specificCapitalInvestmentsK1Axis = Axis(
                        specificCapitalInvestmentsK1AxisX,
                        specificCapitalInvestmentsK1AxisY
                    ),
                    specificCapitalInvestmentsK2Axis = Axis(
                        specificCapitalInvestmentsK2AxisX,
                        specificCapitalInvestmentsK2AxisY
                    ),
                    boundaryN = maxN / 2,
                    totalEconomyAxis = Axis(totalEconomyAxisX, totalEconomyAxisY),
                    specificEconomyAxis = Axis(specificEconomyAxisX, specificEconomyAxisY),
                )
                _chartsDataState.emit(chartsData)
            }
        }
    }

    fun getK1(N: Int): Double =
        (dataState.value.costOfLayingOneKmOfCommunicationLine + N * dataState.value.costPerKmOfPhysicalCircuit) *
                dataState.value.lengthOfHighwayBetweenPoints.toDouble()

    fun getK2(N: Int): Double =
        (dataState.value.costOfLayingOneKmOfCommunicationLine + dataState.value.costPerKmOfPhysicalCircuit) *
                dataState.value.lengthOfHighwayBetweenPoints.toDouble() + 2 * dataState.value.costOfTerminalStationTransmissionSystemEquipment

    fun getSpecificK1(N: Int): Double = getK1(N) / (N * dataState.value.lengthOfHighwayBetweenPoints.toDouble())

    fun getSpecificK2(N: Int): Double = getK2(N) / (N * dataState.value.lengthOfHighwayBetweenPoints.toDouble())

    fun getDeltaK(N: Int): Double = getK1(N) - getK2(N)

    fun getSpecificDeltaK(N: Int): Double = getDeltaK(N) / (N * dataState.value.lengthOfHighwayBetweenPoints.toDouble())
}

data class ChartsData(
    val capitalInvestmentsK1Axis: Axis<Double, Double> = Axis(),
    val capitalInvestmentsK2Axis: Axis<Double, Double> = Axis(),
    val specificCapitalInvestmentsK1Axis: Axis<Double, Double> = Axis(),
    val specificCapitalInvestmentsK2Axis: Axis<Double, Double> = Axis(),

    val totalEconomyAxis: Axis<Double, Double> = Axis(),
    val specificEconomyAxis: Axis<Double, Double> = Axis(),
    val boundaryN: Int = 0
)

data class Axis<T1, T2>(
    val axisX: List<T1> = emptyList(),
    val axisY: List<T2> = emptyList(),
)

data class Data(
    val lengthOfHighwayBetweenPoints: Int, // L, км
    val costPerKmOfPhysicalCircuit: Int, // Кц, руб/км
    val costOfLayingOneKmOfCommunicationLine: Int, // Кл, руб/км
    val costOfTerminalStationTransmissionSystemEquipment: Int, // Ко, руб
) {
    companion object {
        val exampleData1 = Data(
            100,
            2000,
            3000,
            50000
        )
        val exampleData2 = Data(
            120,
            2200,
            2800,
            55000
        )
        val exampleData3 = Data(
            140,
            2400,
            2600,
            60000
        )
        val exampleData4 = Data(
            160,
            2600,
            2400,
            65000
        )
        val exampleData5 = Data(
            180,
            2800,
            2200,
            70000
        )
        val exampleData6 = Data(
            200,
            3000,
            2000,
            75000
        )
        val exampleData7 = Data(
            220,
            3100,
            1900,
            80000
        )
        val exampleData8 = Data(
            240,
            3200,
            1800,
            85000
        )
        val exampleData9 = Data(
            260,
            3300,
            1700,
            90000
        )
        val exampleData10 = Data(
            280,
            3400,
            1600,
            95000
        )
        val exampleData11 = Data(
            300,
            3500,
            1500,
            94000
        )
        val exampleData12 = Data(
            320,
            1900,
            1600,
            92000
        )
        val exampleData13 = Data(
            340,
            1800,
            1700,
            88000
        )
        val exampleData14 = Data(
            360,
            1700,
            1800,
            86000
        )
        val exampleData15 = Data(
            380,
            1600,
            1900,
            84000
        )
        val exampleData16 = Data(
            400,
            1500,
            2000,
            82000
        )
        val exampleData17 = Data(
            420,
            2100,
            2100,
            78000
        )
        val exampleData18 = Data(
            440,
            2300,
            2200,
            76000
        )
        val exampleData19 = Data(
            460,
            2500,
            2300,
            74000
        )
        val exampleData20 = Data(
            480,
            2700,
            2400,
            72000
        )
    }
}