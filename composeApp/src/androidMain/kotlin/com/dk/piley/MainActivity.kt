package com.dk.piley

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.dk.piley.ui.HomeScreen
import com.dk.piley.ui.theme.ThemeHostScreen
import com.dk.piley.util.getDynamicColorScheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            ThemeHostScreen(
                setSystemUsingTheme = {
                    // set status bar color and icons color
                    val activity = context as Activity
                    val window = activity.window
                    window.statusBarColor = MaterialTheme.colorScheme.background.toArgb()
                    window.navigationBarColor = MaterialTheme.colorScheme.surfaceContainer.toArgb()
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
                    onFinishActivity = { this.finishAndRemoveTask() }
                )
            }
        }
    }
}
