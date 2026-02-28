package com.paraskcd.kcdsearch.ui.modules.search.components.searchResultSkeleton

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class SearchResultSkeletonParams(
    val modifier: Modifier = Modifier,
    val titleWidth: Dp = 180.dp,
    val titleHeight: Dp = 20.dp,
    val lineHeight: Dp = 16.dp,
    val lineWidth: Dp = 280.dp,
)
