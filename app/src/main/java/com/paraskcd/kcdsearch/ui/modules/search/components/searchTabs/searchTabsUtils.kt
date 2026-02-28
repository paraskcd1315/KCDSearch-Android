package com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs

import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.AcademicCap
import com.composables.icons.heroicons.outline.MagnifyingGlass
import com.composables.icons.heroicons.outline.MusicalNote
import com.composables.icons.heroicons.outline.Newspaper
import com.composables.icons.heroicons.outline.Photo
import com.composables.icons.heroicons.outline.VideoCamera
import com.paraskcd.kcdsearch.ui.modules.search.data.SearchTab
import com.paraskcd.kcdsearch.ui.modules.search.enums.SearchCategory

val searchTabs: List<SearchTab> = listOf(
    SearchTab("General", SearchCategory.General, Heroicons.Outline.MagnifyingGlass),
    SearchTab("Images", SearchCategory.Images, Heroicons.Outline.Photo),
    SearchTab("Videos", SearchCategory.Videos, Heroicons.Outline.VideoCamera),
    SearchTab("News", SearchCategory.News, Heroicons.Outline.Newspaper),
    SearchTab("Music", SearchCategory.Music, Heroicons.Outline.MusicalNote),
    SearchTab("Science", SearchCategory.Science, Heroicons.Outline.AcademicCap),
)