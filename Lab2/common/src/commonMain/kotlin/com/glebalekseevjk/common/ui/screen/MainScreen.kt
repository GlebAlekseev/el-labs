package com.glebalekseevjk.common.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.glebalekseevjk.common.Data
import com.glebalekseevjk.common.MTSRepository
import com.glebalekseevjk.common.ui.widget.data.DeltaData
import com.glebalekseevjk.common.ui.widget.data.GeneralData
import com.glebalekseevjk.common.ui.widget.data.SpecificData
import ui.widget.MainWrapper
import ui.widget.linechart.sourcesignal.CombinedLineChartPlot

val mtsRepository = MTSRepository(Data.exampleData1)

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

fun lengthOfHighwayBetweenPointsInputValidator(text: String): Boolean = text.toIntOrNull() != null && text.toInt() > 0
fun costOfLayingOneKmOfCommunicationLineInputValidator(text: String): Boolean =
    text.toIntOrNull() != null && text.toInt() > 0

fun costPerKmOfPhysicalCircuitInputValidator(text: String): Boolean = text.toIntOrNull() != null && text.toInt() > 0
fun costOfTerminalStationTransmissionSystemEquipmentInputValidator(text: String): Boolean =
    text.toIntOrNull() != null && text.toInt() > 0

@Composable
fun MainScreen() {
    val chartsDataState by mtsRepository.chartsDataState.collectAsState()
    var textFieldLengthOfHighwayBetweenPoints by remember { mutableStateOf(TextFieldValue()) }
    var textFieldCostOfLayingOneKmOfCommunicationLine by remember { mutableStateOf(TextFieldValue()) }
    var textFieldCostPerKmOfPhysicalCircuit by remember { mutableStateOf(TextFieldValue()) }
    var textFieldCostOfTerminalStationTransmissionSystemEquipment by remember { mutableStateOf(TextFieldValue()) }

    fun setData(data: Data) {
        textFieldLengthOfHighwayBetweenPoints = TextFieldValue(data.lengthOfHighwayBetweenPoints.toString())
        textFieldCostOfLayingOneKmOfCommunicationLine =
            TextFieldValue(data.costOfLayingOneKmOfCommunicationLine.toString())
        textFieldCostPerKmOfPhysicalCircuit = TextFieldValue(data.costPerKmOfPhysicalCircuit.toString())
        textFieldCostOfTerminalStationTransmissionSystemEquipment =
            TextFieldValue(data.costOfTerminalStationTransmissionSystemEquipment.toString())
    }

    fun updateData() {
        val data = runCatching {
            Data(
                textFieldLengthOfHighwayBetweenPoints.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldCostOfLayingOneKmOfCommunicationLine.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldCostPerKmOfPhysicalCircuit.text.toIntOrNull() ?: throw RuntimeException(),
                textFieldCostOfTerminalStationTransmissionSystemEquipment.text.toIntOrNull()
                    ?: throw RuntimeException(),
            )
        }
        val value = data.getOrNull()
        if (data.isSuccess
            && lengthOfHighwayBetweenPointsInputValidator(textFieldLengthOfHighwayBetweenPoints.text)
            && costOfLayingOneKmOfCommunicationLineInputValidator(textFieldCostOfLayingOneKmOfCommunicationLine.text)
            && costPerKmOfPhysicalCircuitInputValidator(textFieldCostPerKmOfPhysicalCircuit.text)
            && costOfTerminalStationTransmissionSystemEquipmentInputValidator(
                textFieldCostOfTerminalStationTransmissionSystemEquipment.text
            )
        ) mtsRepository.setDataState(value!!)
    }
    setData(Data.exampleData1)
    Column {
        MainWrapper {
            Row {
                Column(Modifier.padding(top = 15.dp, bottom = 15.dp)) {
                    OutlinedTextField(
                        singleLine = true,
                        value = textFieldLengthOfHighwayBetweenPoints,
                        label = {
                            Text("Расстояние между каналами A и B, км")
                        },
                        isError = (!lengthOfHighwayBetweenPointsInputValidator(textFieldLengthOfHighwayBetweenPoints.text))
                            .also { if (!it) updateData() },
                        onValueChange = {
                            textFieldLengthOfHighwayBetweenPoints = it
                        },
                        modifier = modifierTextField
                    )
                    OutlinedTextField(
                        singleLine = true,
                        value = textFieldCostOfLayingOneKmOfCommunicationLine,
                        label = {
                            Text("Стоимость прокладки физической цепи, руб/км")
                        },
                        isError = (!costOfLayingOneKmOfCommunicationLineInputValidator(
                            textFieldCostOfLayingOneKmOfCommunicationLine.text
                        ))
                            .also { if (!it) updateData() },
                        onValueChange = {
                            textFieldCostOfLayingOneKmOfCommunicationLine = it
                        },
                        modifier = modifierTextField
                    )
                    OutlinedTextField(
                        singleLine = true,
                        value = textFieldCostPerKmOfPhysicalCircuit,
                        label = {
                            Text("Стоимость физической цепи, руб/км")
                        },
                        isError = (!costPerKmOfPhysicalCircuitInputValidator(textFieldCostPerKmOfPhysicalCircuit.text))
                            .also { if (!it) updateData() },
                        onValueChange = {
                            textFieldCostPerKmOfPhysicalCircuit = it
                        },
                        modifier = modifierTextField
                    )
                    OutlinedTextField(
                        singleLine = true,
                        value = textFieldCostOfTerminalStationTransmissionSystemEquipment,
                        label = {
                            Text("Стоимость оборудования систем передачи оконечных станций, руб")
                        },
                        isError = (!costOfTerminalStationTransmissionSystemEquipmentInputValidator(
                            textFieldCostOfTerminalStationTransmissionSystemEquipment.text
                        ))
                            .also { if (!it) updateData() },
                        onValueChange = {
                            textFieldCostOfTerminalStationTransmissionSystemEquipment = it
                        },
                        modifier = modifierTextField
                    )
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
                    }
                }
                Column {
                    Column(modifier = Modifier.heightIn(0.dp, 450.dp).width(500.dp).padding(15.dp)) {
                        CombinedLineChartPlot(
                            GeneralData(
                                listOf(
                                    Pair(
                                        chartsDataState.capitalInvestmentsK1Axis.axisX,
                                        chartsDataState.capitalInvestmentsK1Axis.axisY
                                    ),
                                    Pair(
                                        chartsDataState.capitalInvestmentsK2Axis.axisX,
                                        chartsDataState.capitalInvestmentsK2Axis.axisY
                                    ),
                                )
                            ),
                            title = "Общие капитальные вложения, N: ${chartsDataState.boundaryN}",
                            axisYLabel = "K, руб"
                        )
                    }
                    Column(modifier = Modifier.heightIn(0.dp, 450.dp).width(500.dp).padding(15.dp)) {
                        CombinedLineChartPlot(
                            DeltaData(
                                listOf(
                                    Pair(
                                        chartsDataState.totalEconomyAxis.axisX,
                                        chartsDataState.totalEconomyAxis.axisY
                                    ),
                                ),
                                hasLegend = false
                            ),
                            title = "Общая экономия, N: ${chartsDataState.boundaryN}",
                            axisYLabel = "K, руб"
                        )
                    }
                }
                Column {
                    Column(modifier = Modifier.heightIn(0.dp, 450.dp).width(500.dp).padding(15.dp)) {
                        CombinedLineChartPlot(
                            SpecificData(
                                listOf(
                                    Pair(
                                        chartsDataState.specificCapitalInvestmentsK1Axis.axisX,
                                        chartsDataState.specificCapitalInvestmentsK1Axis.axisY
                                    ),
                                    Pair(
                                        chartsDataState.specificCapitalInvestmentsK2Axis.axisX,
                                        chartsDataState.specificCapitalInvestmentsK2Axis.axisY
                                    ),
                                )
                            ),
                            title = "Удельные капитальные вложения, N: ${chartsDataState.boundaryN}",
                            axisYLabel = "k, руб/км"
                        )
                    }
                    Column(modifier = Modifier.heightIn(0.dp, 450.dp).width(500.dp).padding(15.dp)) {
                        CombinedLineChartPlot(
                            DeltaData(
                                listOf(
                                    Pair(
                                        chartsDataState.specificEconomyAxis.axisX,
                                        chartsDataState.specificEconomyAxis.axisY
                                    ),
                                ),
                                hasLegend = false
                            ),
                            title = "Удельная экономия, N: ${chartsDataState.boundaryN}",
                            axisYLabel = "k, руб/км"
                        )
                    }
                }
            }
        }
    }
}