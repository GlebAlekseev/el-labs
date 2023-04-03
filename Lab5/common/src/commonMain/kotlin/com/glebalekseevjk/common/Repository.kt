package com.glebalekseevjk.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.log10
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
                // Зависимость количества каналов n от нагрузки А
                val nOnTheLoadAX = mutableListOf<Double>()
                val nOnTheLoadAY = mutableListOf<Double>()

                for (a in 100..1000 step 100) {
                    nOnTheLoadAX.add(getNFromA(a.toDouble()))
                    nOnTheLoadAY.add(a.toDouble())
                }

                val frequencyPlanTable = mutableListOf<FrequencyPlanRow>()

                var lastFrequencyUplink = Data.startUplinkFrequencies
                var lastFrequencyDownlink = Data.startDownlinkFrequencies
                for (i in 0 until getNumberBaseStation().toInt()) {
                    val sector1nf = (getNFrequencyFullCell() / Data.M).roundUp().toInt()
                    val sector2nf = (getNFrequencyFullCell() / Data.M).toInt()
                    val sector3nf = (getNFrequencyFullCell() - sector1nf - sector2nf).toInt()

                    frequencyPlanTable.add(
                        FrequencyPlanRow(
                            "Кластер №${(i / Data.M) + 1} БС №${i % Data.N + 1}",
                            listOf(sector1nf, sector2nf, sector3nf),
                            listOf(
                                lastFrequencyUplink,
                                lastFrequencyUplink + sector1nf * Data.spectrumWidth,
                                (lastFrequencyUplink + sector1nf * Data.spectrumWidth + sector2nf * Data.spectrumWidth)
                                    .also { lastFrequencyUplink = it + sector3nf * Data.spectrumWidth },
                            ),
                            listOf(
                                lastFrequencyDownlink,
                                lastFrequencyDownlink + sector1nf * Data.spectrumWidth,
                                (lastFrequencyDownlink + sector1nf * Data.spectrumWidth + sector2nf * Data.spectrumWidth)
                                    .also { lastFrequencyDownlink = it + sector3nf * Data.spectrumWidth },
                            ),
                        )
                    )
                    if (i / Data.M + 1 == 3) {
                        lastFrequencyUplink = Data.startUplinkFrequencies
                        lastFrequencyDownlink = Data.startDownlinkFrequencies
                    }
                }

                _resultDataState.emit(
                    ResultData(
                        getNab(),
                        getA(),
                        Axis(nOnTheLoadAX, nOnTheLoadAY),
                        getNFromA(getA()),
                        getNFrequencyFullNetwork(),
                        getNFrequencyFullCell(),
                        getNFrequencyFullSector(),
                        getNumberBaseStation(),
                        getMediumUrbanSignalLoss(),
                        getLargeUrbanSignalLoss(),
                        getMediumDenseUrbanSignalLoss(),
                        getLargeDenseUrbanSignalLoss(),
                        getMediumSuburbSignalLoss(),
                        getLargeSuburbSignalLoss(),
                        getMediumRuralAreasSignalLoss(),
                        getLargeRuralAreasSignalLoss(),
                        getMediumWaterSpaceSignalLoss(),
                        getLargeWaterSpaceSignalLoss(),
                        getRadiusCell(),
                        frequencyPlanTable,
                    )
                )
            }
        }
    }

    //1. Вычислить нагрузку абонентов;

    // Количество потенциальных абонентов
    private fun getNab() = 0.8 * dataState.value.numberOfPeople // Тыс. Чел.

    // Нагрузка абонентов
    private fun getA() = getNab() * dataState.value.betta * 1000 // Эрланг

    // Зависимость количества каналов n от нагрузки А
    private fun getNFromA(a: Double): Double = (a - 100.0).roundUp()

    // Число частотных каналов всего и для каждого сектора
    private fun getNFrequencyFullNetwork(): Double = (getNFromA(getA()) / Data.valueNa).roundUp()

    private fun getNFrequencyFullCell(): Double = (getNFrequencyFullNetwork() / getNumberBaseStation()).roundUp()

    private fun getNFrequencyFullSector(): Double = (getNFrequencyFullCell() / Data.M).roundUp()

    //  Кол-во БС для всей сети
    private fun getNumberBaseStation(): Double = (getNFrequencyFullNetwork() / Data.valueNfbc).roundUp()

    // Поправка на размер среднего города, дБ
    private fun getCorrectionSizeCityMedium(): Double =
        (1.1 * log10(Data.averageFrequency) - 0.7) * Data.heightMSAntennaAboveGround -
                (1.56 * log10(Data.averageFrequency) - 0.8)

    // Поправка на размер большого города, дБ
    private fun getCorrectionSizeCityLarge(): Double =
        3.2 * log10(11.75 * Data.heightMSAntennaAboveGround).pow(2.0) - 4.97

    // Потери сигнала для средней городской застройки, дБ
    private fun getMediumUrbanSignalLoss(): Double = 46.3 + 33.9 * log10(Data.averageFrequency) -
            13.82 * log10(dataState.value.heightBaseStationAntenna) - getCorrectionSizeCityMedium() -
            Data.heightMSAntennaAboveGround + (44.9 + 6.55 * log10(dataState.value.heightBaseStationAntenna)) * log10(
        Data.distanceFromBSToMS * 10.0.pow(3)
    )

    // Потери сигнала для большой городской застройки, дБ
    private fun getLargeUrbanSignalLoss(): Double = 46.3 + 33.9 * log10(Data.averageFrequency) -
            13.82 * log10(dataState.value.heightBaseStationAntenna) - getCorrectionSizeCityLarge() -
            Data.heightMSAntennaAboveGround + (44.9 + 6.55 * log10(dataState.value.heightBaseStationAntenna)) * log10(
        Data.distanceFromBSToMS * 10.0.pow(3)
    )

    // Потери сигнала для средней плотной городской застройки, дБ
    private fun getMediumDenseUrbanSignalLoss(): Double = getMediumUrbanSignalLoss() + 3

    // Потери сигнала для большой плотной городской застройки, дБ
    private fun getLargeDenseUrbanSignalLoss(): Double = getLargeUrbanSignalLoss() + 3


    // Потери сигнала для среднего пригорода, дБ
    private fun getMediumSuburbSignalLoss(): Double =
        getMediumUrbanSignalLoss() - 2 * log10(Data.averageFrequency / 28).pow(2) - 5.4

    // Потери сигнала для большого пригорода, дБ
    private fun getLargeSuburbSignalLoss(): Double =
        getLargeUrbanSignalLoss() - 2 * log10(Data.averageFrequency / 28).pow(2) - 5.4


    // Потери сигнала для средней сельской местности, дБ
    private fun getMediumRuralAreasSignalLoss(): Double =
        getMediumUrbanSignalLoss() - 4.78 * log10(Data.averageFrequency).pow(2) +
                18.33 * log10(Data.averageFrequency) - 35.94

    // Потери сигнала для большой сельской местности, дБ
    private fun getLargeRuralAreasSignalLoss(): Double =
        getLargeUrbanSignalLoss() - 4.78 * log10(Data.averageFrequency).pow(2) +
                18.33 * log10(Data.averageFrequency) - 35.94


    // Потери сигнала для среднего водного пространства, дБ
    private fun getMediumWaterSpaceSignalLoss(): Double =
        getMediumUrbanSignalLoss() - 4.78 * log10(Data.averageFrequency).pow(2) +
                18.33 * log10(Data.averageFrequency) - 40.94

    // Потери сигнала для большого водного пространства, дБ
    private fun getLargeWaterSpaceSignalLoss(): Double =
        getLargeUrbanSignalLoss() - 4.78 * log10(Data.averageFrequency).pow(2) +
                18.33 * log10(Data.averageFrequency) - 40.94

    // Радиус сот, км
    private fun getRadiusCell(): Double = sqrt(2 / (3 * sqrt(3.0))) * dataState.value.areaSize / getNumberBaseStation()
}

data class ResultData(
    val valueNab: Double = 0.0,
    val valueA: Double = 0.0,
    val nOnTheLoadA: Axis<Double, Double> = Axis(),
    val n: Double = 0.0,
    val nFrequencyFullNetwork: Double = 0.0,
    val nFrequencyCell: Double = 0.0,
    val nFrequencySector: Double = 0.0,
    val numberBaseStation: Double = 0.0,
    val mediumUrbanSignalLoss: Double = 0.0,
    val largeUrbanSignalLoss: Double = 0.0,
    val mediumDenseUrbanSignalLoss: Double = 0.0,
    val largeDenseUrbanSignalLoss: Double = 0.0,
    val mediumSuburbSignalLoss: Double = 0.0,
    val largeSuburbSignalLoss: Double = 0.0,
    val mediumRuralAreasSignalLoss: Double = 0.0,
    val largeRuralAreasSignalLoss: Double = 0.0,
    val mediumWaterSpaceSignalLoss: Double = 0.0,
    val largeWaterSpaceSignalLoss: Double = 0.0,
    val radiusCell: Double = 0.0,
    val frequencyPlanTable: List<FrequencyPlanRow> = emptyList()
)

data class Axis<T1, T2>(
    val axisX: List<T1> = emptyList(),
    val axisY: List<T2> = emptyList(),
)

data class FrequencyPlanRow(
    val numberBSNumberCluster: String, // Кластер №1 БС №1
    val numberFrequencies: List<Int>, // Список количества частотных каналов по секторам
    val uplinkFrequency: List<Double>, // Список uplink частот для каждого сектора
    val downlinkFrequency: List<Double>, // Список downlink частот для каждого сектора
)

data class Data(
    val numberOfPeople: Double = 0.0, // тыч. чел. (количество населения)
    val pBC: Int = 0, // Вт.
    val pMC: Int = 0, // дБВт
    val gBC: Int = 0, // дБ
    val typeOfTerrain: TypeOfTerrain = TypeOfTerrain.CITY, // Тип местности
    val betta: Double = 0.03, // β – активность 1 абонента в ЧНН (час наибольшей нагрузки), Эрланг
    val heightBaseStationAntenna: Double = 10.0, // hэф – высота антенны базовой станции, м;
    val areaSize: Double = 5.0,
) {
    enum class TypeOfTerrain {
        CITY, // Город
        RURAL_AREAS, // Сельская местность
        SUBURB, // Пригород
    }

    companion object {
        const val M = 3 // количество секторов в соте
        const val N = 3 // размерность кластера
        val valueNa = 8 // 8 абонентов делят 1 частотный канал
        val valueNfbc = 11 // количество частотных каналов 1 базовой станции (nfБc=11).
        val averageFrequency = 1750.0 // средняя частота f, МГц
        val heightMSAntennaAboveGround = 72.0 // высота антенны МС над землей, м
        val distanceFromBSToMS = 10.0 // расстояние от базовой станции (БС) до мобильной станции (МС), км;
        val spectrumWidth: Double = 0.2 // Ширина канала, МГц
        val startUplinkFrequencies: Double = 1710.0 // МГц
        val startDownlinkFrequencies: Double = 1805.0 // МГц

        val exampleData1 = Data(
            10.0,
            5,
            -110,
            12,
            TypeOfTerrain.CITY,
        )
        val exampleData2 = Data(
            11.0,
            10,
            -110,
            13,
            TypeOfTerrain.RURAL_AREAS,
        )
        val exampleData3 = Data(
            12.0,
            15,
            -110,
            14,
            TypeOfTerrain.SUBURB,
        )
        val exampleData4 = Data(
            13.0,
            20,
            -110,
            15,
            TypeOfTerrain.CITY,
        )
        val exampleData5 = Data(
            14.0,
            25,
            -110,
            12,
            TypeOfTerrain.RURAL_AREAS,
        )
        val exampleData6 = Data(
            15.0,
            30,
            -110,
            13,
            TypeOfTerrain.SUBURB,
        )
        val exampleData7 = Data(
            16.0,
            35,
            -110,
            14,
            TypeOfTerrain.CITY,
        )
        val exampleData8 = Data(
            17.0,
            40,
            -110,
            15,
            TypeOfTerrain.RURAL_AREAS,
        )
        val exampleData9 = Data(
            18.0,
            45,
            -110,
            12,
            TypeOfTerrain.SUBURB,
        )
        val exampleData10 = Data(
            19.0,
            50,
            -110,
            13,
            TypeOfTerrain.CITY,
        )
        val exampleData11 = Data(
            20.0,
            5,
            -120,
            14,
            TypeOfTerrain.RURAL_AREAS,
        )
        val exampleData12 = Data(
            21.0,
            10,
            -120,
            15,
            TypeOfTerrain.SUBURB,
        )
        val exampleData13 = Data(
            22.0,
            15,
            -120,
            12,
            TypeOfTerrain.CITY,
        )
        val exampleData14 = Data(
            23.0,
            20,
            -120,
            13,
            TypeOfTerrain.RURAL_AREAS,
        )
        val exampleData15 = Data(
            24.0,
            25,
            -120,
            14,
            TypeOfTerrain.SUBURB,
        )
        val exampleData16 = Data(
            25.0,
            30,
            -120,
            15,
            TypeOfTerrain.CITY,
        )
        val exampleData17 = Data(
            26.0,
            35,
            -120,
            12,
            TypeOfTerrain.RURAL_AREAS,
        )
        val exampleData18 = Data(
            27.0,
            40,
            -120,
            13,
            TypeOfTerrain.SUBURB,
        )
        val exampleData19 = Data(
            28.0,
            45,
            -120,
            14,
            TypeOfTerrain.CITY,
        )
        val exampleData20 = Data(
            29.0,
            50,
            -120,
            15,
            TypeOfTerrain.RURAL_AREAS,
        )
    }
}