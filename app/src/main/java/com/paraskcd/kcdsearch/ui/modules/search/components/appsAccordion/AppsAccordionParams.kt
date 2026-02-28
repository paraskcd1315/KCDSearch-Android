package com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.paraskcd.kcdsearch.data.api.apps.dataSources.AppResult

data class AppsAccordionParams(
    val appResults: List<AppResult>,
    val modifier: Modifier = Modifier,
    val getAppIcon: (packageName: String) -> ImageBitmap?,
    val launchApp: (packageName: String) -> Unit
)
