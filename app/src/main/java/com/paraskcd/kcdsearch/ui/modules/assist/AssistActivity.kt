package com.paraskcd.kcdsearch.ui.modules.assist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.paraskcd.kcdsearch.ui.theme.KCDSearchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssistActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KCDSearchTheme {
                // Assist UI implementation
            }
        }
    }
}