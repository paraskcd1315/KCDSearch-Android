package com.paraskcd.kcdsearch.utils.extensionMethods

import com.paraskcd.kcdsearch.constants.GlobalConstants.RESOLUTION_REGEX
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResult

fun SearchResult.getImageUrl(): String? =
    imgSrc ?: thumbnailSrc ?: thumbnail

fun SearchResult.getAspectRatio(): Float {
    val res = resolution ?: return 1f
    val match = RESOLUTION_REGEX.find(res) ?: return 1f
    val (w, h) = match.destructured
    val height = h.toIntOrNull() ?: return 1f
    if (height == 0) return 1f
    val width = w.toIntOrNull() ?: return 1f
    return width.toFloat() / height
}