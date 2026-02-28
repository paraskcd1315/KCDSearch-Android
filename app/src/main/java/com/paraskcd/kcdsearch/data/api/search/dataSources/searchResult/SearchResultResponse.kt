package com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.paraskcd.kcdsearch.data.api.search.adapters.AnswersDeserializer
import com.paraskcd.kcdsearch.data.api.search.adapters.UnresponsiveEnginesDeserializer
import com.paraskcd.kcdsearch.data.api.search.dataSources.infobox.Infobox

data class SearchResultResponse(
    val query: String,
    @SerializedName("number_of_results")
    val numberOfResults: Int,
    val results: List<SearchResult> = emptyList(),
    @JsonAdapter(AnswersDeserializer::class)
    val answers: List<String>,
    val corrections: List<String>,
    val infoboxes: List<Infobox>,
    val suggestions: List<String>,
    @SerializedName("unresponsive_engines")
    @JsonAdapter(UnresponsiveEnginesDeserializer::class)
    val unresponsiveEngines: List<String>? = null
)
