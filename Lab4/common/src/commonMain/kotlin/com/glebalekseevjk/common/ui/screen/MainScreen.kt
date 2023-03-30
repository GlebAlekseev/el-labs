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
import com.glebalekseevjk.common.Repository
import com.glebalekseevjk.common.ui.widget.data.GeneralData
import ui.widget.MainWrapper
import ui.widget.linechart.sourcesignal.CombinedLineChartPlot

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

fun rangeInputValidator(text: String): Boolean =
    text.toDoubleOrNull() != null && text.toDouble() > 0

fun routeLengthInputValidator(text: String): Boolean =
    text.toDoubleOrNull() != null && text.toDouble() > 0

@Composable
fun MainScreen() {
    val chartsDataState by repository.resultDataState.collectAsState()
    var textFieldRange by remember { mutableStateOf(TextFieldValue()) }
    var textFieldRouteLength by remember { mutableStateOf(TextFieldValue()) }

    fun setData(data: Data) {
        textFieldRange = TextFieldValue(data.range.toString())
        textFieldRouteLength = TextFieldValue(data.routeLength.toString())
    }

    fun updateData() {
        val data = runCatching {
            Data(
                textFieldRange.text.toDoubleOrNull() ?: throw RuntimeException(),
                textFieldRouteLength.text.toDoubleOrNull() ?: throw RuntimeException(),
            )
        }
        val value = data.getOrNull()
        if (data.isSuccess
            && rangeInputValidator(textFieldRange.text)
            && routeLengthInputValidator(textFieldRouteLength.text)
        ) repository.setDataState(value!!)
    }
    setData(Data.exampleData1)
    Column {
        MainWrapper {

            Column(modifier = Modifier.heightIn(0.dp, 600.dp).width(800.dp).padding(15.dp)) {
                CombinedLineChartPlot(
                    GeneralData(
                        listOf(
                            Pair(
                                chartsDataState.curvatureOfEarth.axisX,
                                chartsDataState.curvatureOfEarth.axisY,
                            ),
                            Pair(
                                chartsDataState.fresnelCriticalZone.axisX,
                                chartsDataState.fresnelCriticalZone.axisY,
                            ),
                            Pair(
                                chartsDataState.landscape.axisX,
                                chartsDataState.landscape.axisY,
                            ),
                            Pair(
                                chartsDataState.lineOfSight.axisX,
                                chartsDataState.lineOfSight.axisY,
                            ),
                        )
                    ),
                    title = "Профиль РРЛ: h1=${String.format("%.2f", chartsDataState.h1)}, h2=${
                        String.format(
                            "%.2f",
                            chartsDataState.h2
                        )
                    }",
                    axisYLabel = "м"
                )
            }

            Column(Modifier.padding(top = 15.dp, bottom = 15.dp)) {
                OutlinedTextField(
                    singleLine = true,
                    value = textFieldRange,
                    label = {
                        Text("Диапазон, МГц")
                    },
                    isError = (!rangeInputValidator(textFieldRange.text))
                        .also { if (!it) updateData() },
                    onValueChange = {
                        textFieldRange = it
                    },
                    modifier = modifierTextField
                )
                OutlinedTextField(
                    singleLine = true,
                    value = textFieldRouteLength,
                    label = {
                        Text("Длина трассы, км")
                    },
                    isError = (!routeLengthInputValidator(textFieldRouteLength.text))
                        .also { if (!it) updateData() },
                    onValueChange = {
                        textFieldRouteLength = it
                    },
                    modifier = modifierTextField
                )
            }

            Column {
                Button({
                    Data.refreshLandscape()
                    updateData()
                }) {
                    Text("Сменить ландшафт")
                }
            }

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
    }
}