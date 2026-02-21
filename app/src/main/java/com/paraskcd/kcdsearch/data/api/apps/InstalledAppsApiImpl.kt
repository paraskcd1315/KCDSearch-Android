package com.paraskcd.kcdsearch.data.api.apps

import android.content.pm.PackageManager
import com.paraskcd.kcdsearch.data.api.apps.dataSources.AppItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstalledAppsApiImpl @Inject constructor(
    private val packageManager: PackageManager
): InstalledAppsApi {
    override fun getInstalledApps(): List<AppItem> =
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .mapNotNull { appInfo ->
                packageManager.getLaunchIntentForPackage(appInfo.packageName)?.let {
                    AppItem(
                        packageName = appInfo.packageName,
                        label = packageManager.getApplicationLabel(appInfo).toString(),
                    )
                }
            }
}