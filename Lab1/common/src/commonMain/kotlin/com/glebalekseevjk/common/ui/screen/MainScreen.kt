package com.glebalekseevjk.common.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.glebalekseevjk.common.DCPDRepository
import com.glebalekseevjk.common.Data
import ui.widget.MainWrapper
import ui.widget.linechart.sourcesignal.SourceSignal
import ui.widget.linechart.sourcesignal.SourceSignalPlot

val modifierTextField = Modifier.width(400.dp)

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

val dcpdRepository = DCPDRepository(Data.exampleData1)


fun transmitterGainInputValidator(text: String): Boolean = text.toIntOrNull() != null && text.toInt() > 0
fun inputPowerInputValidator(text: String): Boolean = text.toIntOrNull() != null
fun channelLengthInputValidator(text: String): Boolean = text.toIntOrNull() != null && text.toInt() > 0
fun attenuationInputValidator(text: String): Boolean = text.toDoubleOrNull() != null && text.toDouble() > 0
fun intermediateAmplifiersInputValidator(text: String): Boolean = text.toIntOrNull() != null && text.toInt() > 0
fun receiverGainInputValidator(text: String): Boolean = text.toIntOrNull() != null && text.toInt() > 0
fun outputPowerInputValidator(text: String): Boolean = text.toIntOrNull() != null
fun interferenceLevelInputValidator(text: String): Boolean = text.toIntOrNull() != null
fun noiseImmunityInputValidator(text: String): Boolean = text.toIntOrNull() != null && text.toInt() >= 0

fun comboInputValidator1(inputPower: Int, transmitterGain: Int, interferenceLevel: Int, noiseImmunity: Int): Boolean =
    inputPower + transmitterGain > interferenceLevel + noiseImmunity

fun comboInputValidator1Wrapper(
    inputPower: String,
    transmitterGain: String,
    interferenceLevel: String,
    noiseImmunity: String
): Boolean {
    inputPower.toIntOrNull() ?: return false
    transmitterGain.toIntOrNull() ?: return false
    interferenceLevel.toIntOrNull() ?: return false
    noiseImmunity.toIntOrNull() ?: return false
    return comboInputValidator1(
        inputPower.toInt(),
        transmitterGain.toInt(),
        interferenceLevel.toInt(),
        noiseImmunity.toInt()
    )
}

@Composable
fun MainScreen() {
    val chartsDataState by dcpdRepository.chartsDataState.collectAsState()
    var textFieldTransmitterGain by remember { mutableStateOf(TextFieldValue()) }
    var textFieldInputPower by remember { mutableStateOf(TextFieldValue()) }
    var textFieldChannelLength by remember { mutableStateOf(TextFieldValue()) }
    var textFieldAttenuation by remember { mutableStateOf(TextFieldValue()) }
    var textFieldIntermediateAmplifiers by remember { mutableStateOf(TextFieldValue()) }
    var textFieldReceiverGain by remember { mutableStateOf(TextFieldValue()) }
    var textFieldOutputPower by remember { mutableStateOf(TextFieldValue()) }
    var textFieldInterferenceLevel by remember { mutableStateOf(TextFieldValue()) }
    var textFieldNoiseImmunity by remember { mutableStateOf(TextFieldValue()) }
    fun setData(data: Data) {
        textFieldTransmitterGain = TextFieldValue(data.transmitterGain.toString())
        textFieldInputPower = TextFieldValue(data.inputPower.toString())
        textFieldChannelLength = TextFieldValue(data.channelLength.toString())
        textFieldAttenuation = TextFieldValue(data.attenuation.toString())
        textFieldIntermediateAmplifiers = TextFieldValue(data.intermediateAmplifiers.toString())
        textFieldReceiverGain = TextFieldValue(data.receiverGain.toString())
        textFieldOutputPower = TextFieldValue(data.outputPower.toString())
        textFieldInterferenceLevel = TextFieldValue(data.interferenceLevel.toString())
        textFieldNoiseImmunity = TextFieldValue(data.noiseImmunity.toString())
    }

    fun updateData() {
        val data = runCatching {
            Data(
                textFieldInputPower.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldTransmitterGain.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldChannelLength.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldAttenuation.text.toDoubleOrNull() ?: throw RuntimeException(),
                textFieldIntermediateAmplifiers.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldReceiverGain.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldInterferenceLevel.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldNoiseImmunity.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldOutputPower.text.toIntOrNull() ?: throw RuntimeException()
            )
        }
        val value = data.getOrNull()
        if (data.isSuccess
            && noiseImmunityInputValidator(textFieldNoiseImmunity.text)
            && channelLengthInputValidator(textFieldChannelLength.text)
            && attenuationInputValidator(textFieldAttenuation.text)
            && transmitterGainInputValidator(textFieldTransmitterGain.text)
            && receiverGainInputValidator(textFieldReceiverGain.text)
            && intermediateAmplifiersInputValidator(textFieldIntermediateAmplifiers.text)
            && comboInputValidator1(
                value!!.inputPower,
                value.transmitterGain,
                value.interferenceLevel,
                value.noiseImmunity
            )
        ) dcpdRepository.setDataState(value)
    }
    setData(Data.exampleData1)
    Column {
        MainWrapper {
            Row {
                Column(Modifier.padding(top = 15.dp)) {
                    OutlinedTextField(
                        singleLine = true,
                        value = textFieldTransmitterGain,
                        label = {
                            Text("Коэффициент усиления передатчика, дБ")
                        },
                        isError = (!(transmitterGainInputValidator(textFieldTransmitterGain.text) && comboInputValidator1Wrapper(
                            textFieldInputPower.text, textFieldTransmitterGain.text,
                            textFieldInterferenceLevel.text, textFieldNoiseImmunity.text
                        )))
                            .also { if (!it) updateData() },
                        onValueChange = {
                            textFieldTransmitterGain = it
                        },
                        modifier = modifierTextField
                    )
                    OutlinedTextField(
                        singleLine = true,
                        value = textFieldInputPower,
                        label = {
                            Text("Входная мощность, дБ")
                        },
                        isError = (!(inputPowerInputValidator(textFieldInputPower.text) && comboInputValidator1Wrapper(
                            textFieldInputPower.text, textFieldTransmitterGain.text,
                            textFieldInterferenceLevel.text, textFieldNoiseImmunity.text
                        )))
                            .also { if (!it) updateData() },
                        onValueChange = {
                            textFieldInputPower = it
                        },
                        modifier = modifierTextField
                    )
                    OutlinedTextField(
                        singleLine = true,
                        value = textFieldChannelLength,
                        label = {
                            Text("Длина канала, км")
                        },
                        isError = (!channelLengthInputValidator(textFieldChannelLength.text)).also { if (!it) updateData() },
                        onValueChange = {
                            textFieldChannelLength = it
                        },
                        modifier = modifierTextField
                    )
                    OutlinedTextField(
                        singleLine = true,
                        value = textFieldAttenuation,
                        label = {
                            Text("Удельное затухание, дБ на км")
                        },
                        isError = (!attenuationInputValidator(textFieldAttenuation.text)).also { if (!it) updateData() },
                        onValueChange = {
                            textFieldAttenuation = it
                        },
                        modifier = modifierTextField
                    )
                    OutlinedTextField(
                        singleLine = true,
                        value = textFieldIntermediateAmplifiers,
                        label = {
                            Text("Промежуточные усилители, дБ")
                        },
                        isError = (!intermediateAmplifiersInputValidator(textFieldIntermediateAmplifiers.text)).also { if (!it) updateData() },
                        onValueChange = {
                            textFieldIntermediateAmplifiers = it
                        },
                        modifier = modifierTextField
                    )
                    OutlinedTextField(
                        singleLine = true,
                        value = textFieldReceiverGain,
                        label = {
                            Text("Коэффициент усиления приемника, дБ")
                        },
                        isError = (!receiverGainInputValidator(textFieldReceiverGain.text)).also { if (!it) updateData() },
                        onValueChange = {
                            textFieldReceiverGain = it
                        },
                        modifier = modifierTextField
                    )
                    OutlinedTextField(
                        singleLine = true,
                        value = textFieldOutputPower,
                        label = {
                            Text("Выходная мощность, дБ")
                        },
                        isError = (!(outputPowerInputValidator(textFieldOutputPower.text)))
                            .also { if (!it) updateData() },
                        onValueChange = {
                            textFieldOutputPower = it
                        },
                        modifier = modifierTextField
                    )
                    OutlinedTextField(
                        singleLine = true,
                        value = textFieldInterferenceLevel,
                        label = {
                            Text("Уровень помехи, дБ")
                        },
                        isError = (!(interferenceLevelInputValidator(textFieldInterferenceLevel.text) && comboInputValidator1Wrapper(
                            textFieldInputPower.text, textFieldTransmitterGain.text,
                            textFieldInterferenceLevel.text, textFieldNoiseImmunity.text
                        )))
                            .also { if (!it) updateData() },
                        onValueChange = {
                            textFieldInterferenceLevel = it
                        },
                        modifier = modifierTextField
                    )
                    OutlinedTextField(
                        singleLine = true,
                        value = textFieldNoiseImmunity,
                        label = {
                            Text("Защищенность от помех, дБ")
                        },
                        isError = (!(noiseImmunityInputValidator(textFieldNoiseImmunity.text) && comboInputValidator1Wrapper(
                            textFieldInputPower.text, textFieldTransmitterGain.text,
                            textFieldInterferenceLevel.text, textFieldNoiseImmunity.text
                        )))
                            .also { if (!it) updateData() },
                        onValueChange = {
                            textFieldNoiseImmunity = it
                        },
                        modifier = modifierTextField
                    )

                    Column(Modifier.width(400.dp)) {
                        Row(modifierTextField, horizontalArrangement = Arrangement.Center) {
                            mapExampleData1.forEach { (label, data) ->
                                Button(onClick = {
                                    setData(data)
                                    updateData()
                                }, modifier = Modifier.padding(5.dp)) {
                                    Text(label)
                                }
                            }
                        }

                        Row(modifierTextField, horizontalArrangement = Arrangement.Center) {
                            mapExampleData2.forEach { (label, data) ->
                                Button(onClick = {
                                    setData(data)
                                    updateData()
                                }, modifier = Modifier.padding(5.dp)) {
                                    Text(label)
                                }
                            }
                        }
                    }

                }
                Column(modifier = Modifier.heightIn(0.dp, 700.dp).widthIn(0.dp, 960.dp).padding(15.dp)) {
                    SourceSignalPlot(
                        SourceSignal(
                            listOf(
                                Pair(chartsDataState.amplifierAxis.axisX, chartsDataState.amplifierAxis.axisY),
                                Pair(chartsDataState.noiseLvlAxis.axisX, chartsDataState.noiseLvlAxis.axisY),
                                Pair(chartsDataState.securityLvlAxis.axisX, chartsDataState.securityLvlAxis.axisY),
                                Pair(chartsDataState.outputLvlAxis.axisX, chartsDataState.outputLvlAxis.axisY),
                            )
                        ),
                        title = "Диаграмма уровней, ${chartsDataState.countS} усилитей"
                    )
                }
            }
        }
    }
}