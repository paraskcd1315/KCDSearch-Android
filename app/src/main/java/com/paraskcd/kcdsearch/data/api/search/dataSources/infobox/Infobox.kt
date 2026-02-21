package com.paraskcd.kcdsearch.data.api.search.dataSources.infobox

import com.google.gson.annotations.SerializedName

data class Infobox(
  val infobox: String,
  val attributes: List<InfoboxAttribute> = emptyList(),
  val urls: List<InfoboxUrl> = emptyList(),
  val engine: String,
  val url: String? = null,
  val template: String? = null,
  val parsedUrl: List<String>? = null,
  val title: String,
  val content: String,
  @SerializedName("img_src") val imgSrc: String,
  val thumbnail: String,
  val priority: String? = null,
  val engines: List<String> = emptyList(),
  val positions: List<Int> = emptyList(),
  val score: Double? = null,
  val category: String? = null,
  val publishedDate: String? = null,
)
