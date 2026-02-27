package com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.searchBarModifiers(): Modifier =
    this.fillMaxWidth()
        .padding(horizontal = 24.dp)

fun Modifier.skeletonModifiers(): Modifier =
    this.padding(16.dp)