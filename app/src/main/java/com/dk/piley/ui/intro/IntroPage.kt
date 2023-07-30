package com.dk.piley.ui.intro

import androidx.annotation.DrawableRes
import com.dk.piley.R

sealed class IntroPage(
    @DrawableRes val imageResource: Int,
    val title: String,
    val description: String,
) {
    object Welcome : IntroPage(R.drawable.cat, "Welcome", "Lorem ipsum")
    object Pile : IntroPage(R.drawable.cat, "Pile", "Manage tasks in your pile by...")
    object Piles : IntroPage(R.drawable.cat, "Piles", "Manage your piles by...")
    object End : IntroPage(R.drawable.cat, "Piles", "Manage your piles by...")

}
