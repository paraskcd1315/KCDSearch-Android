package com.paraskcd.kcdsearch.ui.modules.home.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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