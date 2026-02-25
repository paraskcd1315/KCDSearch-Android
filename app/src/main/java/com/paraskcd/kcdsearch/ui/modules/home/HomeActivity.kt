package com.paraskcd.kcdsearch.ui.modules.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import com.paraskcd.kcdsearch.ui.modules.home.screens.HomeScreen
import com.paraskcd.kcdsearch.ui.theme.KCDSearchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity: ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            KCDSearchTheme {
                val viewModel: HomeViewModel by viewModels()
                HomeScreen(viewModel)
            }
        }
    }
}