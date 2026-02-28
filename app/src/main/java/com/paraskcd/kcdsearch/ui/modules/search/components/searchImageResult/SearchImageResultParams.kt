package com.paraskcd.kcdsearch.ui.modules.search.components.searchImageResult

import androidx.compose.ui.Modifier
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResult

data class SearchImageResultParams(
    val result: SearchResult,
    val modifier: Modifier = Modifier,
    val onClick: (() -> Unit)? = null,
    val onLoadFailed: (() -> Unit)? = null
)
