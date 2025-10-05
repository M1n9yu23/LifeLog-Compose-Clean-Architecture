package com.bossmg.android.lifelog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bossmg.android.designsystem.ui.theme.LifeLogTheme
import com.bossmg.android.lifelog.ui.LifeLogApp
import com.bossmg.android.lifelog.ui.rememberLifeLogAppState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LifeLogTheme {
                val appState = rememberLifeLogAppState()
                LifeLogApp(appState)
            }
        }
    }
}