package com.dk.piley.ui.intro

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.door
import piley.composeapp.generated.resources.end_page_description
import piley.composeapp.generated.resources.end_page_title
import piley.composeapp.generated.resources.pile_detail_screen_demo
import piley.composeapp.generated.resources.pile_detail_screen_demo_tablet
import piley.composeapp.generated.resources.pile_detail_screen_night_demo
import piley.composeapp.generated.resources.pile_details_page_description
import piley.composeapp.generated.resources.pile_details_page_title
import piley.composeapp.generated.resources.pile_overview_screen_demo
import piley.composeapp.generated.resources.pile_overview_screen_demo_tablet
import piley.composeapp.generated.resources.pile_overview_screen_night_demo
import piley.composeapp.generated.resources.pile_page_description
import piley.composeapp.generated.resources.pile_page_title
import piley.composeapp.generated.resources.pile_screen_demo
import piley.composeapp.generated.resources.pile_screen_demo_tablet
import piley.composeapp.generated.resources.pile_screen_night_demo
import piley.composeapp.generated.resources.piles_page_description
import piley.composeapp.generated.resources.piles_page_title
import piley.composeapp.generated.resources.profile_page_description
import piley.composeapp.generated.resources.profile_page_title
import piley.composeapp.generated.resources.profile_screen_demo
import piley.composeapp.generated.resources.profile_screen_demo_tablet
import piley.composeapp.generated.resources.profile_screen_night_demo
import piley.composeapp.generated.resources.tasks
import piley.composeapp.generated.resources.welcome_page_description
import piley.composeapp.generated.resources.welcome_page_title

/**
 * Intro page representing pages for the welcome screen
 *
 * @property imageResource intro page image resource id
 * @property imageNightResource intro page dark mode image resource id
 * @property tabletResource intro page tablet image resource id
 * @property titleResource intro page title resource id
 * @property descriptionResource intro page description resource id
 * @property isScreenshot whether the intro page is that of a screenshot
 */
sealed class IntroPage(
    val imageResource: DrawableResource,
    val imageNightResource: DrawableResource,
    val tabletResource: DrawableResource,
    val titleResource: StringResource,
    val descriptionResource: StringResource,
    val isScreenshot: Boolean
) {
    data object Welcome : IntroPage(
        imageResource = Res.drawable.tasks,
        imageNightResource = Res.drawable.tasks,
        tabletResource = Res.drawable.tasks,
        titleResource = Res.string.welcome_page_title,
        descriptionResource = Res.string.welcome_page_description,
        isScreenshot = false
    )

    data object Pile : IntroPage(
        imageResource = Res.drawable.pile_screen_demo,
        imageNightResource = Res.drawable.pile_screen_night_demo,
        tabletResource = Res.drawable.pile_screen_demo_tablet,
        titleResource = Res.string.pile_page_title,
        descriptionResource = Res.string.pile_page_description,
        isScreenshot = true
    )

    data object Piles : IntroPage(
        imageResource = Res.drawable.pile_overview_screen_demo,
        imageNightResource = Res.drawable.pile_overview_screen_night_demo,
        tabletResource = Res.drawable.pile_overview_screen_demo_tablet,
        titleResource = Res.string.piles_page_title,
        descriptionResource = Res.string.piles_page_description,
        isScreenshot = true
    )

    data object PileDetails : IntroPage(
        imageResource = Res.drawable.pile_detail_screen_demo,
        imageNightResource = Res.drawable.pile_detail_screen_night_demo,
        tabletResource = Res.drawable.pile_detail_screen_demo_tablet,
        titleResource = Res.string.pile_details_page_title,
        descriptionResource = Res.string.pile_details_page_description,
        isScreenshot = true
    )

    data object Profile : IntroPage(
        imageResource = Res.drawable.profile_screen_demo,
        imageNightResource = Res.drawable.profile_screen_night_demo,
        tabletResource = Res.drawable.profile_screen_demo_tablet,
        titleResource = Res.string.profile_page_title,
        descriptionResource = Res.string.profile_page_description,
        isScreenshot = true
    )

    data object End : IntroPage(
        imageResource = Res.drawable.door,
        imageNightResource = Res.drawable.door,
        tabletResource = Res.drawable.door,
        titleResource = Res.string.end_page_title,
        descriptionResource = Res.string.end_page_description,
        isScreenshot = false
    )

}
