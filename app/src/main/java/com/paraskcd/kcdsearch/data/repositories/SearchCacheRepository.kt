package com.paraskcd.kcdsearch.data.repositories

import com.google.gson.Gson
import com.paraskcd.kcdsearch.data.api.logging.CacheDeletionLogger
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResultResponse
import com.paraskcd.kcdsearch.data.local.dao.SearchCacheDao
import com.paraskcd.kcdsearch.data.local.entities.SearchCacheEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchCacheRepository @Inject constructor(
    private val searchCacheDao: SearchCacheDao,
    private val gson: Gson,
    private val cacheDeletionLogger: CacheDeletionLogger,
) {
    private val cacheValidityMs = 24 * 60 * 60 * 1000L

    suspend fun getCachedSearchResults(query: String, categories: String?): SearchResultResponse? {
        val key = cacheKey(SearchCacheEntity.TYPE_SEARCH, query, categories ?: "")
        val minValid = System.currentTimeMillis() - cacheValidityMs

        return searchCacheDao.getValid(key, minValid)?.let {
            runCatching {
                gson.fromJson(it.jsonData, SearchResultResponse::class.java)
            }.getOrNull()
        }
    }

    suspend fun cacheSearchResults(query: String, categories: String?, response: SearchResultResponse) {
        val key = cacheKey(SearchCacheEntity.TYPE_SEARCH, query, categories ?: "")
        searchCacheDao.insert(
            SearchCacheEntity(
                cacheKey = key,
                query = query,
                jsonData = gson.toJson(response),
                cacheType = SearchCacheEntity.TYPE_SEARCH
            )
        )
    }

    suspend fun getCachedSuggestions(query: String): List<String>? {
        val key = cacheKey(SearchCacheEntity.TYPE_SUGGESTIONS, query, "")
        val minValid = System.currentTimeMillis() - cacheValidityMs

        return searchCacheDao.getValid(key, minValid)?.let {
            runCatching {
                gson.fromJson(it.jsonData, Array<String>::class.java).toList()
            }.getOrNull()
        }
    }

    suspend fun cacheSuggestions(query: String, suggestions: List<String>) {
        val key = cacheKey(SearchCacheEntity.TYPE_SUGGESTIONS, query, "")
        searchCacheDao.insert(
            SearchCacheEntity(
                cacheKey = key,
                query = query,
                jsonData = gson.toJson(suggestions),
                cacheType = SearchCacheEntity.TYPE_SUGGESTIONS
            )
        )
    }

    suspend fun deleteExpiredCache() {
        try {
            val expireTime = System.currentTimeMillis() - cacheValidityMs
            val deletedCount = searchCacheDao.deleteExpired(expireTime)
            cacheDeletionLogger.logDeletion("search_cache", deletedCount)
        } catch (e: Exception) {
            cacheDeletionLogger.logFailure("search_cache", e.message ?: e.toString())
            throw e
        } finally {
            try {
                cacheDeletionLogger.deleteOldLogs()
            } catch (e: Exception) {
                cacheDeletionLogger.logFailure("delete_old_logs", e.message ?: e.toString())
            }
        }
    }

    private fun cacheKey(type: String, query: String, extra: String): String =
        "${type}_${query.lowercase().trim()}_$extra".hashCode().toString()
}