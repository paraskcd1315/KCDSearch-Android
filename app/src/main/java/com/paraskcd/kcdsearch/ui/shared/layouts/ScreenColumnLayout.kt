package com.paraskcd.kcdsearch.ui.shared.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ScreenColumnLayout(
    params: ScreenColumnLayoutParams,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = params.modifier.scaffoldModifiers(),
        containerColor = params.containerColor,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
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