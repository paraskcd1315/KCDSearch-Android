package com.paraskcd.kcdsearch.ui.activities.home.components.weatherWidget.contents.weatherContent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paraskcd.kcdsearch.ui.shared.components.cardContainer.CardContainer
import com.paraskcd.kcdsearch.ui.shared.components.cardContainer.CardContainerParams
import com.paraskcd.kcdsearch.utils.extensionMethods.toTempString
import com.paraskcd.kcdsearch.utils.extensionMethods.toWeatherDescription
import com.paraskcd.kcdsearch.utils.extensionMethods.toWeatherIcon

@Composable
fun WeatherContent(params: WeatherContentParams) {
    val currently = params.forecast?.currently
    val icon = currently?.icon.toWeatherIcon()
    val description = currently?.summary ?: currently?.icon.toWeatherDescription()
    val temperature = currently?.temperature.toTempString(params.useFahrenheit)
    val cityName = params.cityName ?: "Unknown"

    CardContainer(params = CardContainerParams(modifier = params.modifier)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = description,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cityName,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = temperature,
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 36.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}