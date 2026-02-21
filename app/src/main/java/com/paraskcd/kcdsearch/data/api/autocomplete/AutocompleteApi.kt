package com.paraskcd.kcdsearch.data.api.autocomplete

import com.paraskcd.kcdsearch.constants.GlobalConstants
import retrofit2.http.GET
import retrofit2.http.Query

interface AutocompleteApi {
    @GET(GlobalConstants.AUTOCOMPLETE_API_PATH)
    suspend fun autocomplete(
        @Query("q") query: String,
    ): List<String>
}