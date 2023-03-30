package com.glebalekseevjk.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt

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
                val resultData = ResultData(
                    getPolarizationModeDispersion(),
                    getLimitValueChromaticDispersionCoefficient(),
                    getChromaticDispersionValue(),
                    getResultingDispersion(),
                    getBitInterval4(),
                    getBitInterval64(),
                    getMaximumPermissibleValueOfPulseBroadening4(),
                    getMaximumPermissibleValueOfPulseBroadening64(),
                    getInitialPulseDuration4(),
                    getInitialPulseDuration64(),
                    getEndPulseDuration4(),
                    getEndPulseDuration64(),
                    getFibreOpticAttenuation(),
                    getEnergyBudget(),
                    getMaxPossibleDistance(),
                )
                _resultDataState.emit(resultData)
            }
        }
    }


    // 1.1. Расчет поляризационной модовой дисперсии
    // Поляризационная модовая дисперсия
    fun getPolarizationModeDispersion(): Double =
        Math.sqrt(dataState.value.lengthFOCL.toDouble()) * Data.polarizationModeDispersionCoefficient

    // 1.2. Расчет хроматической (волноводной) дисперсии
    // Предельное значение коэффициента хроматической дисперсии
    fun getLimitValueChromaticDispersionCoefficient(): Double =
        Data.maximumValueOfZeroDispersionSteepnessRangeWithZeroDispersion *
                (dataState.value.workingWavelength * 10.0.pow(3.0) - Data.wavelengthRangeWithZeroDispersionMin.pow(4.0) /
                        ((dataState.value.workingWavelength * 10.0.pow(3.0)).pow(3.0))) / 4

    // Значение хроматической дисперсии
    fun getChromaticDispersionValue(): Double = getLimitValueChromaticDispersionCoefficient() *
            dataState.value.maximumWidthSourceRadiationSpectrum * dataState.value.lengthFOCL

    // Результирующая дисперсия
    fun getResultingDispersion(): Double =
        sqrt(getChromaticDispersionValue().pow(2.0) + getPolarizationModeDispersion().pow(2.0))

    // Битовый интервал 4
    fun getBitInterval4(): Double = 1 / (dataState.value.transmissionRateSTM4.toDouble() * 10.0.pow(-6.0))

    // битовый интервал 64
    fun getBitInterval64(): Double = 1 / (dataState.value.transmissionRateSTM64.toDouble() * 10.0.pow(-6.0))

    // Максимально допустимая величина уширения импульсов
    fun getMaximumPermissibleValueOfPulseBroadening4(): Double = getBitInterval4() / 2
    fun getMaximumPermissibleValueOfPulseBroadening64(): Double = getBitInterval64() / 2

    // Начальная длительность импульсов
    fun getInitialPulseDuration4(): Double = getBitInterval4() / 4
    fun getInitialPulseDuration64(): Double = getBitInterval64() / 4

    // Конечная длительность импульса
    fun getEndPulseDuration4(): Double = sqrt(getInitialPulseDuration4().pow(2.0) + getResultingDispersion().pow(2.0))
    fun getEndPulseDuration64(): Double = sqrt(getInitialPulseDuration64().pow(2.0) + getResultingDispersion().pow(2.0))


    // Расчет энергетического бюджета
    //
    // Затухание ВОЛС
    fun getFibreOpticAttenuation(): Double = Data.lossesFixedJoints * dataState.value.numberCouplings +
            dataState.value.attenuation * dataState.value.lengthFOCL + Data.lossesDetachableJoints * dataState.value.numberDetachableJoints

    // Энергетический бюджет
    fun getEnergyBudget(): Double = dataState.value.powerOpticalRadiationSourceOutput -
            dataState.value.receiverSensitivity - Data.exploitationMarginForEquipment - Data.exploitationMarginForCable -
            getFibreOpticAttenuation()

    fun getMaxPossibleDistance(): Double =
        (dataState.value.powerOpticalRadiationSourceOutput - dataState.value.receiverSensitivity -
                Data.exploitationMarginForEquipment - Data.exploitationMarginForCable - Data.lossesFixedJoints * dataState.value.numberCouplings -
                Data.lossesDetachableJoints * dataState.value.numberDetachableJoints) / dataState.value.attenuation
}

data class ResultData(
    val polarizationModeDispersion: Double = 0.0, // Поляризационная модовая дисперсия
    val limitValueChromaticDispersionCoefficient: Double = 0.0, // Предельное значение коэффициента хроматической дисперсии
    val chromaticDispersionValue: Double = 0.0, // Значение хроматической дисперсии
    val resultingDispersion: Double = 0.0, // Результирующая дисперсия
    val bitInterval4: Double = 0.0, // Битовый интервал 4
    val bitInterval64: Double = 0.0, // Битовый интервал 64

    val maximumPermissibleValueOfPulseBroadening4: Double = 0.0, // Максимально допустимая величина уширения импульсов 4
    val maximumPermissibleValueOfPulseBroadening64: Double = 0.0, // Максимально допустимая величина уширения импульсов 64
    val initialPulseDuration4: Double = 0.0, // Начальная длительность импульсов
    val initialPulseDuration64: Double = 0.0, // Начальная длительность импульсов
    val endPulseDuration4: Double = 0.0, // Конечная длительность импульса
    val endPulseDuration64: Double = 0.0, // Конечная длительность импульса

    val fibreOpticAttenuation: Double = 0.0, // Затухание ВОЛС
    val energyBudget: Double = 0.0, // Энергетический бюджет
    val maxPossibleDistance: Double = 0.0, // Максимальная длина при Aэб = 0
) {
    // Условия
    val isPositiveEnergyBudget: Boolean
        get() = energyBudget > 0

    val isTauNotLongerThanBitInterval4: Boolean
        get() = bitInterval4 > endPulseDuration4

    val isTauNotLongerThanBitInterval64: Boolean
        get() = bitInterval64 > endPulseDuration64
}

data class Data(
    val lengthFOCL: Int, // L, км
    val refractiveIndexCore: Double, // n
    val workingWavelength: Double, // lambda, мкм
    val numberCouplings: Int, // n_нс
    val attenuation: Double, // дБ/км
    val numberDetachableJoints: Int, // n_рс
    val powerOpticalRadiationSourceOutput: Int, // P вых, дБм
    val receiverSensitivity: Int, // P фпр, дбм
    val maximumWidthSourceRadiationSpectrum: Double, // delta lambda, нм
    val transmissionRateSTM4: Int, // B0;4 Мбит/с
    val initialPulseDurationSTM4: Int, // t0;4 пс
    val transmissionRateSTM64: Int, // B0;64 Мбит/с
    val initialPulseWidthSTM64: Int, // t0;64 пс
) {
    companion object {
        val lossesFixedJoints: Double = 0.05 //  Ans=0,05 dB;
        val lossesDetachableJoints: Double = 0.2 //  Apc=0.2 dB;
        val exploitationMarginForEquipment: Double = 3.0 //  Aesa=3 dB;
        val exploitationMarginForCable: Double = 3.0 //  Aezk=3 dB;
        val wavelengthRangeWithZeroDispersionMin: Double = 1301.5 * Math.pow(10.0, -9.0) //  от 0 =1301,5/1321,5 нм;
        val wavelengthRangeWithZeroDispersionMax: Double = 1321.5 * Math.pow(10.0, -9.0) //  от 0 =1301,5/1321,5 нм;
        val maximumValueOfZeroDispersionSteepnessRangeWithZeroDispersion: Double = 0.092 //  S0=0.092 ps/(nm2-km);
        val polarizationModeDispersionCoefficient: Double = 0.5 //  Dpmd=0.5 ps/km1/2.

        val exampleData1 = Data(
            50,
            1.467,
            1.82,
            21,
            0.26,
            4,
            15,
            -20,
            0.02,
            620,
            420,
            9900,
            20
        )
        val exampleData2 = Data(
            52,
            1.467,
            1.80,
            21,
            0.26,
            4,
            15,
            -21,
            0.02,
            621,
            418,
            9910,
            21
        )
        val exampleData3 = Data(
            54,
            1.467,
            1.79,
            21,
            0.26,
            4,
            15,
            -22,
            0.02,
            622,
            416,
            9920,
            22
        )
        val exampleData4 = Data(
            56,
            1.467,
            1.78,
            21,
            0.26,
            4,
            15,
            -23,
            0.02,
            623,
            414,
            9930,
            23
        )
        val exampleData5 = Data(
            58,
            1.467,
            1.77,
            21,
            0.26,
            4,
            15,
            -24,
            0.02,
            624,
            412,
            9940,
            24
        )
        val exampleData6 = Data(
            60,
            1.467,
            1.76,
            21,
            0.26,
            4,
            16,
            -25,
            0.03,
            625,
            410,
            9950,
            25
        )
        val exampleData7 = Data(
            62,
            1.467,
            1.75,
            21,
            0.26,
            4,
            16,
            -26,
            0.03,
            626,
            408,
            9960,
            26
        )
        val exampleData8 = Data(
            64,
            1.467,
            1.74,
            21,
            0.26,
            4,
            16,
            -27,
            0.03,
            627,
            406,
            9970,
            27
        )
        val exampleData9 = Data(
            66,
            1.467,
            1.73,
            21,
            0.26,
            4,
            16,
            -28,
            0.03,
            628,
            404,
            9980,
            28
        )
        val exampleData10 = Data(
            68,
            1.467,
            1.72,
            21,
            0.26,
            4,
            16,
            -29,
            0.03,
            629,
            402,
            9900,
            29
        )
        val exampleData11 = Data(
            70,
            1.321,
            1.71,
            22,
            0.24,
            6,
            14,
            -30,
            0.04,
            630,
            400,
            10000,
            30
        )
        val exampleData12 = Data(
            72,
            1.321,
            1.79,
            22,
            0.24,
            6,
            14,
            -29,
            0.04,
            629,
            398,
            9990,
            29
        )
        val exampleData13 = Data(
            74,
            1.321,
            1.69,
            22,
            0.24,
            6,
            14,
            -28,
            0.04,
            628,
            396,
            9980,
            28
        )
        val exampleData14 = Data(
            76,
            1.321,
            1.68,
            22,
            0.24,
            6,
            14,
            -27,
            0.04,
            627,
            394,
            9970,
            27
        )
        val exampleData15 = Data(
            78,
            1.321,
            1.67,
            22,
            0.24,
            6,
            14,
            -26,
            0.04,
            626,
            392,
            9960,
            26
        )
        val exampleData16 = Data(
            80,
            1.321,
            1.66,
            22,
            0.24,
            6,
            13,
            -25,
            0.05,
            625,
            390,
            9950,
            25
        )
        val exampleData17 = Data(
            82,
            1.321,
            1.65,
            22,
            0.24,
            6,
            13,
            -24,
            0.05,
            624,
            388,
            9940,
            24
        )
        val exampleData18 = Data(
            84,
            1.321,
            1.64,
            22,
            0.24,
            6,
            13,
            -24,
            0.05,
            623,
            386,
            9930,
            23
        )
        val exampleData19 = Data(
            86,
            1.321,
            1.63,
            23,
            0.24,
            6,
            13,
            -22,
            0.05,
            622,
            384,
            9920,
            22
        )
        val exampleData20 = Data(
            88,
            1.321,
            1.62,
            23,
            0.24,
            6,
            13,
            -21,
            0.05,
            621,
            382,
            9910,
            21
        )
        val exampleData21 = Data(
            90,
            1.667,
            1.61,
            23,
            0.22,
            8,
            12,
            -20,
            0.06,
            620,
            380,
            9900,
            20
        )
        val exampleData22 = Data(
            92,
            1.667,
            1.60,
            23,
            0.22,
            8,
            12,
            -19,
            0.06,
            619,
            378,
            8990,
            19
        )
        val exampleData23 = Data(
            94,
            1.667,
            1.59,
            23,
            0.22,
            8,
            12,
            -18,
            0.06,
            618,
            376,
            8980,
            31
        )
        val exampleData24 = Data(
            96,
            1.667,
            1.58,
            23,
            0.22,
            8,
            12,
            -17,
            0.06,
            617,
            374,
            8970,
            18
        )
        val exampleData25 = Data(
            98,
            1.667,
            1.57,
            24,
            0.22,
            8,
            12,
            -16,
            0.06,
            616,
            372,
            8960,
            32
        )
        val exampleData26 = Data(
            100,
            1.667,
            1.56,
            24,
            0.22,
            8,
            11,
            -17,
            0.07,
            615,
            370,
            8950,
            17
        )
        val exampleData27 = Data(
            102,
            1.667,
            1.55,
            24,
            0.22,
            8,
            11,
            -18,
            0.07,
            614,
            368,
            8940,
            3
        )
        val exampleData28 = Data(
            104,
            1.667,
            1.54,
            24,
            0.22,
            8,
            11,
            -19,
            0.07,
            613,
            366,
            8930,
            16
        )
        val exampleData29 = Data(
            106,
            1.667,
            1.53,
            24,
            0.22,
            8,
            11,
            -20,
            0.07,
            612,
            364,
            8920,
            34
        )
        val exampleData30 = Data(
            108,
            1.667,
            1.52,
            24,
            0.22,
            8,
            11,
            -21,
            0.07,
            611,
            362,
            8910,
            15
        )
    }
}