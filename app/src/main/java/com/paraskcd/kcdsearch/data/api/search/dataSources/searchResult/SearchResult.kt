package com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult

import com.google.gson.annotations.SerializedName

data class SearchResult(
  val url: String,
  val title: String,
  val thumbnail: String,
  @SerializedName("thumbnail_src") val thumbnailSrc: String? = null,
  val template: String? = null,
  val score: Double? = null,
  val content: String,
  val engine: String,
  @SerializedName("parsed_url") val parsedUrl: List<String> = emptyList(),
  @SerializedName("img_src") val imgSrc: String? = null,
  val publishedDate: String? = null,
  val positions: List<Int> = emptyList(),
  val engines: List<String> = emptyList(),
  val priority: String? = null,
  val category: String? = null,
  @SerializedName("iframe_src") val iframeSrc: String? = null,
  val resolution: String? = null,
  @SerializedName("img_format") val imgFormat: String? = null
)
