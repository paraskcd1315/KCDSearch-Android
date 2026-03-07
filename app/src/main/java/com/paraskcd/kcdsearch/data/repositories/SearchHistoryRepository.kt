package com.paraskcd.kcdsearch.data.repositories

import com.paraskcd.kcdsearch.data.local.dao.SearchQueryDao
import com.paraskcd.kcdsearch.data.local.entities.SearchQueryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepository @Inject constructor(
    private val searchQueryDao: SearchQueryDao
) {
    fun getRecentQueries(limit: Int = 20): Flow<List<SearchQueryEntity>> =
        searchQueryDao.getAllByUpdatedDesc().map { it.take(limit) }

    suspend fun upsertQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.isNotBlank() && trimmed.length > 2) {
            searchQueryDao.upsert(SearchQueryEntity(query))
        }
    }

    suspend fun getRecentQueriesSync(limit: Int = 20): List<String> =
        searchQueryDao.getRecent(limit).map { it.query }

    suspend fun deleteQuery(query: String) {
        searchQueryDao.deleteByQuery(query)
    }

}