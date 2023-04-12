package com.glebalekseevjk.common.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.glebalekseevjk.common.Data
import com.glebalekseevjk.common.Repository
import com.glebalekseevjk.common.roundToString
import com.glebalekseevjk.common.ui.widget.data.GeneralData
import com.glebalekseevjk.common.ui.widget.table.TableWidget
import ui.widget.MainWrapper
import ui.widget.linechart.sourcesignal.CombinedLineChartPlot

val repository = Repository(Data.exampleData1)

val modifierTextField = Modifier.width(400.dp).padding(vertical = 5.dp)

fun numberOfPeopleInputValidator(text: String): Boolean =
    text.toDoubleOrNull() != null && text.toDouble() > 0

fun bettaLengthInputValidator(text: String): Boolean =
    text.toDoubleOrNull() != null && text.toDouble() > 0

fun heightBaseStationAntennaLengthInputValidator(text: String): Boolean =
    text.toDoubleOrNull() != null && text.toDouble() > 0

fun areaSizeInputValidator(text: String): Boolean =
    text.toDoubleOrNull() != null && text.toDouble() > 0

@Composable
fun MainScreen() {
    val resultDataState by repository.resultDataState.collectAsState()
    var textFieldNumberOfPeople by remember { mutableStateOf(TextFieldValue()) }
    var textFieldBetta by remember { mutableStateOf(TextFieldValue()) }
    var textFieldHeightBaseStationAntenna by remember { mutableStateOf(TextFieldValue()) }
    var textFieldHeightAreaSize by remember { mutableStateOf(TextFieldValue()) }

    fun setData(data: Data) {
        textFieldNumberOfPeople = TextFieldValue(data.numberOfPeople.toString())
        textFieldBetta = TextFieldValue(data.betta.toString())
        textFieldHeightBaseStationAntenna = TextFieldValue(data.heightBaseStationAntenna.toString())
        textFieldHeightAreaSize = TextFieldValue(data.areaSize.toString())
    }

    fun updateData() {
        val data = runCatching {
            Data(
                numberOfPeople = textFieldNumberOfPeople.text.toDoubleOrNull() ?: throw RuntimeException(),
                betta = textFieldBetta.text.toDoubleOrNull() ?: throw RuntimeException(),
                heightBaseStationAntenna = textFieldHeightBaseStationAntenna.text.toDoubleOrNull()
                    ?: throw RuntimeException(),
                areaSize = textFieldHeightAreaSize.text.toDoubleOrNull() ?: throw RuntimeException(),
            )
        }
        val value = data.getOrNull()
        if (data.isSuccess
            && numberOfPeopleInputValidator(textFieldNumberOfPeople.text)
            && bettaLengthInputValidator(textFieldBetta.text)
            && heightBaseStationAntennaLengthInputValidator(textFieldHeightBaseStationAntenna.text)
            && areaSizeInputValidator(textFieldHeightAreaSize.text)
        ) repository.setDataState(value!!)
    }
    setData(Data.exampleData1)
    Column {
        MainWrapper {

            // Поля ввода
            // β – активность 1 абонента в ЧНН
            // Высота антенны базовой станции, м

            Column(Modifier.padding(top = 15.dp, bottom = 15.dp)) {
                OutlinedTextField(
                    singleLine = true,
                    value = textFieldNumberOfPeople,
                    label = {
                        Text("Количество населения, тыс. чел.")
                    },
                    isError = (!numberOfPeopleInputValidator(textFieldNumberOfPeople.text))
                        .also { if (!it) updateData() },
                    onValueChange = {
                        textFieldNumberOfPeople = it
                    },
                    modifier = modifierTextField
                )
                OutlinedTextField(
                    singleLine = true,
                    value = textFieldBetta,
                    label = {
                        Text("β – активность 1 абонента в ЧНН, Эрланг")
                    },
                    isError = (!bettaLengthInputValidator(textFieldBetta.text))
                        .also { if (!it) updateData() },
                    onValueChange = {
                        textFieldBetta = it
                    },
                    modifier = modifierTextField
                )
            }

            // На каждом пункте таблица констант.
            //1. Вычислить нагрузку абонентов;

            // Количество потенциальных абонентов
            Column(modifier = Modifier.width(800.dp)) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Количество потенциальных абонентов рассчитывается как 80 % от Nн: ${
                        resultDataState.valueNab.roundToString(
                            2
                        )
                    } тыс. чел."
                )
                // Нагрузка абонентов
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Нагрузка абонентов: ${resultDataState.valueA.roundToString(2)}"
                )
            }

            //2. Определить размерность кластера сети;
            // M = 3
            // N = 3
            Column(modifier = Modifier.width(800.dp)) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Размерность кластера: ${Data.M}"
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Количество секторов в соте: ${Data.N}"
                )
            }

            //3. Определить число каналов связи;
            // График Зависимость количества каналов n от нагрузки А
            // Показать n для A

            Column(modifier = Modifier.heightIn(0.dp, 350.dp).width(700.dp).padding(15.dp)) {
                CombinedLineChartPlot(
                    GeneralData(
                        listOf(
                            Pair(
                                resultDataState.nOnTheLoadA.axisX,
                                resultDataState.nOnTheLoadA.axisY,
                            ),
                        ),
                        false
                    ),
                    title = "Зависимость количества каналов n от нагрузки А",
                    axisYLabel = "Нагрузка A",
                )
            }
            Column(modifier = Modifier.width(800.dp)) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Количества разговорных каналов n от нагрузки А: ${resultDataState.n.toInt()}"
                )
            }

            //4. Определить число частотных каналов;
            // Число частотных каналов всего и для каждого сектора
            Column(modifier = Modifier.width(800.dp)) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Количества частотных каналов: ${resultDataState.nFrequencyFullNetwork.toInt()}"
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Количества частотных каналов на ячейку: ${resultDataState.nFrequencyCell.toInt()}"
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Количества частотных каналов на сектор: ${resultDataState.nFrequencySector.toInt()}"
                )
            }

            //5. Определить количество необходимых базовых станций;
            // Кол-во БС каждого сектора/всего
            Column(modifier = Modifier.width(800.dp)) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Базовых станций для всей сети: ${resultDataState.numberBaseStation.toInt()}"
                )
            }

            Column {
                OutlinedTextField(
                    singleLine = true,
                    value = textFieldHeightBaseStationAntenna,
                    label = {
                        Text("hэф – высота антенны базовой станции, м")
                    },
                    isError = (!heightBaseStationAntennaLengthInputValidator(textFieldHeightBaseStationAntenna.text))
                        .also { if (!it) updateData() },
                    onValueChange = {
                        textFieldHeightBaseStationAntenna = it
                    },
                    modifier = modifierTextField
                )
            }

            //6. Определить потери и вычислить радиусы сот;
            // L большой/средний соотв a
            Column(modifier = Modifier.width(800.dp)) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 30.dp),
                    textAlign = TextAlign.Left,
                    text = "Определение потерь сигнала при распространении по методу Окамура-Хата в диапазоне частот 1500.. .2000 МГц"
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Для средней городской застройки: ${resultDataState.mediumUrbanSignalLoss.roundToString(2)} дБ"
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Для большой городской застройки: ${resultDataState.largeUrbanSignalLoss.roundToString(2)} дБ"
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Для средней плотной городской застройки: ${
                        resultDataState.mediumDenseUrbanSignalLoss.roundToString(
                            2
                        )
                    } дБ"
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Для большой плотной городской застройки: ${
                        resultDataState.largeDenseUrbanSignalLoss.roundToString(
                            2
                        )
                    } дБ"
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Для среднего пригорода: ${resultDataState.mediumSuburbSignalLoss.roundToString(2)} дБ"
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Для большого пригорода: ${resultDataState.largeSuburbSignalLoss.roundToString(2)} дБ"
                )

                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Для средней сельской местности: ${resultDataState.mediumRuralAreasSignalLoss.roundToString(2)} дБ"
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Для большой сельской местности: ${resultDataState.largeRuralAreasSignalLoss.roundToString(2)} дБ"
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Для среднего водного пространства: ${
                        resultDataState.mediumWaterSpaceSignalLoss.roundToString(
                            2
                        )
                    } дБ"
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 25.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Для большого водного пространства: ${
                        resultDataState.largeWaterSpaceSignalLoss.roundToString(
                            2
                        )
                    } дБ"
                )
            }

            Column {
                OutlinedTextField(
                    singleLine = true,
                    value = textFieldHeightAreaSize,
                    label = {
                        Text("St – площадь территории, на которой проектируется сеть, км^2")
                    },
                    isError = (!areaSizeInputValidator(textFieldHeightAreaSize.text))
                        .also { if (!it) updateData() },
                    onValueChange = {
                        textFieldHeightAreaSize = it
                    },
                    modifier = modifierTextField
                )
            }

            Column(modifier = Modifier.width(800.dp)) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp),
                    textAlign = TextAlign.Left,
                    text = "Радиус ячейки: ${resultDataState.radiusCell.roundToString(2)} км"
                )
            }

            TableWidget(
                resultDataState.frequencyPlanTable.map {
                    listOf(
                        it.numberBSNumberCluster,
                        it.numberFrequencies.joinToString(" - "),
                        it.uplinkFrequency.map { it.roundToString(2) }.joinToString(" - "),
                        it.downlinkFrequency.map { it.roundToString(2) }.joinToString(" - "),
                    )
                },
                listOf(
                    "№ БС\n№ кластера",
                    "№ nf",
                    "Частота uplink, МГц",
                    "Частота downlink, МГц",
                ),
                floatArrayOf(0.25f, 0.10f, 0.32f, 0.33f),
                width = 1000.dp,
                height = 900.dp,
                headerCellHeight = 80.dp,
                contentCellHeight = 60.dp,
            )
        }
    }
}