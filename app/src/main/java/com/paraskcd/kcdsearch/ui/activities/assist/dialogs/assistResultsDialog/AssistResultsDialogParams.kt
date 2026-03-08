package com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import com.paraskcd.kcdsearch.model.SuggestionItem
import kotlinx.coroutines.flow.StateFlow

data class AssistResultsDialogParams(
    val context: Context,
    val suggestionsFlow: StateFlow<List<SuggestionItem>>,
    val isLoadingFlow: StateFlow<Boolean>,
    val getAppIcon: (String) -> ImageBitmap?,
    val onSuggestionClick: (SuggestionItem) -> Unit,
    val onCallClick: (String) -> Unit,
    val onMessageClick: (String) -> Unit,
    val onWhatsAppClick: (String) -> Unit,
    val isWhatsappInstalled: Boolean
)
