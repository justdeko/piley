package com.dk.piley.util

import android.content.Context
import android.content.res.Configuration
import com.dk.piley.R

/**
 * Set ui theme for non-compose UI elements
 *
 * @param context generic context
 * @param nightModeEnabled whether night mode is enabled
 */
fun setUiTheme(context: Context, nightModeEnabled: Boolean) {
    val mainTheme = if (nightModeEnabled) R.style.Theme_Piley_Dark else R.style.Theme_Piley_Light
    context.setTheme(mainTheme)
}


/**
 * Whether the user is has night mode enabled
 *
 * @return true if night mode enabled
 */
fun Context.isDarkMode(): Boolean {
    val darkModeFlag = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
}