package com.paraskcd.kcdsearch.ui.shared.components.expandableAccordionSection

import androidx.compose.ui.Modifier

data class ExpandableAccordionSectionParams(
    val title: String,
    val modifier: Modifier = Modifier,
    val initiallyExpanded: Boolean = true
)