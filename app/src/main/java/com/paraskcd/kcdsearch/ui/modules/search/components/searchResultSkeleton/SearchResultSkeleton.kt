package com.paraskcd.kcdsearch.ui.modules.search.components.searchResultSkeleton

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.ui.shared.components.skeleton.Skeleton
import com.paraskcd.kcdsearch.ui.shared.components.skeleton.SkeletonParams

@Composable
fun SearchResultSkeleton(
    params: SearchResultSkeletonParams = SearchResultSkeletonParams()
) {
    Card(
        modifier = params.modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Skeleton(
                params = SkeletonParams(
                    width = params.titleWidth,
                    height = params.titleHeight,
                    clip = RoundedCornerShape(8.dp)
                ),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(12.dp))
            Skeleton(
                params = SkeletonParams(
                    fillMaxWidth = true,
                    height = params.lineHeight,
                    clip = RoundedCornerShape(8.dp)
                ),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(8.dp))
            Skeleton(
                params = SkeletonParams(
                    fillMaxWidth = true,
                    height = params.lineHeight,
                    clip = RoundedCornerShape(8.dp)
                ),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(8.dp))
            Skeleton(
                params = SkeletonParams(
                    width = 120.dp,
                    height = params.lineHeight,
                    clip = RoundedCornerShape(8.dp)
                ),
                modifier = Modifier
            )
        }
    }
}