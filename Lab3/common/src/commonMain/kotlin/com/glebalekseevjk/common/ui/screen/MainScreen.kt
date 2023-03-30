package ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.glebalekseevjk.common.Data
import com.glebalekseevjk.common.Repository
import com.glebalekseevjk.common.roundToString
import com.glebalekseevjk.common.ui.widget.table.TableWidget
import ui.widget.MainWrapper

val repository = Repository(Data.exampleData1)

val modifierTextField = Modifier.width(400.dp).padding(vertical = 5.dp)

val mapExampleData1 = mapOf<String, Data>(
    "#1" to Data.exampleData1,
    "#2" to Data.exampleData2,
    "#3" to Data.exampleData3,
    "#4" to Data.exampleData4,
    "#5" to Data.exampleData5,
)

val mapExampleData2 = mapOf<String, Data>(
    "#6" to Data.exampleData6,
    "#7" to Data.exampleData7,
    "#8" to Data.exampleData8,
    "#9" to Data.exampleData9,
    "#10" to Data.exampleData10,
)

val mapExampleData3 = mapOf<String, Data>(
    "#11" to Data.exampleData11,
    "#12" to Data.exampleData12,
    "#13" to Data.exampleData13,
    "#14" to Data.exampleData14,
    "#15" to Data.exampleData15,
)

val mapExampleData4 = mapOf<String, Data>(
    "#16" to Data.exampleData16,
    "#17" to Data.exampleData17,
    "#18" to Data.exampleData18,
    "#19" to Data.exampleData19,
    "#20" to Data.exampleData20,
)

val mapExampleData5 = mapOf<String, Data>(
    "#21" to Data.exampleData21,
    "#22" to Data.exampleData22,
    "#23" to Data.exampleData23,
    "#24" to Data.exampleData24,
    "#25" to Data.exampleData25,
)

val mapExampleData6 = mapOf<String, Data>(
    "#26" to Data.exampleData26,
    "#27" to Data.exampleData27,
    "#28" to Data.exampleData28,
    "#29" to Data.exampleData29,
    "#30" to Data.exampleData30,
)

val aligningModifier = Modifier.width(1000.dp)

fun isIntegerInputValidator(text: String): Boolean = text.toIntOrNull() != null
fun isDoubleInputValidator(text: String): Boolean = text.toDoubleOrNull() != null
fun isPositiveIntegerInputValidator(text: String): Boolean = text.toIntOrNull() != null && text.toInt() > 0
fun isPositiveDoubleInputValidator(text: String): Boolean = text.toDoubleOrNull() != null && text.toDouble() > 0

@Composable
fun MainScreen() {
    val resultDataState by repository.resultDataState.collectAsState()

    var textFieldLengthFOCL by remember { mutableStateOf(TextFieldValue()) }
    var textFieldRefractiveIndexCore by remember { mutableStateOf(TextFieldValue()) }
    var textFieldWorkingWavelength by remember { mutableStateOf(TextFieldValue()) }
    var textFieldNumberCouplings by remember { mutableStateOf(TextFieldValue()) }
    var textFieldAttenuation by remember { mutableStateOf(TextFieldValue()) }
    var textFieldNumberDetachableJoints by remember { mutableStateOf(TextFieldValue()) }
    var textFieldPowerOpticalRadiationSourceOutput by remember { mutableStateOf(TextFieldValue()) }
    var textFieldReceiverSensitivity by remember { mutableStateOf(TextFieldValue()) }
    var textFieldMaximumWidthSourceRadiationSpectrum by remember { mutableStateOf(TextFieldValue()) }
    var textFieldTransmissionRateSTM4 by remember { mutableStateOf(TextFieldValue()) }
    var textFieldInitialPulseDurationSTM4 by remember { mutableStateOf(TextFieldValue()) }
    var textFieldTransmissionRateSTM64 by remember { mutableStateOf(TextFieldValue()) }
    var textFieldInitialPulseWidthSTM64 by remember { mutableStateOf(TextFieldValue()) }

    fun setData(data: Data) {
        textFieldLengthFOCL = TextFieldValue(data.lengthFOCL.toString())
        textFieldRefractiveIndexCore = TextFieldValue(data.refractiveIndexCore.toString())
        textFieldWorkingWavelength = TextFieldValue(data.workingWavelength.toString())
        textFieldNumberCouplings = TextFieldValue(data.numberCouplings.toString())
        textFieldAttenuation = TextFieldValue(data.attenuation.toString())
        textFieldNumberDetachableJoints = TextFieldValue(data.numberDetachableJoints.toString())
        textFieldPowerOpticalRadiationSourceOutput = TextFieldValue(data.powerOpticalRadiationSourceOutput.toString())
        textFieldReceiverSensitivity = TextFieldValue(data.receiverSensitivity.toString())
        textFieldMaximumWidthSourceRadiationSpectrum =
            TextFieldValue(data.maximumWidthSourceRadiationSpectrum.toString())
        textFieldTransmissionRateSTM4 = TextFieldValue(data.transmissionRateSTM4.toString())
        textFieldInitialPulseDurationSTM4 = TextFieldValue(data.initialPulseDurationSTM4.toString())
        textFieldTransmissionRateSTM64 = TextFieldValue(data.transmissionRateSTM64.toString())
        textFieldInitialPulseWidthSTM64 = TextFieldValue(data.initialPulseWidthSTM64.toString())
    }

    fun updateData() {
        val data = runCatching {
            Data(
                textFieldLengthFOCL.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldRefractiveIndexCore.text.toDoubleOrNull() ?: throw RuntimeException(),
                textFieldWorkingWavelength.text.toDoubleOrNull() ?: throw RuntimeException(),
                textFieldNumberCouplings.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldAttenuation.text.toDoubleOrNull() ?: throw RuntimeException(),
                textFieldNumberDetachableJoints.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldPowerOpticalRadiationSourceOutput.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldReceiverSensitivity.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldMaximumWidthSourceRadiationSpectrum.text.toDoubleOrNull() ?: throw RuntimeException(),
                textFieldTransmissionRateSTM4.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldInitialPulseDurationSTM4.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldTransmissionRateSTM64.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldInitialPulseWidthSTM64.text.toIntOrNull() ?: throw RuntimeException(),
            )
        }
        val value = data.getOrNull()
        if (data.isSuccess
            && isPositiveIntegerInputValidator(textFieldLengthFOCL.text)
            && isPositiveDoubleInputValidator(textFieldRefractiveIndexCore.text)
            && isPositiveDoubleInputValidator(textFieldWorkingWavelength.text)
            && isPositiveIntegerInputValidator(textFieldNumberCouplings.text)
            && isPositiveDoubleInputValidator(textFieldAttenuation.text)
            && isPositiveIntegerInputValidator(textFieldNumberDetachableJoints.text)
            && isPositiveIntegerInputValidator(textFieldPowerOpticalRadiationSourceOutput.text)
            && isIntegerInputValidator(textFieldReceiverSensitivity.text)
            && isPositiveDoubleInputValidator(textFieldMaximumWidthSourceRadiationSpectrum.text)
            && isPositiveIntegerInputValidator(textFieldTransmissionRateSTM4.text)
            && isPositiveIntegerInputValidator(textFieldInitialPulseDurationSTM4.text)
            && isPositiveIntegerInputValidator(textFieldTransmissionRateSTM64.text)
            && isPositiveIntegerInputValidator(textFieldInitialPulseWidthSTM64.text)
        ) repository.setDataState(value!!)
    }

    setData(Data.exampleData1)

    Column {
        MainWrapper {
            Row {
                Text(
                    modifier = Modifier
                        .padding(bottom = 10.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    text = "Расчет технических характеристик магистральной волоконно-оптических линий связи"
                )
            }

            Row {
                Column {
                    TableWidget(
                        listOf(
                            listOf(
                                resultDataState.polarizationModeDispersion.roundToString(),
                                resultDataState.limitValueChromaticDispersionCoefficient.roundToString(),
                                resultDataState.chromaticDispersionValue.roundToString(),
                                resultDataState.resultingDispersion.roundToString(),
                                resultDataState.bitInterval4.roundToString(),
                                resultDataState.bitInterval64.roundToString(),
                            ),
                        ),
                        listOf(
                            "Поляризационная модовая дисперсия, пс",
                            "Предельное значение коэффициента хроматической дисперсии, пс/(нм*км)",
                            "Значение хроматической дисперсии, пс",
                            "Результирующая дисперсия, пс",
                            "Битовый интервал 4, пс",
                            "Битовый интервал 64, пс",
                        ),
                        floatArrayOf(0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f),
                        width = 1000.dp,
                        height = 500.dp,
                        headerCellHeight = 140.dp,
                        contentCellHeight = 60.dp,
                    )

                    TableWidget(
                        listOf(
                            listOf(
                                resultDataState.maximumPermissibleValueOfPulseBroadening4.roundToString(),
                                resultDataState.maximumPermissibleValueOfPulseBroadening64.roundToString(),
                                resultDataState.initialPulseDuration4.roundToString(),
                                resultDataState.initialPulseDuration64.roundToString(),
                                resultDataState.endPulseDuration4.roundToString(),
                                resultDataState.endPulseDuration64.roundToString(),
                            ),
                        ),
                        listOf(
                            "Максимально допустимая величина уширения импульсов 4, пс",
                            "Максимально допустимая величина уширения импульсов 64, пс",
                            "Начальная длительность импульсов 4, пс",
                            "Начальная длительность импульсов 64, пс",
                            "Конечная длительность импульса 4, пс",
                            "Конечная длительность импульса 64, пс",
                        ),
                        floatArrayOf(0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f),
                        width = 1000.dp,
                        height = 500.dp,
                        headerCellHeight = 140.dp,
                        contentCellHeight = 60.dp,
                    )

                    TableWidget(
                        listOf(
                            listOf(
                                resultDataState.fibreOpticAttenuation.roundToString(),
                                "${resultDataState.isTauNotLongerThanBitInterval4}\n" +
                                        resultDataState.bitInterval4.roundToString() + ">" + resultDataState.endPulseDuration4.roundToString(),
                                "${resultDataState.isTauNotLongerThanBitInterval64}\n" +
                                        resultDataState.bitInterval64.roundToString() + ">" + resultDataState.endPulseDuration64.roundToString(),
                                resultDataState.energyBudget.roundToString(),
                                "${resultDataState.isPositiveEnergyBudget}",
                                resultDataState.maxPossibleDistance.roundToString(),
                            ),
                        ),
                        listOf(
                            "Затухание ВОЛС, дБ",
                            "Условие: T4 > tau4(конечная)",
                            "Условие: T64 > tau64(конечная)",
                            "Энергетический бюджет, дБ",
                            "Условие: Aэб > 0",
                            "Максимальная длина при Aэб = 0, км",
                        ),
                        floatArrayOf(0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f),
                        width = 1000.dp,
                        height = 500.dp,
                        headerCellHeight = 80.dp,
                        contentCellHeight = 100.dp,
                    )
                }
            }

            Column(aligningModifier, horizontalAlignment = Alignment.CenterHorizontally) {
                // Поля ввода + примеры данных
                Row {
                    Column(Modifier.padding(10.dp)) {
                        OutlinedTextField(
                            singleLine = true,
                            value = textFieldLengthFOCL,
                            label = {
                                Text("Протяженность ВОЛС - L, км")
                            },
                            isError = (!isPositiveIntegerInputValidator(textFieldLengthFOCL.text))
                                .also { if (!it) updateData() },
                            onValueChange = {
                                textFieldLengthFOCL = it
                            },
                            modifier = modifierTextField
                        )
                        OutlinedTextField(
                            singleLine = true,
                            value = textFieldWorkingWavelength,
                            label = {
                                Text("Рабочая длина волны - λ, мкм")
                            },
                            isError = (!isPositiveDoubleInputValidator(textFieldWorkingWavelength.text))
                                .also { if (!it) updateData() },
                            onValueChange = {
                                textFieldWorkingWavelength = it
                            },
                            modifier = modifierTextField
                        )
                        OutlinedTextField(
                            singleLine = true,
                            value = textFieldAttenuation,
                            label = {
                                Text("Километрическое затухание в оптическом волокне (ОВ), дБ/км")
                            },
                            isError = (!isPositiveDoubleInputValidator(textFieldAttenuation.text))
                                .also { if (!it) updateData() },
                            onValueChange = {
                                textFieldAttenuation = it
                            },
                            modifier = modifierTextField
                        )
                        OutlinedTextField(
                            singleLine = true,
                            value = textFieldPowerOpticalRadiationSourceOutput,
                            label = {
                                Text("Мощность источника оптического излучения - Рвых, дБм")
                            },
                            isError = (!isPositiveIntegerInputValidator(textFieldPowerOpticalRadiationSourceOutput.text))
                                .also { if (!it) updateData() },
                            onValueChange = {
                                textFieldPowerOpticalRadiationSourceOutput = it
                            },
                            modifier = modifierTextField
                        )
                        OutlinedTextField(
                            singleLine = true,
                            value = textFieldTransmissionRateSTM4,
                            label = {
                                Text("Скорость передачи при STM-4 – B0;4, Мбит/с")
                            },
                            isError = (!isPositiveIntegerInputValidator(textFieldTransmissionRateSTM4.text))
                                .also { if (!it) updateData() },
                            onValueChange = {
                                textFieldTransmissionRateSTM4 = it
                            },
                            modifier = modifierTextField
                        )
                        OutlinedTextField(
                            singleLine = true,
                            value = textFieldTransmissionRateSTM64,
                            label = {
                                Text("Скорость передачи при STM-64 - B0;64, Мбит/с")
                            },
                            isError = (!isPositiveIntegerInputValidator(textFieldTransmissionRateSTM64.text))
                                .also { if (!it) updateData() },
                            onValueChange = {
                                textFieldTransmissionRateSTM64 = it
                            },
                            modifier = modifierTextField
                        )
                    }
                    Column(Modifier.padding(10.dp)) {
                        OutlinedTextField(
                            singleLine = true,
                            value = textFieldRefractiveIndexCore,
                            label = {
                                Text("Показатель преломления сердцевины - n")
                            },
                            isError = (!isPositiveDoubleInputValidator(textFieldRefractiveIndexCore.text))
                                .also { if (!it) updateData() },
                            onValueChange = {
                                textFieldRefractiveIndexCore = it
                            },
                            modifier = modifierTextField
                        )
                        OutlinedTextField(
                            singleLine = true,
                            value = textFieldNumberCouplings,
                            label = {
                                Text("Количество муфт (количество сростков) - n_нс")
                            },
                            isError = (!isPositiveIntegerInputValidator(textFieldNumberCouplings.text))
                                .also { if (!it) updateData() },
                            onValueChange = {
                                textFieldNumberCouplings = it
                            },
                            modifier = modifierTextField
                        )
                        OutlinedTextField(
                            singleLine = true,
                            value = textFieldNumberDetachableJoints,
                            label = {
                                Text("Количество разъемных соединений - n_рс")
                            },
                            isError = (!isPositiveIntegerInputValidator(textFieldNumberDetachableJoints.text))
                                .also { if (!it) updateData() },
                            onValueChange = {
                                textFieldNumberDetachableJoints = it
                            },
                            modifier = modifierTextField
                        )
                        OutlinedTextField(
                            singleLine = true,
                            value = textFieldReceiverSensitivity,
                            label = {
                                Text("Чувствительность приемника - Рфпр, дБм")
                            },
                            isError = (!isIntegerInputValidator(textFieldReceiverSensitivity.text))
                                .also { if (!it) updateData() },
                            onValueChange = {
                                textFieldReceiverSensitivity = it
                            },
                            modifier = modifierTextField
                        )
                        OutlinedTextField(
                            singleLine = true,
                            value = textFieldInitialPulseDurationSTM4,
                            label = {
                                Text("Начальная длительность импульса для STM-4 - t40;4, пс")
                            },
                            isError = (!isPositiveIntegerInputValidator(textFieldInitialPulseDurationSTM4.text))
                                .also { if (!it) updateData() },
                            onValueChange = {
                                textFieldInitialPulseDurationSTM4 = it
                            },
                            modifier = modifierTextField
                        )
                        OutlinedTextField(
                            singleLine = true,
                            value = textFieldInitialPulseWidthSTM64,
                            label = {
                                Text("Начальная длительность импульса для STM-64 - t0;64, пс")
                            },
                            isError = (!isPositiveIntegerInputValidator(textFieldInitialPulseWidthSTM64.text))
                                .also { if (!it) updateData() },
                            onValueChange = {
                                textFieldInitialPulseWidthSTM64 = it
                            },
                            modifier = modifierTextField
                        )
                    }
                }
                /////////
                Column(Modifier.width(400.dp)) {
                    Row(Modifier.width(400.dp), horizontalArrangement = Arrangement.Center) {
                        mapExampleData1.forEach { (label, data) ->
                            Button(onClick = {
                                setData(data)
                                updateData()
                            }, modifier = Modifier.padding(2.dp)) {
                                Text(label)
                            }
                        }
                    }

                    Row(Modifier.width(400.dp), horizontalArrangement = Arrangement.Center) {
                        mapExampleData2.forEach { (label, data) ->
                            Button(onClick = {
                                setData(data)
                                updateData()
                            }, modifier = Modifier.padding(2.dp)) {
                                Text(label)
                            }
                        }
                    }

                    Row(Modifier.width(400.dp), horizontalArrangement = Arrangement.Center) {
                        mapExampleData3.forEach { (label, data) ->
                            Button(onClick = {
                                setData(data)
                                updateData()
                            }, modifier = Modifier.padding(2.dp)) {
                                Text(label)
                            }
                        }
                    }

                    Row(Modifier.width(400.dp), horizontalArrangement = Arrangement.Center) {
                        mapExampleData4.forEach { (label, data) ->
                            Button(onClick = {
                                setData(data)
                                updateData()
                            }, modifier = Modifier.padding(2.dp)) {
                                Text(label)
                            }
                        }
                    }

                    Row(Modifier.width(400.dp), horizontalArrangement = Arrangement.Center) {
                        mapExampleData5.forEach { (label, data) ->
                            Button(onClick = {
                                setData(data)
                                updateData()
                            }, modifier = Modifier.padding(2.dp)) {
                                Text(label)
                            }
                        }
                    }

                    Row(Modifier.width(400.dp), horizontalArrangement = Arrangement.Center) {
                        mapExampleData6.forEach { (label, data) ->
                            Button(onClick = {
                                setData(data)
                                updateData()
                            }, modifier = Modifier.padding(2.dp)) {
                                Text(label)
                            }
                        }
                    }
                }
            }
            Row {
                // таблциа констант
                TableWidget(
                    listOf(
                        listOf(
                            Data.lossesFixedJoints.roundToString(),
                            Data.lossesDetachableJoints.roundToString(),
                            Data.exploitationMarginForEquipment.roundToString(),
                            Data.exploitationMarginForCable.roundToString(),
                            "${Data.wavelengthRangeWithZeroDispersionMin} - ${Data.wavelengthRangeWithZeroDispersionMax}",
                            Data.maximumValueOfZeroDispersionSteepnessRangeWithZeroDispersion.roundToString(),
                            Data.polarizationModeDispersionCoefficient.roundToString(),
                        ),
                    ),
                    listOf(
                        "Потери на неразъемных соединениях (сростках): Анс, дБ",
                        "Потери на разъемных соединениях: Арс=, дБ",
                        "Эксплуатационный запас для аппаратуры: Аэза, дБ",
                        "Эксплуатационный запас для кабеля: Аэзк, дБ",
                        "Диапазон длин волн с нулевой дисперсией lambda, нм",
                        "Максимальная величина крутизны нулевой дисперсии: S0, пс/(км*нм^2)",
                        "Коэффициент поляризационной модовой дисперсии: Dpmd, пс/км^(1/2))",
                    ),
                    floatArrayOf(0.14f, 0.14f, 0.14f, 0.14f, 0.14f, 0.14f, 0.14f),
                    width = 1000.dp,
                    height = 500.dp,
                    headerCellHeight = 160.dp,
                    contentCellHeight = 100.dp,
                )
            }
        }
    }
}