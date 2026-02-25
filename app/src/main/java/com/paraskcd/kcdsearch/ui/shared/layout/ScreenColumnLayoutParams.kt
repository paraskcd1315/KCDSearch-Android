package com.paraskcd.kcdsearch.ui.shared.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

data class ScreenColumnLayoutParams(
    val modifier: Modifier = Modifier,
    val containerColor: Color = Color.Transparent,
    val gradientColors: List<Color>?,
    val contentAlignment: Alignment = Alignment.Center,
    val verticalArrangement: Arrangement.Vertical = Arrangement.Center
)
