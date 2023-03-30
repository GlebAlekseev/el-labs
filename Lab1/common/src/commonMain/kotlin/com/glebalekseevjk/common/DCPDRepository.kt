package com.glebalekseevjk.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DCPDRepository(initData: Data) {
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

                // Посчитать графики.
                var counterS = 0
                val amplifierAxisX = mutableListOf<Double>()
                val amplifierAxisY = mutableListOf<Double>()

                amplifierAxisX.add(-it.channelLength * .05)
                amplifierAxisY.add(it.inputPower.toDouble())

                amplifierAxisX.add(0.0)
                amplifierAxisY.add(it.inputPower.toDouble())

                amplifierAxisX.add(0.0)
                amplifierAxisY.add((it.inputPower + it.transmitterGain).toDouble())

                // пока точка пересечение с A не больше L
                for (i in 0..1000) {
                    // пересечение прямой y=-аx+lastY и y=A+Pпом
                    val intersectionX =
                        (it.interferenceLevel + it.noiseImmunity - amplifierAxisY.last()) / -it.attenuation + amplifierAxisX.last()
                    if (intersectionX > it.channelLength) break
                    val intersectionY = (it.interferenceLevel + it.noiseImmunity).toDouble()

                    amplifierAxisX.add(intersectionX)
                    amplifierAxisY.add(intersectionY)
                    counterS++

                    // добавлениене после усиления
                    amplifierAxisX.add(intersectionX)
                    amplifierAxisY.add(intersectionY + it.intermediateAmplifiers)
                }

                val intersectionX = it.channelLength.toDouble()
                val intersectionY = -it.attenuation * (it.channelLength - amplifierAxisX.last()) + amplifierAxisY.last()
                amplifierAxisX.add(intersectionX)
                amplifierAxisY.add(intersectionY)


                if (intersectionY + it.receiverGain < it.outputPower) {
                    for (i in 0..1000) {
                        // добавить усилитель
                        amplifierAxisX.add(intersectionX)
                        amplifierAxisY.add(intersectionY + it.intermediateAmplifiers)

                        if (amplifierAxisY.last() + it.receiverGain >= it.outputPower) break
                    }
                }

                // добавлениене после усиления
                amplifierAxisX.add(amplifierAxisX.last())
                amplifierAxisY.add(amplifierAxisY.last() + it.receiverGain)

                // final
                amplifierAxisX.add(amplifierAxisX.last() + it.channelLength.toDouble() * .05)
                amplifierAxisY.add(amplifierAxisY.last())

                val noiseLvlAxisX = listOf(0.0, it.channelLength.toDouble())
                val noiseLvlAxisY = listOf(
                    (it.interferenceLevel + it.noiseImmunity).toDouble(),
                    (it.interferenceLevel + it.noiseImmunity).toDouble()
                )

                val securityLvlAxisX = listOf(0.0, it.channelLength.toDouble())
                val securityLvlAxisY = listOf((it.interferenceLevel).toDouble(), (it.interferenceLevel).toDouble())

                val outputLvlAxisX = listOf(it.channelLength.toDouble(), it.channelLength.toDouble() * 1.05)
                val outputLvlAxisY = listOf((it.outputPower).toDouble(), (it.outputPower).toDouble())

                val chartsData = ChartsData(
                    amplifierAxis = Axis(amplifierAxisX, amplifierAxisY),
                    securityLvlAxis = Axis(securityLvlAxisX, securityLvlAxisY),
                    noiseLvlAxis = Axis(noiseLvlAxisX, noiseLvlAxisY),
                    outputLvlAxis = Axis(outputLvlAxisX, outputLvlAxisY),
                    counterS
                )
                _chartsDataState.emit(chartsData)
            }
        }
    }
}

data class ChartsData(
    val amplifierAxis: Axis<Double, Double> = Axis(),
    val securityLvlAxis: Axis<Double, Double> = Axis(),
    val noiseLvlAxis: Axis<Double, Double> = Axis(),
    val outputLvlAxis: Axis<Double, Double> = Axis(),
    val countS: Int = 0
)

data class Axis<T1, T2>(
    val axisX: List<T1> = emptyList(),
    val axisY: List<T2> = emptyList(),
)

data class Data(
    var inputPower: Int, // Pвх, дБ
    var transmitterGain: Int, // Sпер, дБ
    var channelLength: Int, // L, км
    var attenuation: Double, // альфа, дБ/км
    var intermediateAmplifiers: Int, // S, дБ
    var receiverGain: Int, // Sпр, дБ
    var interferenceLevel: Int, // Pпом, дБ
    var noiseImmunity: Int, // A, дБ
    var outputPower: Int, // Pвых, дБ
) {
    companion object {
        val exampleData1 = Data(
            -3,
            10,
            100,
            1.0,
            20,
            4,
            -15,
            5,
            -5
        )
        val exampleData2 = Data(
            -4,
            11,
            120,
            1.2,
            21,
            5,
            -16,
            4,
            -6
        )
        val exampleData3 = Data(
            -5,
            12,
            140,
            1.4,
            22,
            6,
            -17,
            3,
            -7
        )
        val exampleData4 = Data(
            -6,
            13,
            160,
            1.6,
            23,
            7,
            -18,
            4,
            -8
        )
        val exampleData5 = Data(
            -7,
            14,
            180,
            1.8,
            24,
            8,
            -19,
            5,
            -9
        )
        val exampleData6 = Data(
            -8,
            15,
            200,
            2.0,
            25,
            9,
            -20,
            6,
            -10
        )
        val exampleData7 = Data(
            -9,
            16,
            240,
            2.2,
            24,
            8,
            -21,
            7,
            -11
        )
        val exampleData8 = Data(
            -10,
            17,
            280,
            2.4,
            23,
            7,
            -22,
            6,
            -10
        )
        val exampleData9 = Data(
            -9,
            18,
            320,
            2.6,
            22,
            6,
            -23,
            5,
            -9
        )
        val exampleData10 = Data(
            -8,
            19,
            340,
            2.8,
            21,
            5,
            -24,
            4,
            -8
        )
    }
}