package com.paraskcd.kcdsearch.ui.shared.components.expandableAccordionSection

import android.graphics.Color
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

data class ExpandableAccordionSectionParams(
    val title: String,
    val modifier: Modifier = Modifier,
    val initiallyExpanded: Boolean = true,
    val titleTextStyle: TextStyle? = null,
    val titleColor: Color? = null,
    val summaryContent: (@Composable (expanded: Boolean) -> Unit)? = null,
    val contentPaddingValues: PaddingValues? = null,
)