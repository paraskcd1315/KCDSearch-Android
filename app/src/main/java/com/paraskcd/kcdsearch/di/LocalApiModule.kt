package com.paraskcd.kcdsearch.di

import android.content.Context
import android.content.pm.PackageManager
import com.paraskcd.kcdsearch.data.api.apps.InstalledAppsApi
import com.paraskcd.kcdsearch.data.api.apps.InstalledAppsApiImpl
import com.paraskcd.kcdsearch.data.api.contacts.ContactsApi
import com.paraskcd.kcdsearch.data.api.contacts.ContactsApiImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalApiModule {
    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager =
        context.packageManager

    @Provides
    @Singleton
    fun provideInstalledAppsApi(impl: InstalledAppsApiImpl): InstalledAppsApi = impl

    @Provides
    @Singleton
    fun provideContactsApi(impl: ContactsApiImpl): ContactsApi = impl
}