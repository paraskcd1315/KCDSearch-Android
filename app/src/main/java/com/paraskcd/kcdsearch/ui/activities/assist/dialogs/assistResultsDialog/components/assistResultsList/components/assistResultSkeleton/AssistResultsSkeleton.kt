package com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.assistResultSkeleton

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.ui.shared.components.skeleton.Skeleton
import com.paraskcd.kcdsearch.ui.shared.components.skeleton.SkeletonParams

@Composable
fun AssistResultsSkeleton(params: AssistResultsSkeletonParams = AssistResultsSkeletonParams()) {
    repeat(5) { index ->
        ListItem(
            headlineContent = {
                Skeleton(
                    params = SkeletonParams(
                        width = when (index % 3) {
                            0 -> 140.dp
                            1 -> 180.dp
                            else -> 120.dp
                        },
                        height = 16.dp,
                        clip = RoundedCornerShape(8.dp)
                    ),
                    modifier = Modifier
                )
            },
            supportingContent = if (index % 2 == 0) {
                {
                    Skeleton(
                        params = SkeletonParams(
                            width = 100.dp,
                            height = 12.dp,
                            clip = RoundedCornerShape(6.dp)
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            } else null,
            leadingContent = {
                Skeleton(
                    params = SkeletonParams(
                        size = 40.dp,
                        clip = CircleShape
                    ),
                    modifier = Modifier
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            modifier = params.modifier.fillMaxWidth()
        )
    }
}