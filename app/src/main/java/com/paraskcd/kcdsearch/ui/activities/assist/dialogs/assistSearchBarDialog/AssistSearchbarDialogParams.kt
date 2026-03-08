package com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistSearchBarDialog

import android.content.Context
import com.paraskcd.kcdsearch.model.SuggestionItem

data class AssistSearchbarDialogParams(
    val context: Context,
    val onQueryChange: (String) -> Unit,
    val onQuerySubmit: (String) -> Unit,
    val onDismiss: () -> Unit
)
