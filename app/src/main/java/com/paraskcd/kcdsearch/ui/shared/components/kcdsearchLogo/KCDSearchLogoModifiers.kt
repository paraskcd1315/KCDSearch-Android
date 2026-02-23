package com.paraskcd.kcdsearch.ui.shared.components.kcdsearchLogo

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.kcdSearchLogoModifiers(height: Dp): Modifier =
    this.fillMaxWidth()
        .height(height)
        .padding(16.dp)