package com.dk.piley

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.dk.piley.ui.HomeScreen
import com.dk.piley.ui.theme.ThemeHostScreen
import com.dk.piley.util.getDynamicColorScheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            ThemeHostScreen(
                setSystemUsingTheme = {
                    // set status bar color and icons color
                    val activity = context as Activity
                    val window = activity.window
                    val wic = WindowCompat.getInsetsController(window, window.decorView)
                    wic.isAppearanceLightStatusBars = !it
                },
                customColorSchemeProvider = { state, nightModeEnabled ->
                    getDynamicColorScheme(
                        state,
                        nightModeEnabled
                    )
                }
            ) {
                HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    onFinishActivity = { this.finishAndRemoveTask() }
                )
            }
        }
    }
}
