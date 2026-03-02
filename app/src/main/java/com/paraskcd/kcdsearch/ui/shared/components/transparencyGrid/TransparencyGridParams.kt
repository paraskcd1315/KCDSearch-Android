package com.paraskcd.kcdsearch.ui.shared.components.transparencyGrid

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class TransparencyGridParams(
    val modifier: Modifier = Modifier,
    val squareSize: Dp = 8.dp,
    val color1: Color = Color.LightGray.copy(alpha = 0.4f),
    val color2: Color = Color.White.copy(alpha = 0.6f)
)
