package com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.paraskcd.kcdsearch.model.SuggestionItem

data class AssistResultsListParams(
    val suggestions: List<SuggestionItem>,
    val isLoading: Boolean,
    val getAppIcon: (String) -> ImageBitmap?,
    val onSuggestionClick: (SuggestionItem) -> Unit,
    val onCallClick: (String) -> Unit,
    val onMessageClick: (String) -> Unit,
    val onWhatsAppClick: (String) -> Unit,
    val isWhatsappInstalled: Boolean,
    val supportsBlur: Boolean,
    val modifier: Modifier = Modifier
)