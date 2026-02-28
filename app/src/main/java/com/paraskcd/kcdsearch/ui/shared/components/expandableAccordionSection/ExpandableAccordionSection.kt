package com.paraskcd.kcdsearch.ui.shared.components.expandableAccordionSection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.ui.shared.components.cardContainer.CardContainer
import com.paraskcd.kcdsearch.ui.shared.components.cardContainer.CardContainerParams
import com.paraskcd.kcdsearch.ui.shared.components.expandableAccordionSection.components.accordionExpandIcon.AccordionExpandIcon

@Composable
fun ExpandableAccordionSection(
    params: ExpandableAccordionSectionParams,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(params.initiallyExpanded) }
    val titleStyle = params.titleTextStyle ?: MaterialTheme.typography.titleMedium
    val titleColor = (params.titleColor ?: MaterialTheme.colorScheme.onSurface) as Color
    val contentPadding =
        params.contentPaddingValues ?: PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp)

    CardContainer(
        params = CardContainerParams(
            modifier = params.modifier.fillMaxWidth(),
            onClick = { expanded = !expanded }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = params.title,
                    style = titleStyle,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                AccordionExpandIcon(expanded)
            }
            params.summaryContent?.invoke(expanded)
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(contentPadding),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    content()
                }
            }
        }
    }
}