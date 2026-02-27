package com.paraskcd.kcdsearch.ui.shared.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
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

@Composable
fun Modifier.columnModifiers(paddingValues: PaddingValues): Modifier =
    this.fillMaxSize()
        .padding(paddingValues)
        .windowInsetsPadding(WindowInsets.statusBars)