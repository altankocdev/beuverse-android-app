package com.altankoc.beuverse

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.altankoc.beuverse.core.navigation.NavGraph
import com.altankoc.beuverse.core.ui.theme.BeuverseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            BeuverseTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}