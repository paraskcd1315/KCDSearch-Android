package com.paraskcd.kcdsearch.ui.modules.assist

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.paraskcd.kcdsearch.constants.AssistConstants
import com.paraskcd.kcdsearch.model.SuggestionItem
import com.paraskcd.kcdsearch.ui.modules.assist.dialogs.AssistResultsDialog
import com.paraskcd.kcdsearch.ui.modules.assist.dialogs.AssistSearchBarDialog
import com.paraskcd.kcdsearch.ui.modules.assist.dialogs.AssistWidgetsDialog
import com.paraskcd.kcdsearch.ui.theme.KCDSearchTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class AssistActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: AssistViewModel by viewModels()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        @Suppress("DEPRECATION")
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        window.setDimAmount(0.0f)

        setContent {
            val localView = androidx.compose.ui.platform.LocalView.current
            var scrimVisible by remember { mutableStateOf(false) }
            var searchBarReady by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(AssistConstants.ENTRY_ANIMATION_DELAY_MS)
                scrimVisible = true
                localView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

                viewModel.onSearchBarExpanded()

                AssistSearchBarDialog.show(
                    context = this@AssistActivity,
                    onQueryChange = { query -> viewModel.setQuery(query) },
                    onQuerySubmit = { query ->
                        viewModel.onSuggestionClick(SuggestionItem.Text(query))
                        AssistResultsDialog.close()
                        finish()
                    },
                    onSuggestionClick = { suggestion ->
                        viewModel.onSuggestionClick(suggestion)
                    },
                    onDismiss = {
                        AssistResultsDialog.close()
                        AssistWidgetsDialog.close()
                        finish()
                    }
                )

                searchBarReady = true
            }

            val suggestions by viewModel.suggestions.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()
            val query by viewModel.query.collectAsState()
            val shouldShowResults = suggestions.isNotEmpty() || isLoading || query.isNotBlank()
            val shouldShowWidgets = query.isBlank()

            LaunchedEffect(shouldShowWidgets, searchBarReady) {
                if (shouldShowWidgets && searchBarReady) {
                    AssistWidgetsDialog.show(
                        context = this@AssistActivity,
                        forecastFlow = viewModel.weatherForecast,
                        weatherIsLoadingFlow = viewModel.weatherIsLoading,
                        cityNameFlow = viewModel.weatherCityName,
                        useFahrenheitFlow = viewModel.useFahrenheit,
                        recentAppsFlow = viewModel.recentApps,
                        getAppIcon = { viewModel.getAppIcon(it) },
                        onAppClick = { packageName ->
                            viewModel.launchAppFromWidget(packageName)
                            AssistSearchBarDialog.close()
                            AssistWidgetsDialog.close()
                            AssistResultsDialog.close()
                            finish()
                        }
                    )
                } else {
                    AssistWidgetsDialog.close()
                }
            }

            LaunchedEffect(shouldShowResults) {
                if (shouldShowResults) {
                    AssistResultsDialog.show(
                        context = this@AssistActivity,
                        suggestionsFlow = viewModel.suggestions,
                        isLoadingFlow = viewModel.isLoading,
                        getAppIcon = { viewModel.getAppIcon(it) },
                        onSuggestionClick = { suggestion ->
                            viewModel.onSuggestionClick(suggestion)
                            AssistSearchBarDialog.close()
                            AssistResultsDialog.close()
                            finish()
                        },
                        onCallClick = { number ->
                            viewModel.openDialer(number)
                            AssistSearchBarDialog.close()
                            AssistResultsDialog.close()
                            finish()
                        },
                        onMessageClick = { number ->
                            viewModel.openMessages(number)
                            AssistSearchBarDialog.close()
                            AssistResultsDialog.close()
                            finish()
                        },
                        onWhatsAppClick = { number ->
                            viewModel.openWhatsApp(number)
                            AssistSearchBarDialog.close()
                            AssistResultsDialog.close()
                            finish()
                        },
                        isWhatsappInstalled = viewModel.isWhatsappInstalled()
                    )
                } else {
                    AssistResultsDialog.close()
                }
            }

            val scrimColor by animateColorAsState(
                targetValue = if (scrimVisible)
                    Color.Black.copy(alpha = AssistConstants.SCRIM_ALPHA)
                else Color.Transparent,
                animationSpec = tween(durationMillis = AssistConstants.ENTRY_ANIMATION_DURATION_MS),
                label = "scrim"
            )

            KCDSearchTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(scrimColor)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            AssistSearchBarDialog.close()
                            AssistWidgetsDialog.close()
                            AssistResultsDialog.close()
                            finish()
                        }
                )
            }
        }
    }

    override fun onDestroy() {
        AssistResultsDialog.close()
        AssistWidgetsDialog.close()
        AssistSearchBarDialog.close()
        super.onDestroy()
    }
}
