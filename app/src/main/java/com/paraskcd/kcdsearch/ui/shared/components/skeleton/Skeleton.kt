package com.paraskcd.kcdsearch.ui.shared.components.skeleton

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

@Composable
fun Skeleton(
    params: SkeletonParams = SkeletonParams(),
    modifier: Modifier
) {
    val sizeModifier = when {
        params.size != null -> Modifier.size(params.size)
        else -> Modifier
            .then(
                when {
                    params.width != null -> Modifier.width(params.width)
                    params.fillMaxWidth -> Modifier.fillMaxWidth()
                    else -> Modifier
                }
            )
            .then(
                when {
                    params.height != null -> Modifier.height(params.height)
                    params.fillMaxHeight -> Modifier.fillMaxHeight()
                    else -> Modifier
                }
            )
    }

    Box(
        modifier = modifier
            .then(sizeModifier)
            .then(params.clip?.let { Modifier.clip(it) } ?: Modifier)
            .shimmerLoading()
    )
}