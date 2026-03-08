package com.paraskcd.kcdsearch.ui.activities.search.components.searchTabs.contents.imageContent

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.constants.GlobalConstants.IMAGE_SKELETON_ASPECT_RATIOS
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResult
import com.paraskcd.kcdsearch.ui.activities.search.components.imageResultSkeleton.ImageResultSkeletonParams
import com.paraskcd.kcdsearch.ui.activities.search.components.searchImageResult.ImageResultSkeleton
import com.paraskcd.kcdsearch.ui.activities.search.components.searchImageResult.SearchImageResult
import com.paraskcd.kcdsearch.ui.activities.search.components.searchImageResult.SearchImageResultParams
import com.paraskcd.kcdsearch.utils.extensionMethods.getImageUrl

fun LazyStaggeredGridScope.imagesContent(params: ImagesContentParams) {
    val (isLoading, query, displayableImages, hasMorePages, onImageSelected, onImageLoadFailed) = params

    if (isLoading && displayableImages.isEmpty() && query.isNotBlank()) {
        itemsIndexed(
            IMAGE_SKELETON_ASPECT_RATIOS,
            key = { index, _ -> "img_skeleton_$index" }
        ) { _, ratio ->
            ImageResultSkeleton(
                params = ImageResultSkeletonParams(
                    aspectRatio = ratio,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            )
        }
    } else {
        itemsIndexed(
            displayableImages,
            key = { index: Int, result: SearchResult -> "img_${index}_${result.url}_${result.imgSrc}" }
        ) { _: Int, result: SearchResult ->
            SearchImageResult(
                params = SearchImageResultParams(
                    result = result,
                    modifier = Modifier.padding(bottom = 12.dp),
                    onClick = { onImageSelected(result) },
                    onLoadFailed = {
                        result.getImageUrl()?.let { url ->
                            onImageLoadFailed(url)
                        }
                    }
                ),
            )
        }
        if (isLoading && displayableImages.isNotEmpty()) {
            itemsIndexed(
                IMAGE_SKELETON_ASPECT_RATIOS.take(3),
                key = { i, _ -> "img_loading_skeleton_$i" }
            ) { _, ratio ->
                ImageResultSkeleton(
                    params = ImageResultSkeletonParams(
                        aspectRatio = ratio,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                )
            }
        }
        if (!hasMorePages && displayableImages.isNotEmpty()) {
            item(key = "img_no_more") {
                Text(
                    text = "No more results",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}