package com.paraskcd.kcdsearch.ui.modules.search.components.infoboxAccordion

import androidx.compose.ui.Modifier
import com.paraskcd.kcdsearch.data.api.search.dataSources.infobox.Infobox

data class InfoboxAccordionParams(
    val infobox: Infobox,
    val modifier: Modifier = Modifier
)
