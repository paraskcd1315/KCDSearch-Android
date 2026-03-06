package com.paraskcd.kcdsearch.ui.modules.home.components.weatherWidget.contents.weatherNoPermissionContent

import androidx.compose.ui.Modifier

data class WeatherNoPermissionContentParams(
    val onRequestPermission: () -> Unit,
    val modifier: Modifier = Modifier
)