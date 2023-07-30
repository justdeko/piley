package com.dk.piley.ui.intro

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.theme.PileyTheme


@Composable
fun IntroScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
) {
    IntroScreen(
        modifier = modifier,
        onFinish = {
            navController.popBackStack()
            navController.navigate(Screen.Pile.route)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroScreen(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {}
) {
    val pages = listOf(
        IntroPage.Welcome,
        IntroPage.Pile,
        IntroPage.Piles,
        IntroPage.End
    )
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) { pages.size }
    Column(modifier = modifier.fillMaxSize()) {
        HorizontalPager(
            modifier = Modifier.weight(10f),
            state = pagerState,
            verticalAlignment = Alignment.CenterVertically
        ) { position ->
            if (position != pages.lastIndex) {
                IntroPageContent(introPage = pages[position])
            } else {
                IntroPageContent(
                    introPage = pages[position],
                    showButton = true,
                    buttonText = "Start Piling",
                    onClickButton = onFinish
                )
            }
        }
        PagerIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .weight(1f),
            pagerState = pagerState
        )
    }
}

@PreviewMainScreen
@Composable
fun IntroScreenPreview() {
    PileyTheme {
        Surface {
            IntroScreen(onFinish = {})
        }
    }
}
