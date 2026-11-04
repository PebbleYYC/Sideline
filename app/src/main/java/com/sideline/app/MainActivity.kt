package com.sideline.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sideline.app.ui.scores.ScoresScreen
import com.sideline.app.ui.theme.SidelineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SidelineTheme {
                ScoresScreen()
            }
        }
    }
}
