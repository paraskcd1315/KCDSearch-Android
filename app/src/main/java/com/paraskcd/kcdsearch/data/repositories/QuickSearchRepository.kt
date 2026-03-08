package com.paraskcd.kcdsearch.data.repositories

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.paraskcd.kcdsearch.data.api.quickSearch.QuickSearchApi
import com.paraskcd.kcdsearch.data.api.quickSearch.dataSources.QuickSearchResult
import com.paraskcd.kcdsearch.ui.activities.search.SearchActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuickSearchRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val quickSearchApi: QuickSearchApi
) {
    fun search(query: String): List<QuickSearchResult> {
        if (query.isBlank()) return emptyList()

        val kcdSearchResult = QuickSearchResult(
            query = query,
            title = "Search \"$query\" on KCD Search",
            subtitle = "KCD Search",
            packageName = context.packageName
        )

        return listOf(kcdSearchResult) + quickSearchApi.getSearchActions(query)
    }

    fun buildIntent(result: QuickSearchResult): Intent {
        return when (result.packageName) {
            context.packageName -> Intent(context, SearchActivity::class.java)
            "com.google.android.googlequicksearchbox" -> Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(SearchManager.QUERY, result.query)
                setPackage(result.packageName)
            }
            "com.google.android.youtube" -> Intent(Intent.ACTION_VIEW).apply {
                data = "https://www.youtube.com/results?search_query=${Uri.encode(result.query)}".toUri()
                setPackage(result.packageName)
            }
            "com.android.vending" -> Intent(Intent.ACTION_VIEW).apply {
                data = "https://play.google.com/store/search?q=${Uri.encode(result.query)}&c=apps".toUri()
                setPackage(result.packageName)
            }
            "com.google.android.apps.maps" -> Intent(Intent.ACTION_VIEW).apply {
                data = "geo:0,0?q=${Uri.encode(result.query)}".toUri()
                setPackage(result.packageName)
            }
            else -> Intent(context, SearchActivity::class.java)
        }
    }
}
