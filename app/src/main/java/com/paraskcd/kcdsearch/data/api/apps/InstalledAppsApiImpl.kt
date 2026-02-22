package com.paraskcd.kcdsearch.data.api.apps

import android.content.pm.PackageManager
import com.paraskcd.kcdsearch.data.api.apps.dataSources.AppResult
import com.paraskcd.kcdsearch.data.api.apps.enums.AppCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstalledAppsApiImpl @Inject constructor(
    private val packageManager: PackageManager
): InstalledAppsApi {
    override fun getInstalledApps(): List<AppResult> =
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .mapNotNull { appInfo ->
                packageManager.getLaunchIntentForPackage(appInfo.packageName)?.let {
                    val label = packageManager.getApplicationLabel(appInfo).toString()
                    AppResult(
                        packageName = appInfo.packageName,
                        label,
                        category = AppCategory.fromSystemCategory(appInfo.category) ?: AppCategory.Other,
                    )
                }
            }
}
