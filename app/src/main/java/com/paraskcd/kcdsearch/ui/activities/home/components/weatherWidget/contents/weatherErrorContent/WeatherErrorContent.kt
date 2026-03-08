package com.paraskcd.kcdsearch.ui.activities.home.components.weatherWidget.contents.weatherErrorContent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.ui.shared.components.cardContainer.CardContainer
import com.paraskcd.kcdsearch.ui.shared.components.cardContainer.CardContainerParams

@Composable
fun WeatherErrorContent(params: WeatherErrorContentParams) {
    CardContainer(params = CardContainerParams(modifier = params.modifier)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            params.message?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}