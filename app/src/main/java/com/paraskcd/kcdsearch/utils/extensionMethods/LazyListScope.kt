package com.paraskcd.kcdsearch.utils.extensionMethods

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun <T> LazyListScope.segmentedListItems(
    items: List<T>,
    bottomPadding: Dp = 4.dp,
    horizontalPadding: Dp = 24.dp,
    cornerSize: Dp = 16.dp,
    lessRoundedCornerSize: Dp = 8.dp,
    key: (index: Int, T) -> Any,
    content: @Composable (index: Int, item: T, isFirst: Boolean, isLast: Boolean) -> Unit
) {
    itemsIndexed(
        items,
        key = { index, item -> key(index, item) }
    ) { index, item ->
        val isFirst = index == 0
        val isLast = index == items.size - 1
        val shape: Shape = when {
            isFirst && isLast -> RoundedCornerShape(cornerSize)
            isFirst -> RoundedCornerShape(
                topStart = cornerSize,
                topEnd = cornerSize,
                bottomStart = lessRoundedCornerSize,
                bottomEnd = lessRoundedCornerSize
            )
            isLast -> RoundedCornerShape(
                bottomStart = cornerSize,
                bottomEnd = cornerSize,
                topStart = lessRoundedCornerSize,
                topEnd = lessRoundedCornerSize
            )
            else -> RoundedCornerShape(lessRoundedCornerSize)
        }
        Box(
            modifier = Modifier
                .fillParentMaxWidth()
                .padding(start = horizontalPadding/2, end = horizontalPadding/2, bottom = bottomPadding)
                .clip(shape)
        ) {
            content(index, item, isFirst, isLast)
        }
    }
}