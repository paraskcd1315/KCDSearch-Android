package com.paraskcd.kcdsearch.ui.shared.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ScreenColumnLayout(
    params: ScreenColumnLayoutParams,
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = params.modifier.scaffoldModifiers(),
        containerColor = params.containerColor
    ) { innerPadding ->
        Box(
            modifier = Modifier.backgroundGradientModifiers(params.gradientColors ?: listOf(Color.Transparent))
        ) {
            Column(
                modifier = Modifier
                    .columnModifiers(innerPadding),
                horizontalAlignment = when (params.contentAlignment) {
                    Alignment.Center -> Alignment.CenterHorizontally
                    Alignment.CenterStart -> Alignment.Start
                    Alignment.CenterEnd -> Alignment.End
                    else -> Alignment.CenterHorizontally
                },
                verticalArrangement = params.verticalArrangement
            ) {
                content()
            }
        }
    }
}