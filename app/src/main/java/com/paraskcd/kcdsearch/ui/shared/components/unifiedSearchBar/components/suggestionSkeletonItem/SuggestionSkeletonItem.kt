package com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.suggestionSkeletonItem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.ui.shared.components.skeleton.Skeleton
import com.paraskcd.kcdsearch.ui.shared.components.skeleton.SkeletonParams

@Composable
fun SuggestionSkeletonItem(params: SuggestionSkeletonItemParams) {
    Row(
        modifier = params.modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Skeleton(
            params = SkeletonParams(
                size = 40.dp,
                clip = CircleShape
            ),
            modifier = Modifier.size(40.dp)
        )
        Skeleton(
            params = SkeletonParams(
                height = 20.dp,
                fillMaxWidth = false,
                clip = RoundedCornerShape(8.dp)
            ),
            modifier = Modifier.fillMaxWidth(params.textWidthFraction)
        )
    }
}