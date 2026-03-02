package com.paraskcd.kcdsearch.ui.shared.components.transparencyGrid

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

@Composable
fun TransparencyGrid(params: TransparencyGridParams) {
    val squareSizePx = params.squareSize
    Canvas(modifier = params.modifier) {
        val width = size.width
        val height = size.height
        val sizePx = params.squareSize.toPx()
        val xSteps = (width / sizePx).toInt() + 1
        val ySteps = (height / sizePx).toInt() + 1
        for (y in 0 until ySteps) {
            for (x in 0 until xSteps) {
                val color = if ((x + y) % 2 == 0) params.color1 else params.color2
                drawRect(
                    color = color,
                    topLeft = Offset(x * sizePx, y * sizePx),
                    size = Size(sizePx, sizePx)
                )
            }
        }
    }
}