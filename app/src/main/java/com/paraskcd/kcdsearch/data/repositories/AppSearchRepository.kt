package com.paraskcd.kcdsearch.data.repositories

import com.paraskcd.kcdsearch.data.api.apps.InstalledAppsApi
import com.paraskcd.kcdsearch.data.api.apps.dataSources.AppResult
import com.paraskcd.kcdsearch.data.api.apps.enums.AppCategory
import com.paraskcd.kcdsearch.data.dtos.AppSearchRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSearchRepository @Inject constructor(
    private val installedAppsApi: InstalledAppsApi
) {
    fun search(appSearchRequestDto: AppSearchRequestDto): List<AppResult> {
        val q = appSearchRequestDto.query.trim().lowercase()
        if (q.isEmpty()) {
            return emptyList()
        }

        val effectiveCategory = appSearchRequestDto.category ?: AppCategory.fromQuery(q)

        return installedAppsApi.getInstalledApps()
            .filter { app ->
                app.label.lowercase().contains(q) ||
                app.packageName.lowercase().contains(q) ||
                app.category == effectiveCategory
            }
    }
}