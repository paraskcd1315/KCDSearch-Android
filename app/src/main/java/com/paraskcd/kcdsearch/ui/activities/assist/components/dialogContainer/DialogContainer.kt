package com.paraskcd.kcdsearch.ui.activities.assist.components.dialogContainer

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.constants.AssistConstants

@Composable
fun DialogContainer(
    params: DialogContainerParams,
    content: @Composable () -> Unit
) {
    val alpha = if (params.supportsBlur) AssistConstants.COMPONENT_ALPHA_WITH_BLUR
    else AssistConstants.COMPONENT_ALPHA_WITHOUT_BLUR

    Surface(
        modifier = params.modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = alpha),
        tonalElevation = 2.dp
    ) {
        content()
    }
}