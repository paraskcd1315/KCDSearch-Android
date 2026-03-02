package com.paraskcd.kcdsearch.ui.shared.components.asyncImage

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.request.ImageRequest

data class AsyncImageWithPlaceholderParams(
    val model: ImageRequest,
    val contentDescription: String?,
    val modifier: Modifier = Modifier,
    val contentScale: ContentScale,
    val loadingContent: (@Composable () -> Unit)? = null,
    val errorContent: (@Composable () -> Unit)? = null,
    val onLoadFailed: (() -> Unit)? = null,
    val collapseOnError: Boolean = true
)
