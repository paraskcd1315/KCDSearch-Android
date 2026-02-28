package com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion.components.appListRow.AppListRow
import com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion.components.appListRow.AppListRowParams
import com.paraskcd.kcdsearch.ui.shared.components.expandableAccordionSection.ExpandableAccordionSection
import com.paraskcd.kcdsearch.ui.shared.components.expandableAccordionSection.ExpandableAccordionSectionParams

@Composable
fun AppsAccordion(params: AppsAccordionParams) {
    ExpandableAccordionSection(
        params = ExpandableAccordionSectionParams(
            title = "Apps",
            initiallyExpanded = false,
            modifier = Modifier.padding(bottom = 16.dp),
            contentPaddingValues = PaddingValues(top = 12.dp)
        )
    ) {
        params.appResults.forEach { app ->
            AppListRow(
                params = AppListRowParams(
                    app,
                    getAppIcon = params.getAppIcon,
                    launchApp = params.launchApp
                )
            )
        }
    }
}