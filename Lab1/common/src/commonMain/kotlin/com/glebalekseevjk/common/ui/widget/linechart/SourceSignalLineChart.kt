package ui.widget.linechart.sourcesignal

import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.DefaultPoint
import io.github.koalaplot.core.line.LineChart
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.xychart.*
import io.github.koalaplot.sample.*
import kotlin.math.absoluteValue


data class SourceSignal(
    val axis: List<Pair<List<Double>, List<Double>>>
) {
    var distance = listOf(
        "Сигнал" to mutableListOf<Double>(),
        "Канал защищенности" to mutableListOf<Double>(),
        "Уровень помех" to mutableListOf<Double>(),
        "Выходная мощность" to mutableListOf<Double>(),
    )
    val power = listOf(
        "Сигнал" to mutableListOf<Double>(),
        "Канал защищенности" to mutableListOf<Double>(),
        "Уровень помех" to mutableListOf<Double>(),
        "Выходная мощность" to mutableListOf<Double>(),
    )

    init {
        axis.forEachIndexed { index, (axisX, axisY) ->
            axisX.forEach {
                distance[index].second.add(it)
            }
            axisY.forEach {
                power[index].second.add(it)
            }
        }
    }

    //
//    val minY = power.map { it.second.minOrNull() ?: Double.MAX_VALUE }.minOrNull() ?: 0.0
//    val maxY = power.map { it.second.maxOrNull() ?: Double.MIN_VALUE }.maxOrNull() ?: 0.0
//
//    val minX = distance.map { it.second.minOrNull() ?: Double.MAX_VALUE }.minOrNull() ?: 0.0
//    val maxX = distance.map { it.second.maxOrNull() ?: Double.MIN_VALUE }.maxOrNull() ?: 0.0
    val minY = power.map { it.second.minOrNull() }.filter { it != null }.minOfOrNull { it!! }
    val maxY = power.map { it.second.maxOrNull() }.filter { it != null }.maxOfOrNull { it!! }

    val minX = distance.map { it.second.minOrNull() }.filter { it != null }.minOfOrNull { it!! }
    val maxX = distance.map { it.second.maxOrNull() }.filter { it != null }.maxOfOrNull { it!! }

    val colorMap = buildMap {
        val colors = generateHueColorPalette(power.size)
        var i = 0
        power.forEach {
            put(it.first, colors[i++])
        }
    }

    val isValid: Boolean
        get() = minX != null && maxX != null && minY != null && maxY != null
}


@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
@Suppress("MagicNumber")
fun SourceSignalPlot(sourceSignal: SourceSignal, modifier: Modifier = Modifier, title: String = "Диаграмма уровней") {
//    val title = "Диаграмма уровней"
    ChartLayout(
        modifier = modifier,
        title = { ChartTitle(title) },
        legend = { Legend(sourceSignal) },
        legendLocation = LegendLocation.BOTTOM
    ) {
        XYChart(
            xAxisModel = LinearAxisModel(
                if (sourceSignal.isValid) (sourceSignal.minX!! - (sourceSignal.maxX!! - sourceSignal.minX).absoluteValue * 0.1
                        ..sourceSignal.maxX + (sourceSignal.maxX - sourceSignal.minX).absoluteValue * 0.1)
                else (0.0..1.0),
                minimumMajorTickSpacing = 1.dp,
                minimumMajorTickIncrement = 1.0,
                minorTickCount = 3
            ),
            yAxisModel = LinearAxisModel(
                if (sourceSignal.isValid) (sourceSignal.minY!! - (sourceSignal.maxY!! - sourceSignal.minY).absoluteValue * 0.1
                        ..sourceSignal.maxY + (sourceSignal.maxY - sourceSignal.minY).absoluteValue * 0.1)
                else (0.0..1.0),
                minimumMajorTickSpacing = 50.dp,
                minimumMajorTickIncrement = 1.0,
                minorTickCount = 3
            ),
            xAxisLabels = {
                AxisLabel(it.toInt().toString(), Modifier.padding(top = 2.dp))
            },
            xAxisTitle = { AxisTitle("L, км") },
            yAxisLabels = {
                AxisLabel(it.toInt().toString(), Modifier.absolutePadding(right = 2.dp))
            },
            yAxisTitle = {
                AxisTitle(
                    "P, дБ",
                    modifier = Modifier.rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                        .padding(bottom = padding)
                )
            }
        ) {
            sourceSignal.power.forEachIndexed { index, (label, powerList) ->
                chart(
                    sourceSignal,
                    label,
                    powerList.mapIndexed { i: Int, d: Double ->
                        DefaultPoint(sourceSignal.distance[index].second[i], d)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun XYChartScope<Double, Double>.chart(
    sourceSignal: SourceSignal,
    caseName: String,
    data: List<DefaultPoint<Double, Double>>,
) {
    LineChart(
        data = data,
        isSpline = false,
        splineMinWeight = 100.0,
        lineStyle = LineStyle(
            brush = SolidColor(sourceSignal.colorMap[caseName] ?: Color.Black),
            strokeWidth = 6.dp,
            alpha = 0.6f
        ),
        symbol = { point ->
            Symbol(
                size = 10.dp,
                shape = RoundedCornerShape(50),
                fillBrush = SolidColor(sourceSignal.colorMap[caseName] ?: Color.Black),
                modifier = Modifier.then(
                    Modifier.hoverableElement {
                        HoverSurface { Text(point.y.toString()) }
                    }
                ),
            )
        }
    )
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun Legend(sourceSignal: SourceSignal) {
    val cases = sourceSignal.power.map { it.first }.sorted()

    Surface(elevation = 2.dp) {
        FlowLegend(
            itemCount = cases.size,
            symbol = { i ->
                Symbol(
                    modifier = Modifier.size(padding),
                    fillBrush = SolidColor(sourceSignal.colorMap[cases[i]] ?: Color.Black)
                )
            },
            label = { i ->
                Text(cases[i])
            },
            modifier = paddingMod
        )
    }
}
