package com.paraskcd.kcdsearch.data.api.search.dataSources.infobox

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.paraskcd.kcdsearch.data.api.search.adapters.PositionsDeserializer

data class Infobox(
  val infobox: String? = null,
  val attributes: List<InfoboxAttribute> = emptyList(),
  val urls: List<InfoboxUrl> = emptyList(),
  val engine: String? = null,
  val url: String? = null,
  val template: String? = null,
  val parsedUrl: List<String>? = null,
  val title: String? = null,
  val content: String? = null,
  @SerializedName("img_src")
  val imgSrc: String? = null,
  val thumbnail: String? = null,
  val priority: String? = null,
  val engines: List<String> = emptyList(),
  @SerializedName("positions")
  @JsonAdapter(PositionsDeserializer::class)
  val positions: List<Int> = emptyList(),
  val score: Double? = null,
  val category: String? = null,
  val publishedDate: String? = null,
)
