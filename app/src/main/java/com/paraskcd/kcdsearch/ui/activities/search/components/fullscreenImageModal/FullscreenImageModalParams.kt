package com.paraskcd.kcdsearch.ui.activities.search.components.fullscreenImageModal

import androidx.compose.ui.Modifier

data class FullscreenImageModalParams(
    val imageUrl: String,
    val pageUrl: String?,
    val title: String? = null,
    val onDismiss: () -> Unit,
    val onOpenUrl: (String) -> Unit,
    val onShare: (String) -> Unit,
    val onDownload: (String) -> Unit,
    val modifier: Modifier = Modifier
)