package com.paraskcd.kcdsearch.ui.shared.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.scaffoldModifiers(): Modifier =
    this.fillMaxSize()

fun Modifier.backgroundGradientModifiers(colors: List<Color>): Modifier =
    this.fillMaxSize()
        .background(
            brush = Brush.verticalGradient(colors)
        )

fun Modifier.columnModifiers(paddingValues: PaddingValues): Modifier =
    this.fillMaxSize()
        .padding(paddingValues)