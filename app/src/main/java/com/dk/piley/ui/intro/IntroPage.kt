package com.dk.piley.ui.intro

import androidx.annotation.DrawableRes
import com.dk.piley.R

sealed class IntroPage(
    @DrawableRes val imageResource: Int,
    val title: String,
    val description: String,
) {
    object Welcome : IntroPage(
        R.drawable.tasks,
        "Hi there",
        "Welcome to piley! This app allows you to manage regular and one-time tasks by displaying them in piles."
    )

    object Pile : IntroPage(
        R.drawable.pile_screen_demo,
        "Pile",
        "A pile is like the name says - a pile of your tasks. Click on the bottom to create a new task, or swipe tasks away to complete them."
    )

    object Piles : IntroPage(
        R.drawable.pile_overview_screen_demo,
        "Piles",
        "Manage your piles by going to the piles section and creating, editing or deleting your piles. Only the daily Pile can't be deleted."
    )

    object Profile : IntroPage(
        R.drawable.profile_screen_demo,
        "Profile",
        "You can also see general statistics and upcoming tasks by going to the profile section."
    )

    object End : IntroPage(
        R.drawable.door,
        "Almost there",
        "That's it! Click on the button to start using the app, and have fun with piley!"
    )

}
