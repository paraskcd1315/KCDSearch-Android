package com.paraskcd.kcdsearch.ui.modules.assist.components.assistResults

import androidx.compose.ui.graphics.ImageBitmap
import com.paraskcd.kcdsearch.model.SuggestionItem

data class AssistResultsListParams(
    val suggestions: List<SuggestionItem>,
    val isLoading: Boolean,
    val getAppIcon: (String) -> ImageBitmap?,
    val onSuggestionClick: (SuggestionItem) -> Unit,
    val supportsBlur: Boolean,
    val onCallClick: (String) -> Unit = {},
    val onMessageClick: (String) -> Unit = {},
    val onWhatsAppClick: (String) -> Unit = {},
    val isWhatsappInstalled: Boolean = false,
)
