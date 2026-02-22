package com.paraskcd.kcdsearch.data.api.apps.enums

import android.content.pm.ApplicationInfo

enum class AppCategory(val keywords: List<String>, val systemCategories: List<Int>) {
    Social(
        keywords = listOf("social", "chat", "message", "messenger", "communication", "contact", "friend", "share"),
        systemCategories = listOf(ApplicationInfo.CATEGORY_SOCIAL),
    ),
    Games(
        keywords = listOf("game", "gaming", "play"),
        systemCategories = listOf(ApplicationInfo.CATEGORY_GAME),
    ),
    Productivity(
        keywords = listOf("notes", "todo", "task", "calendar", "email", "office", "document", "productivity", "work"),
        systemCategories = listOf(ApplicationInfo.CATEGORY_PRODUCTIVITY),
    ),
    Entertainment(
        keywords = listOf("music", "video", "movie", "streaming", "entertainment", "audio", "media", "player"),
        systemCategories = listOf(
            ApplicationInfo.CATEGORY_AUDIO,
            ApplicationInfo.CATEGORY_VIDEO,
            ApplicationInfo.CATEGORY_IMAGE,
        ),
    ),
    News(
        keywords = listOf("news", "article", "blog", "rss", "feed", "reader", "magazine"),
        systemCategories = listOf(ApplicationInfo.CATEGORY_NEWS),
    ),
    Tools(
        keywords = listOf("tool", "utility", "file", "calculator", "weather", "clock", "camera", "photo", "map", "navigation"),
        systemCategories = listOf(ApplicationInfo.CATEGORY_MAPS),
    ),
    Other(
        keywords = emptyList(),
        systemCategories = listOf(ApplicationInfo.CATEGORY_UNDEFINED),
    );

    companion object {
        fun fromSystemCategory(systemCategory: Int): AppCategory? =
            entries.find { systemCategory in it.systemCategories }

        fun fromQuery(query: String): AppCategory? {
            val q = query.trim().lowercase()
            if (q.isEmpty()) {
                return null
            }

            return entries
                .filter { it != Other && it.keywords.isNotEmpty() }
                .find { category -> category.keywords.any { q.contains(it) } }
        }
    }
}