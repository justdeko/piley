package com.dk.piley.ui.intro

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dk.piley.R

sealed class IntroPage(
    @DrawableRes val imageResource: Int,
    @StringRes val titleResource: Int,
    @StringRes val descriptionResource: Int,
) {
    object Welcome : IntroPage(
        R.drawable.tasks,
        R.string.welcome_page_title,
        R.string.welcome_page_description
    )

    object Pile : IntroPage(
        R.drawable.pile_screen_demo,
        R.string.pile_page_title,
        R.string.pile_page_description
    )

    object Piles : IntroPage(
        R.drawable.pile_overview_screen_demo,
        R.string.piles_page_title,
        R.string.piles_page_description
    )

    object Profile : IntroPage(
        R.drawable.profile_screen_demo,
        R.string.profile_page_title,
        R.string.profile_page_description
    )

    object End : IntroPage(
        R.drawable.door,
        R.string.end_page_title,
        R.string.end_page_description
    )

}
