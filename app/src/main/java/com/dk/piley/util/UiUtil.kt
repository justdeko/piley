package com.dk.piley.util

import android.content.Context
import android.content.res.Configuration


fun Context.isDarkMode(): Boolean {
    val darkModeFlag = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
}