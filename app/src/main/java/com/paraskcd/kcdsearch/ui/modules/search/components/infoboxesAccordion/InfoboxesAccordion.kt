package com.paraskcd.kcdsearch.ui.modules.search.components.infoboxesAccordion

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.data.api.search.dataSources.infobox.Infobox
import com.paraskcd.kcdsearch.ui.modules.search.components.infoboxesAccordion.components.InfoboxAccordionItem
import com.paraskcd.kcdsearch.ui.shared.components.expandableAccordionSection.ExpandableAccordionSection
import com.paraskcd.kcdsearch.ui.shared.components.expandableAccordionSection.ExpandableAccordionSectionParams
import kotlin.collections.filter

@Composable
fun InfoboxesAccordion(
    infoboxes: List<Infobox>,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = true
) {
    if (infoboxes.isEmpty()) return
    val filtered = infoboxes.filter { it.attributes.isNotEmpty() || it.content.isNotBlank() || it.imgSrc.isNotBlank() }
    if (filtered.isEmpty()) return

    filtered.forEach { infobox ->
      InfoboxAccordionItem(
          infobox = infobox,
          modifier = Modifier.padding(vertical = 4.dp)
      )
  }
}