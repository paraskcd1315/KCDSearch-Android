package com.paraskcd.kcdsearch.data.api.apps

import com.paraskcd.kcdsearch.data.api.apps.dataSources.AppResult

interface InstalledAppsApi {
    fun getInstalledApps(): List<AppResult>
}