package com.paraskcd.kcdsearch.constants

object GlobalConstants {
    val RESOLUTION_REGEX = Regex("""(\d+)\s*[x×]\s*(\d+)""", RegexOption.IGNORE_CASE)
    val IMAGE_SKELETON_ASPECT_RATIOS = listOf(
        1.33f, 0.75f, 1.5f, 1f, 0.67f, 1.2f,
        0.9f, 1.6f, 1f, 0.85f, 1.25f, 0.8f,
        1.1f, 1.4f, 0.7f, 1.15f, 0.95f
    )
}