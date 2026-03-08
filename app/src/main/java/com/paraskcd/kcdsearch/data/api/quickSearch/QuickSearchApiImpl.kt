package com.paraskcd.kcdsearch.data.api.quickSearch

import android.content.pm.PackageManager
import com.paraskcd.kcdsearch.data.api.quickSearch.dataSources.QuickSearchResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuickSearchApiImpl @Inject constructor(
    private val packageManager: PackageManager
) : QuickSearchApi {
    private data class SearchDef(
        val packageName: String,
        val titleBuilder: (String) -> String,
        val subtitle: String
    )

    private val searchDefs = listOf(
        SearchDef(
            packageName = "com.google.android.googlequicksearchbox",
            titleBuilder = { q -> "Search \"$q\" on Google" },
            subtitle = "Google"
        ),
        SearchDef(
            packageName = "com.google.android.youtube",
            titleBuilder = { q -> "Search \"$q\" on YouTube" },
            subtitle = "YouTube"
        ),
        SearchDef(
            packageName = "com.android.vending",
            titleBuilder = { q -> "Search \"$q\" on the Play Store" },
            subtitle = "Play Store"
        ),
        SearchDef(
            packageName = "com.google.android.apps.maps",
            titleBuilder = { q -> "Search \"$q\" on Maps" },
            subtitle = "Google Maps"
        )
    )

    override fun getSearchActions(query: String): List<QuickSearchResult> {
        if (query.isBlank()) return emptyList()

        return searchDefs
            .filter { isInstalled(it.packageName) }
            .map { def ->
                QuickSearchResult(
                    query = query,
                    title = def.titleBuilder(query),
                    subtitle = def.subtitle,
                    packageName = def.packageName
                )
            }
    }

    override fun isInstalled(packageName: String): Boolean = try {
        packageManager.getApplicationInfo(packageName, 0); true
    } catch (_: Exception) {
        false
    }
}