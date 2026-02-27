package com.paraskcd.kcdsearch.ui.shared.components.expandableAccordionSection.components.accordionExpandIcon

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.ChevronDown
import com.composables.icons.heroicons.outline.ChevronUp

@Composable
fun AccordionExpandIcon(
    expanded: Boolean,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = if (expanded) Heroicons.Outline.ChevronUp else Heroicons.Outline.ChevronDown,
        contentDescription = if (expanded) "Collapse" else "Expand",
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .size(24.dp)
            .animateContentSize(animationSpec = tween(200))
    )
}