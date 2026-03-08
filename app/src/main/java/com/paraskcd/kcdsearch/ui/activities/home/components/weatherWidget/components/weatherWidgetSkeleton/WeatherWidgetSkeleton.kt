package com.paraskcd.kcdsearch.ui.activities.home.components.weatherWidget.components.weatherWidgetSkeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.ui.shared.components.cardContainer.CardContainer
import com.paraskcd.kcdsearch.ui.shared.components.cardContainer.CardContainerParams
import com.paraskcd.kcdsearch.ui.shared.components.skeleton.Skeleton
import com.paraskcd.kcdsearch.ui.shared.components.skeleton.SkeletonParams

@Composable
fun WeatherWidgetSkeleton(params: WeatherWidgetSkeletonParams) {
    CardContainer(params = CardContainerParams(modifier = params.modifier)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Skeleton(
                    params = SkeletonParams(size = 48.dp, clip = RoundedCornerShape(8.dp)),
                    modifier = Modifier
                )
                Spacer(Modifier.width(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Skeleton(
                        params = SkeletonParams(width = 72.dp, height = 36.dp, clip = RoundedCornerShape(8.dp)),
                        modifier = Modifier
                    )
                    Skeleton(
                        params = SkeletonParams(width = 100.dp, height = 14.dp, clip = RoundedCornerShape(8.dp)),
                        modifier = Modifier
                    )
                }
            }
            Skeleton(
                params = SkeletonParams(width = 56.dp, height = 12.dp, clip = RoundedCornerShape(8.dp)),
                modifier = Modifier
            )
        }
    }
}