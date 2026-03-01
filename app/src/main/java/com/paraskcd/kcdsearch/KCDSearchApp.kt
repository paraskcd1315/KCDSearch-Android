package com.paraskcd.kcdsearch

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.paraskcd.kcdsearch.data.api.contacts.ContactsApi
import com.paraskcd.kcdsearch.services.SearchService
import com.paraskcd.kcdsearch.workers.CacheCleanupWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class KCDSearchApp: Application(), Configuration.Provider {
    @Inject
    lateinit var searchService: SearchService

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var contactsApi: ContactsApi

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        WorkManager.initialize(this, workManagerConfiguration)

        val cleanupRequest = PeriodicWorkRequestBuilder<CacheCleanupWorker>(24, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "cache_cleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupRequest
        )

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                contactsApi.startObserving()
            }
            override fun onStop(owner: LifecycleOwner) {
                searchService.clearSuggestions()
                contactsApi.stopObserving()
            }
        })
    }
}