package com.paraskcd.kcdsearch.ui.shared.components.cardContainer

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

data class CardContainerParams(
    val modifier: Modifier = Modifier,
    val containerColor: Color? = null,
    val onClick: (() -> Unit)? = null,
)
