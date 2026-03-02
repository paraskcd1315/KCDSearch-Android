package com.paraskcd.kcdsearch.ui.modules.search.components.fullscreenImageModal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.ArrowLeft
import com.composables.icons.heroicons.outline.EllipsisVertical
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenImageModal(params: FullscreenImageModalParams) {
    val context = LocalContext.current
    val scale = remember { Animatable(1f) }
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var gestureScale by remember { mutableStateOf(1f) }
    var gestureOffset by remember { mutableStateOf(Offset.Zero) }
    var gestureEnded by remember { mutableStateOf(false) }
    var wasInGesture by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    var twoFingersOrMore by remember { mutableStateOf(false) }

    val enterScale = remember { Animatable(0.7f) }
    val enterAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { enterScale.animateTo(1f, animationSpec = tween(300)) }
        launch { enterAlpha.animateTo(1f, animationSpec = tween(250)) }
    }

    LaunchedEffect(gestureScale, gestureOffset) {
        scale.snapTo(gestureScale)
        offset.snapTo(gestureOffset)
    }

    LaunchedEffect(gestureEnded) {
        if (gestureEnded) {
            scale.animateTo(1f, animationSpec = tween(250))
            offset.animateTo(Offset.Zero, animationSpec = tween(250))
            gestureScale = 1f
            gestureOffset = Offset.Zero
            gestureEnded = false
        }
    }

    Dialog(
        onDismissRequest = params.onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = enterScale.value,
                    scaleY = enterScale.value,
                    alpha = enterAlpha.value,
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Initial)
                                    val anyPressed = event.changes.any { it.pressed }
                                    if (anyPressed) {
                                        wasInGesture = true
                                    } else if (wasInGesture) {
                                        gestureEnded = true
                                        wasInGesture = false
                                    }
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Initial)
                                    twoFingersOrMore = event.changes.count { it.pressed } >= 2
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            detectTransformGestures { _, panChange, zoomChange, _ ->
                                if (twoFingersOrMore) {
                                    gestureScale = (gestureScale * zoomChange).coerceIn(0.5f, 5f)
                                    gestureOffset += panChange
                                }
                            }
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clipToBounds(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(params.imageUrl).build(),
                            contentDescription = params.title,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale.value,
                                    scaleY = scale.value,
                                    translationX = offset.value.x,
                                    translationY = offset.value.y
                                ),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                TopAppBar(
                    title = { if (params.title != null) Text(params.title, maxLines = 1) },
                    navigationIcon = {
                        IconButton(onClick = params.onDismiss) {
                            Icon(Heroicons.Outline.ArrowLeft, contentDescription = "Close")
                        }
                    },
                    actions = {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Heroicons.Outline.EllipsisVertical, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Download") },
                                onClick = {
                                    menuExpanded = false
                                    params.onDownload(params.imageUrl)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Share") },
                                onClick = {
                                    menuExpanded = false
                                    params.onShare(params.imageUrl)
                                }
                            )
                            params.pageUrl?.let { url ->
                                DropdownMenuItem(
                                    text = { Text("Open URL") },
                                    onClick = {
                                        menuExpanded = false
                                        params.onOpenUrl(url)
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}