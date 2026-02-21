package com.paraskcd.kcdsearch.data.api.autocomplete

import com.paraskcd.kcdsearch.constants.SearchApiConstants
import retrofit2.http.GET
import retrofit2.http.Query

interface AutocompleteApi {
    @GET(SearchApiConstants.AUTOCOMPLETE_API_PATH)
    suspend fun autocomplete(
        @Query("q") query: String,
    ): List<Any>
}