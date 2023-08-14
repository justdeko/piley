package com.dk.piley.util

import android.content.Context
import android.content.res.Configuration
import android.util.Patterns


fun Context.isDarkMode(): Boolean {
    val darkModeFlag = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
}

fun CharSequence?.isValidEmail() =
    !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

const val descriptionCharacterLimit = 200
const val titleCharacterLimit = 80
const val pileTitleCharacterLimit = 20
const val usernameCharacterLimit = 40


// initial message to display when (re)starting app
const val INITIAL_MESSAGE = "initial_message"