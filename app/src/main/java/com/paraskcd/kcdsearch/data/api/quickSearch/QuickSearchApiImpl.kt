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

    private val youtubePackages = listOf(
        "com.morphe.android.youtube",
        "app.revanced.android.youtube",
        "com.google.android.youtube"
    )

    private val fixedDefs = listOf(
        SearchDef(
            packageName = "com.google.android.googlequicksearchbox",
            titleBuilder = { q -> "Search \"$q\" on Google" },
            subtitle = "Google"
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

        val results = mutableListOf<QuickSearchResult>()

        // YouTube — pick first installed variant
        val ytPackage = youtubePackages.firstOrNull { isInstalled(it) }
        if (ytPackage != null) {
            results.add(
                QuickSearchResult(
                    query = query,
                    title = "Search \"$query\" on YouTube",
                    subtitle = "YouTube",
                    packageName = ytPackage
                )
            )
        }

        // Fixed defs
        fixedDefs
            .filter { isInstalled(it.packageName) }
            .mapTo(results) { def ->
                QuickSearchResult(
                    query = query,
                    title = def.titleBuilder(query),
                    subtitle = def.subtitle,
                    packageName = def.packageName
                )
            }

        return results
    }

    override fun isInstalled(packageName: String): Boolean = try {
        packageManager.getApplicationInfo(packageName, 0); true
    } catch (_: Exception) {
        false
    }
}