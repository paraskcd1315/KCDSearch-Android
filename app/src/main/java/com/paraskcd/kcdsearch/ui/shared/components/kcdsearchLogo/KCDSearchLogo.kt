package com.paraskcd.kcdsearch.ui.shared.components.kcdsearchLogo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.R

@Composable
fun KCDSearchLogo() {
    Image(
        painter = painterResource(R.drawable.kcd_search_logo),
        contentDescription = "App logo",
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    )
}