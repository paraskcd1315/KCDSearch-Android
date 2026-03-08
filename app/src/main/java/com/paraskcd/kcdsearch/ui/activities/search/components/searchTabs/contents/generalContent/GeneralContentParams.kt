package com.paraskcd.kcdsearch.ui.activities.search.components.searchTabs.contents.generalContent

import android.content.Context
import com.paraskcd.kcdsearch.data.api.apps.dataSources.AppResult
import com.paraskcd.kcdsearch.data.api.contacts.dataSources.ContactResult
import com.paraskcd.kcdsearch.data.api.search.dataSources.infobox.Infobox
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResult
import com.paraskcd.kcdsearch.ui.activities.search.SearchViewModel

data class GeneralContentParams(
    val isLoading: Boolean,
    val query: String,
    val webResults: List<SearchResult>,
    val infoboxes: List<Infobox>,
    val appResults: List<AppResult>,
    val contactResults: List<ContactResult>,
    val hasMorePages: Boolean,
    val viewModel: SearchViewModel,
    val context: Context,
)
