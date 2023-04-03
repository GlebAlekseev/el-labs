package com.glebalekseevjk.common.ui.widget.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    height: Dp = 60.dp
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .height(height)
            .weight(weight, true)
            .padding(8.dp)
    )
}

@Composable
fun TableWidget(
    data: List<List<String>>,
    headerColumn: List<String>,
    columnWeight: FloatArray? = null,
    width: Dp,
    height: Dp,
    headerCellHeight: Dp = 60.dp,
    contentCellHeight: Dp = 60.dp,
    modifier: Modifier = Modifier
) {
    val columnWeight = columnWeight ?: Array(headerColumn.size) { 1 / headerColumn.size.toFloat() }.toFloatArray()
    LazyColumn(modifier.padding(16.dp).heightIn(0.dp, height).width(width).background(Color(237, 242, 244))) {
        item {
            Row(Modifier.background(Color(141, 153, 174))) {
                for (i in headerColumn.indices) {
                    TableCell(text = headerColumn[i], weight = columnWeight[i], headerCellHeight)
                }
            }
        }
        items(data) {
            Row(Modifier) {
                for (i in it.indices) {
                    TableCell(text = it[i], weight = columnWeight[i], contentCellHeight)
                }
            }
        }
    }
}