package com.paraskcd.kcdsearch.ui.modules.search.components.searchImageResult

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.ui.modules.search.components.imageResultSkeleton.ImageResultSkeletonParams
import com.paraskcd.kcdsearch.ui.shared.components.skeleton.Skeleton
import com.paraskcd.kcdsearch.ui.shared.components.skeleton.SkeletonParams

private val ImageCornerRadius = 12.dp

@Composable
fun ImageResultSkeleton(params: ImageResultSkeletonParams) {
    Column(
        modifier = params.modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(params.aspectRatio)
                .clip(RoundedCornerShape(ImageCornerRadius))
        ) {
            Skeleton(
                params = SkeletonParams(
                    fillMaxWidth = true,
                    fillMaxHeight = true,
                    clip = RoundedCornerShape(ImageCornerRadius)
                ),
                modifier = Modifier.matchParentSize()
            )
        }
    }
}