package com.paraskcd.kcdsearch.ui.shared.components.asyncImage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.paraskcd.kcdsearch.ui.shared.components.skeleton.Skeleton
import com.paraskcd.kcdsearch.ui.shared.components.skeleton.SkeletonParams

@Composable
fun AsyncImageWithPlaceholder(params: AsyncImageWithPlaceholderParams) {
    var imageState by remember { mutableStateOf<AsyncImagePainter.State?>(null) }
    val collapseOnError = params.collapseOnError && params.errorContent == null
    val isCollapsed = collapseOnError && imageState is AsyncImagePainter.State.Error

    if (isCollapsed) {
        Box(Modifier.size(0.dp))
        return
    }

    Box(modifier = params.modifier) {
        SubcomposeAsyncImage(
            model = params.model,
            contentDescription = params.contentDescription,
            modifier = Modifier.matchParentSize(),
            contentScale = params.contentScale
        ) {
            SideEffect { imageState = painter.state }
            when (val state = painter.state) {
                is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
                is AsyncImagePainter.State.Error -> {
                    LaunchedEffect(Unit) {
                        params.onLoadFailed?.invoke()
                    }
                    when {
                        params.errorContent != null -> params.errorContent.invoke()
                        params.collapseOnError -> Box(Modifier.size(0.dp))
                        else -> Skeleton(
                            params = SkeletonParams(
                                fillMaxWidth = true,
                                fillMaxHeight = true,
                                clip = RoundedCornerShape(4.dp)
                            ),
                            modifier = Modifier.matchParentSize()
                        )
                    }
                }
                else -> {
                    params.loadingContent?.invoke() ?: Skeleton(
                        params = SkeletonParams(
                            fillMaxWidth = true,
                            fillMaxHeight = true,
                            clip = RoundedCornerShape(4.dp)
                        ),
                        modifier = Modifier.matchParentSize()
                    )
                }
            }
        }
    }
}