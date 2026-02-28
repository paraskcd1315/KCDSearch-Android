package com.paraskcd.kcdsearch.utils.extensionMethods

import com.paraskcd.kcdsearch.ui.modules.search.enums.SearchCategory

fun SearchCategory.toApiString(): String = when (this) {
    SearchCategory.General -> "general"
    SearchCategory.Images -> "images"
    SearchCategory.Videos -> "videos"
    SearchCategory.News -> "news"
    SearchCategory.Map -> "map"
    SearchCategory.Music -> "music"
    SearchCategory.Science -> "science"
}