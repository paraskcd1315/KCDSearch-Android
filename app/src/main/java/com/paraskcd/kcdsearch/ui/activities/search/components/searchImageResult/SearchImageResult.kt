package com.paraskcd.kcdsearch.ui.activities.search.components.searchImageResult

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.paraskcd.kcdsearch.ui.shared.components.asyncImage.AsyncImageWithPlaceholder
import com.paraskcd.kcdsearch.ui.shared.components.asyncImage.AsyncImageWithPlaceholderParams
import com.paraskcd.kcdsearch.ui.shared.components.transparencyGrid.TransparencyGrid
import com.paraskcd.kcdsearch.ui.shared.components.transparencyGrid.TransparencyGridParams
import com.paraskcd.kcdsearch.utils.extensionMethods.getAspectRatio
import com.paraskcd.kcdsearch.utils.extensionMethods.getImageUrl

private val ImageCornerRadius = 12.dp

@Composable
fun SearchImageResult(params: SearchImageResultParams) {
    val result = params.result
    val imageUrl = result.getImageUrl() ?: return
    val aspectRatio = result.getAspectRatio()

    val clickModifier = if (params.onClick != null) Modifier.clickable(onClick = params.onClick) else Modifier

    Column(
        modifier = params.modifier
            .fillMaxWidth()
            .then(clickModifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .clip(RoundedCornerShape(ImageCornerRadius))
        ) {
            TransparencyGrid(
                params = TransparencyGridParams(
                    modifier = Modifier.matchParentSize(),
                    squareSize = 8.dp,
                    color1 = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    color2 = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                )
            )
            AsyncImageWithPlaceholder(
                params = AsyncImageWithPlaceholderParams(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = result.title ?: "",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onLoadFailed = params.onLoadFailed,
                    collapseOnError = false
                )
            )
            if (!result.resolution.isNullOrBlank()) {
                Text(
                    text = result.resolution,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(
                            MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}