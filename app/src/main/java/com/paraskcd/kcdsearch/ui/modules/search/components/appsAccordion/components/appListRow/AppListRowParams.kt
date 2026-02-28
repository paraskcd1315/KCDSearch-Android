package com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion.components.appListRow

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.paraskcd.kcdsearch.data.api.apps.dataSources.AppResult

data class AppListRowParams(
    val app: AppResult,
    val modifier: Modifier = Modifier,
    val getAppIcon: (packageName: String) -> ImageBitmap?,
    val launchApp: (packageName: String) -> Unit
)