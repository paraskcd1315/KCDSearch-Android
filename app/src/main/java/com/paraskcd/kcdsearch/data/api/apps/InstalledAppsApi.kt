package com.paraskcd.kcdsearch.data.api.apps

import com.paraskcd.kcdsearch.data.api.apps.dataSources.AppItem

interface InstalledAppsApi {
    fun getInstalledApps(): List<AppItem>
}