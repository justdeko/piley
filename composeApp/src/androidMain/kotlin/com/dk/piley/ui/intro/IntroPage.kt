package com.dk.piley.ui.intro

import androidx.annotation.DrawableRes
import com.dk.piley.R
import org.jetbrains.compose.resources.StringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.end_page_description
import piley.composeapp.generated.resources.end_page_title
import piley.composeapp.generated.resources.pile_page_description
import piley.composeapp.generated.resources.pile_page_title
import piley.composeapp.generated.resources.piles_page_description
import piley.composeapp.generated.resources.piles_page_title
import piley.composeapp.generated.resources.profile_page_description
import piley.composeapp.generated.resources.profile_page_title
import piley.composeapp.generated.resources.recurring_pile_page_description
import piley.composeapp.generated.resources.recurring_pile_page_title
import piley.composeapp.generated.resources.welcome_page_description
import piley.composeapp.generated.resources.welcome_page_title

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
    val titleResource: StringResource,
    val descriptionResource: StringResource,
    val isScreenshot: Boolean
) {
    data object Welcome : IntroPage(
        imageResource = R.drawable.tasks,
        imageNightResource = R.drawable.tasks,
        titleResource = Res.string.welcome_page_title,
        descriptionResource = Res.string.welcome_page_description,
        isScreenshot = false
    )

    data object Pile : IntroPage(
        imageResource = R.drawable.pile_screen_demo,
        imageNightResource = R.drawable.pile_screen_night_demo,
        titleResource = Res.string.pile_page_title,
        descriptionResource = Res.string.pile_page_description,
        isScreenshot = true
    )

    data object RecurringPile : IntroPage(
        imageResource = R.drawable.recurring_pile_screen_demo,
        imageNightResource = R.drawable.recurring_pile_screen_night_demo,
        titleResource = Res.string.recurring_pile_page_title,
        descriptionResource = Res.string.recurring_pile_page_description,
        isScreenshot = true
    )

    data object Piles : IntroPage(
        imageResource = R.drawable.pile_overview_screen_demo,
        imageNightResource = R.drawable.pile_overview_screen_night_demo,
        titleResource = Res.string.piles_page_title,
        descriptionResource = Res.string.piles_page_description,
        isScreenshot = true
    )

    data object Profile : IntroPage(
        imageResource = R.drawable.profile_screen_demo,
        imageNightResource = R.drawable.profile_screen_night_demo,
        titleResource = Res.string.profile_page_title,
        descriptionResource = Res.string.profile_page_description,
        isScreenshot = true
    )

    data object End : IntroPage(
        imageResource = R.drawable.door,
        imageNightResource = R.drawable.door,
        titleResource = Res.string.end_page_title,
        descriptionResource = Res.string.end_page_description,
        isScreenshot = false
    )

}
