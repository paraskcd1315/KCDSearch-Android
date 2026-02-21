package com.paraskcd.kcdsearch.data.api.search.dataSources.infobox

data class InfoboxAttribute(
    val label: String,
    val value: String? = null,
    val image: InfoboxImage? = null,
)
