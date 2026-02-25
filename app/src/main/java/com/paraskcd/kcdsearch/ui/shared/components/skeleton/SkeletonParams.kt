package com.paraskcd.kcdsearch.ui.shared.components.skeleton

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class SkeletonParams(
    val fillMaxWidth: Boolean = false,
    val width: Dp? = null,
    val fillMaxHeight: Boolean = false,
    val height: Dp? = null,
    val size: Dp? = null,
    val clip: Shape? = RoundedCornerShape(16.dp)
)
