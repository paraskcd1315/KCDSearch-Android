package com.paraskcd.kcdsearch.ui.activities.assist

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.paraskcd.kcdsearch.model.SuggestionItem
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.AssistResultsDialog
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.AssistResultsDialogParams
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistSearchBarDialog.AssistSearchbarDialog
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistSearchBarDialog.AssistSearchbarDialogParams
import com.paraskcd.kcdsearch.ui.shared.components.kcdsearchLogo.KCDSearchLogo
import com.paraskcd.kcdsearch.ui.shared.components.kcdsearchLogo.KCDSearchLogoParams
import com.paraskcd.kcdsearch.ui.shared.layouts.backgroundGradientModifiers
import com.paraskcd.kcdsearch.ui.theme.KCDSearchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssistActivity: ComponentActivity() {
    private val viewModel: AssistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        @Suppress("DEPRECATION")
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        window.setDimAmount(0.0f)

        setContent {
            KCDSearchTheme {
                var scrimVisible by remember { mutableStateOf(false) }
                val suggestions by viewModel.suggestions.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()
                val shouldShowResults = suggestions.isNotEmpty() || isLoading

                LaunchedEffect(Unit) {
                    scrimVisible = true
                }

                LaunchedEffect(Unit) {
                    AssistSearchbarDialog.show(
                        AssistSearchbarDialogParams(
                            context = this@AssistActivity,
                            onQueryChange = { viewModel.setQuery(it) },
                            onQuerySubmit = { query ->
                                viewModel.onSuggestionClick(
                                    SuggestionItem.Text(query)
                                )
                                closeAllAndFinish()
                            },
                            onDismiss = { closeAllAndFinish() }
                        )
                    )
                }

                LaunchedEffect(shouldShowResults) {
                    if (shouldShowResults && !AssistResultsDialog.isShowing()) {
                        AssistResultsDialog.show(
                            AssistResultsDialogParams(
                                context = this@AssistActivity,
                                suggestionsFlow = viewModel.suggestions,
                                isLoadingFlow = viewModel.isLoading,
                                getAppIcon = viewModel::getAppIcon,
                                onSuggestionClick = { suggestion ->
                                    viewModel.onSuggestionClick(suggestion)
                                    closeAllAndFinish()
                                },
                                onCallClick = { viewModel.openDialer(it) },
                                onMessageClick = { viewModel.openMessages(it) },
                                onWhatsAppClick = { viewModel.openWhatsApp(it) },
                                isWhatsappInstalled = viewModel.isWhatsappInstalled()
                            )
                        )
                    } else if (!shouldShowResults && AssistResultsDialog.isShowing()) {
                        AssistResultsDialog.close()
                    }
                }

                AnimatedVisibility(
                    visible = scrimVisible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .backgroundGradientModifiers(
                                colors = listOf(
                                            MaterialTheme.colorScheme
                                                .surfaceContainerHigh.copy(alpha = 0.5f),
                                            MaterialTheme.colorScheme
                                                .surface.copy(alpha = 0.5f
                                        )
                                )
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                closeAllAndFinish()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        KCDSearchLogo(
                            params = KCDSearchLogoParams(
                                height = 150.dp
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        AssistSearchbarDialog.close()
        AssistResultsDialog.close()
        super.onDestroy()
    }

    private fun closeAllAndFinish() {
        AssistSearchbarDialog.close()
        AssistResultsDialog.close()
        finish()
    }
}