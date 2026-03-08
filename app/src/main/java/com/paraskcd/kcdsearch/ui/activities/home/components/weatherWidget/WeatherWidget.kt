package com.paraskcd.kcdsearch.ui.activities.home.components.weatherWidget

import androidx.compose.runtime.Composable
import com.paraskcd.kcdsearch.ui.activities.home.components.weatherWidget.components.weatherWidgetSkeleton.WeatherWidgetSkeleton
import com.paraskcd.kcdsearch.ui.activities.home.components.weatherWidget.components.weatherWidgetSkeleton.WeatherWidgetSkeletonParams
import com.paraskcd.kcdsearch.ui.activities.home.components.weatherWidget.contents.weatherContent.WeatherContent
import com.paraskcd.kcdsearch.ui.activities.home.components.weatherWidget.contents.weatherContent.WeatherContentParams
import com.paraskcd.kcdsearch.ui.activities.home.components.weatherWidget.contents.weatherErrorContent.WeatherErrorContent
import com.paraskcd.kcdsearch.ui.activities.home.components.weatherWidget.contents.weatherErrorContent.WeatherErrorContentParams
import com.paraskcd.kcdsearch.ui.activities.home.components.weatherWidget.contents.weatherNoPermissionContent.WeatherNoPermissionContent
import com.paraskcd.kcdsearch.ui.activities.home.components.weatherWidget.contents.weatherNoPermissionContent.WeatherNoPermissionContentParams

@Composable
fun WeatherWidget(params: WeatherWidgetParams) {
    when {
        params.requiresPermission -> WeatherNoPermissionContent(
            params = WeatherNoPermissionContentParams(
                onRequestPermission = params.onRequestPermission,
                modifier = params.modifier
            )
        )
        params.isLoading && params.forecast == null -> WeatherWidgetSkeleton(
            params = WeatherWidgetSkeletonParams(modifier = params.modifier)
        )
        params.forecast != null -> WeatherContent(
            params = WeatherContentParams(
                forecast = params.forecast,
                cityName = params.cityName,
                modifier = params.modifier,
                useFahrenheit = params.useFahrenheit
            )
        )
        params.error != null -> WeatherErrorContent(
            params = WeatherErrorContentParams(
                message = params.error.localizedMessage ?: "Unable to load weather",
                onRetry = params.onRetry,
                modifier = params.modifier
            )
        )
        else -> Unit

    }
}