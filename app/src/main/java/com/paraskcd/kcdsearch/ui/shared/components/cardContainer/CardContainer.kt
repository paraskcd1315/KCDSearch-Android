package com.paraskcd.kcdsearch.ui.shared.components.cardContainer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun CardContainer(
    params: CardContainerParams,
    content: @Composable () -> Unit
) {
    val containerColor = params.containerColor ?: MaterialTheme.colorScheme.surfaceContainer

    if (params.onClick != null) {
        Card(
            modifier = params.modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            onClick = params.onClick
        ) {
            content()
        }
    } else {
        Card(
            modifier = params.modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor)
        ) {
            content()
        }
    }
}
