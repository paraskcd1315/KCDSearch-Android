package com.paraskcd.kcdsearch.ui.activities.search.components.searchTabs.contents.imageContent

import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResult

data class ImagesContentParams(
    val isLoading: Boolean,
    val query: String,
    val displayableImages: List<SearchResult>,
    val hasMorePages: Boolean,
    val onImageSelected: (SearchResult) -> Unit,
    val onImageLoadFailed: (String) -> Unit,
)