package com.paraskcd.kcdsearch.data.api.search

import com.paraskcd.kcdsearch.constants.GlobalConstants
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResultResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET(GlobalConstants.SEARCH_API_PATH)
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("pageno") pageno: Int = 1,
        @Query("language") language: String? = null,
        @Query("categories") categories: String? = null,
        @Query("engines") engines: String? = null,
        @Query("safesearch") safesearch: Int? = null,
    ): SearchResultResponse
}