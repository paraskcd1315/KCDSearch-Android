package com.paraskcd.kcdsearch.ui.modules.search

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.paraskcd.kcdsearch.ui.modules.search.screens.SearchScreen
import com.paraskcd.kcdsearch.ui.theme.KCDSearchTheme
import dagger.hilt.android.AndroidEntryPoint
import android.graphics.Color

@AndroidEntryPoint
class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        window.isNavigationBarContrastEnforced = false
        setContent {
            KCDSearchTheme {
                val viewModel: SearchViewModel by viewModels()
                SearchScreen(viewModel)
            }
        }
    }
}