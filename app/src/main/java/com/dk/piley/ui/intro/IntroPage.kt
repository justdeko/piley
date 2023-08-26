package com.dk.piley.ui.intro

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dk.piley.R

/**
 * Intro page representing pages for the welcome screen
 *
 * @property imageResource intro page image resource id
 * @property titleResource intro page title resource id
 * @property descriptionResource intro page description resource id
 * @property isScreenshot whether the intro page is that of a screenshot
 */
sealed class IntroPage(
    @DrawableRes val imageResource: Int,
    @DrawableRes val imageNightResource: Int,
    @StringRes val titleResource: Int,
    @StringRes val descriptionResource: Int,
    val isScreenshot: Boolean
) {
    object Welcome : IntroPage(
        imageResource = R.drawable.tasks,
        imageNightResource = R.drawable.tasks,
        titleResource = R.string.welcome_page_title,
        descriptionResource = R.string.welcome_page_description,
        isScreenshot = false
    )

    object Pile : IntroPage(
        imageResource = R.drawable.pile_screen_demo,
        imageNightResource = R.drawable.pile_screen_night_demo,
        titleResource = R.string.pile_page_title,
        descriptionResource = R.string.pile_page_description,
        isScreenshot = true
    )

    object Piles : IntroPage(
        imageResource = R.drawable.pile_overview_screen_demo,
        imageNightResource = R.drawable.pile_overview_screen_night_demo,
        titleResource = R.string.piles_page_title,
        descriptionResource = R.string.piles_page_description,
        isScreenshot = true
    )

    object Profile : IntroPage(
        imageResource = R.drawable.profile_screen_demo,
        imageNightResource = R.drawable.profile_screen_night_demo,
        titleResource = R.string.profile_page_title,
        descriptionResource = R.string.profile_page_description,
        isScreenshot = true
    )

    object End : IntroPage(
        imageResource = R.drawable.door,
        imageNightResource = R.drawable.door,
        titleResource = R.string.end_page_title,
        descriptionResource = R.string.end_page_description,
        isScreenshot = false
    )

}
