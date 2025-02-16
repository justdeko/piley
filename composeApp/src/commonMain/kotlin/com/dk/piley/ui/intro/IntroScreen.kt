package com.dk.piley.ui.intro

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.Piley
import com.dk.piley.ui.nav.Screen
import com.dk.piley.util.usernameCharacterLimit
import kotlinx.coroutines.launch
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
    viewModel: IntroViewModel = viewModel {
        IntroViewModel(
            userRepository = Piley.getModule().userRepository,
            shortcutEventRepository = Piley.getModule().shortcutEventRepository
        )
    }
) {
    val viewState by viewModel.state.collectAsState()
    IntroScreen(
        viewState = viewState,
        modifier = modifier,
        onFinish = {
            viewModel.setUsername(it)
            navController.popBackStack()
            navController.navigate(Screen.Pile.route)
        },
        onConsumeKeyEvent = { viewModel.onConsumeKeyEvent() }
    )
}

/**
 * Intro screen content
 *
 * @param modifier generic modifier
 * @param onFinish on intro screen completion
 */
@Composable
fun IntroScreen(
    viewState: IntroViewState,
    modifier: Modifier = Modifier,
    onFinish: (String) -> Unit = {},
    onConsumeKeyEvent: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val pages = listOf(
        IntroPage.Welcome,
        IntroPage.Pile,
        IntroPage.Piles,
        IntroPage.PileDetails,
        IntroPage.Profile,
        IntroPage.End
    )
    val pagerState = rememberPagerState { pages.size }
    viewState.keyEvent?.let {
        coroutineScope.launch {
            when (it) {
                KeyEventAction.LEFT -> pagerState.animateScrollToPage(pagerState.currentPage - 1)
                KeyEventAction.RIGHT -> pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
        onConsumeKeyEvent()
    }
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                },
                enabled = pagerState.currentPage != 0
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowLeft,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = LocalContentColor.current.alpha),
                    contentDescription = "go back one page"
                )
            }
            PagerIndicator(
                modifier = Modifier.weight(1f),
                pagerState = pagerState
            )
            IconButton(
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                },
                enabled = pagerState.currentPage != pagerState.pageCount - 1
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowRight,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = LocalContentColor.current.alpha),
                    contentDescription = "go forward one page"
                )
            }
        }
    }
}
