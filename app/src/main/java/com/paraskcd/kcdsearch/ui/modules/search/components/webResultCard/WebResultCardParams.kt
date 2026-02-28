package com.paraskcd.kcdsearch.ui.modules.search.components.webResultCard

import androidx.compose.ui.Modifier
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResult

data class WebResultCardParams(
    val result: SearchResult,
    val modifier: Modifier,
    val onUrlClick: (String) -> Unit
)
