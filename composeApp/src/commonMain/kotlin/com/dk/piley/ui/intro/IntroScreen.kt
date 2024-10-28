package com.dk.piley.ui.intro

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.Piley
import com.dk.piley.ui.nav.Screen
import com.dk.piley.util.usernameCharacterLimit
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.finish_intro_button
import piley.composeapp.generated.resources.first_user_name_hint

/**
 * Intro screen
 *
 * @param modifier generic modifier
 * @param navController generic nav controller
 * @param viewModel intro view model
 */
@Composable
fun IntroScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: IntroViewModel = viewModel {  IntroViewModel(Piley.getModule().userRepository) }
) {
    IntroScreen(
        modifier = modifier,
        onFinish = {
            viewModel.setUsername(it)
            navController.popBackStack()
            navController.navigate(Screen.Pile.route)
        },
    )
}

/**
 * Intro screen content
 *
 * @param modifier generic modifier
 * @param onFinish on intro screen completion
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroScreen(
    modifier: Modifier = Modifier,
    onFinish: (String) -> Unit = {},
) {
    val pages = listOf(
        IntroPage.Welcome,
        IntroPage.Pile,
        IntroPage.RecurringPile,
        IntroPage.Piles,
        IntroPage.Profile,
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
                IntroPageTextFieldContent(
                    introPage = pages[position],
                    textFieldHint = stringResource(Res.string.first_user_name_hint),
                    textMaxLength = usernameCharacterLimit,
                    buttonText = stringResource(Res.string.finish_intro_button),
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
