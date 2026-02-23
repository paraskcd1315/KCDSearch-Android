package com.paraskcd.kcdsearch.ui.shared.components.kcdsearchLogo

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.R

@Composable
fun KCDSearchLogo(params: KCDSearchLogoParams) {
    Image(
        painter = painterResource(R.drawable.kcd_search_logo),
        contentDescription = "App logo",
        contentScale = ContentScale.Fit,
        modifier = Modifier.kcdSearchLogoModifiers(params.height)
    )
}

@Preview
@Composable
fun KCDSearchLogoPreview() {
    KCDSearchLogo(
        params = KCDSearchLogoParams(
            height = 240.dp
        )
    )
}