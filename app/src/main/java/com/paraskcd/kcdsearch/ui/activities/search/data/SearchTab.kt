package com.paraskcd.kcdsearch.ui.activities.search.data

import androidx.compose.ui.graphics.vector.ImageVector
import com.paraskcd.kcdsearch.ui.activities.search.enums.SearchCategory

data class SearchTab(
    val label: String,
    val value: SearchCategory,
    val icon: ImageVector
)
