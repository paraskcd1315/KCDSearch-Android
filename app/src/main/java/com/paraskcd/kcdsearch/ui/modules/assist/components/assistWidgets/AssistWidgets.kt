package com.paraskcd.kcdsearch.ui.modules.assist.components.assistWidgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paraskcd.kcdsearch.constants.AssistConstants
import com.paraskcd.kcdsearch.utils.extensionMethods.toTempString
import com.paraskcd.kcdsearch.utils.extensionMethods.toWeatherDescription
import com.paraskcd.kcdsearch.utils.extensionMethods.toWeatherIcon

private val CORNER = RoundedCornerShape(24.dp)

@Composable
fun AssistWidgets(params: AssistWidgetsParams, modifier: Modifier = Modifier) {
    val containerAlpha = if (params.supportsBlur)
        AssistConstants.COMPONENT_ALPHA_WITH_BLUR
    else AssistConstants.COMPONENT_ALPHA_WITHOUT_BLUR

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (params.forecast != null || params.weatherIsLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CORNER)
                    .background(MaterialTheme.colorScheme.surfaceBright.copy(alpha = containerAlpha))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = AssistConstants.BORDER_ALPHA), CORNER)
                    .padding(20.dp)
            ) {
                val currently = params.forecast?.currently
                val icon = currently?.icon.toWeatherIcon()
                val description = currently?.summary ?: currently?.icon.toWeatherDescription()
                val temperature = currently?.temperature.toTempString(params.useFahrenheit)
                val cityName = params.cityName ?: "Unknown"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = description,
                            modifier = Modifier.size(40.dp),
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
                    }
                    Text(
                        text = temperature,
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (params.recentApps.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CORNER)
                    .background(MaterialTheme.colorScheme.surfaceBright.copy(alpha = containerAlpha))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = AssistConstants.BORDER_ALPHA), CORNER)
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = "Frequent Apps",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp)
                ) {
                    items(
                        items = params.recentApps,
                        key = { it.packageName }
                    ) { app ->
                        val icon = params.getAppIcon(app.packageName)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { params.onAppClick(app.packageName) }
                                .padding(vertical = 8.dp, horizontal = 4.dp)
                        ) {
                            if (icon != null) {
                                Image(
                                    bitmap = icon,
                                    contentDescription = app.label,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Text(
                                text = app.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
