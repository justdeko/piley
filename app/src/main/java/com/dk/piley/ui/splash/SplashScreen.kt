package com.dk.piley.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.R
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.BigSpacer
import com.dk.piley.util.navigateClearBackstack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Splash screen
 *
 * @param modifier generic modifier
 * @param navController generic nav controller
 * @param viewModel splash view model
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: SplashViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    SplashScreen(
        modifier,
        viewState,
        onAnimFinished = {
            Timber.d("splash anim finished")
            val destination =
                if (viewModel.state.value.initState == InitState.NOT_SIGNED_IN) {
                    Screen.SignIn.route
                } else Screen.Pile.route
            navController.navigateClearBackstack(destination)
        }
    )
}

/**
 * Splash screen content
 *
 * @param modifier generic modifier
 * @param viewState splash view state
 * @param onAnimFinished on splash animation finished
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    viewState: SplashViewState,
    onAnimFinished: () -> Unit = {}
) {
    val scaleFactor = remember { Animatable(1.5f) }
    val alphaFactor = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    SplashAnimationLaunchedEffect(
        coroutineScope = coroutineScope,
        scaleFactor = scaleFactor,
        alpha = alphaFactor,
        viewState = viewState,
        onFinished = onAnimFinished
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier
                .scale(scaleFactor.value)
                .alpha(alphaFactor.value),
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            tint = Color.Unspecified
        )
        BigSpacer()
        Text(
            text = stringResource(R.string.app_name),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier
                .padding(bottom = LocalDim.current.veryLarge)
                .alpha(alphaFactor.value),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Splash animation inside a one-time launched effect
 *
 * @param coroutineScope generic coroutine scope
 * @param scaleFactor animated icon scale factor
 * @param alpha animated icon and text alpha factor
 * @param viewState splash view state
 * @param onFinished on animation finished
 */
@Composable
private fun SplashAnimationLaunchedEffect(
    coroutineScope: CoroutineScope,
    scaleFactor: Animatable<Float, AnimationVector1D>,
    alpha: Animatable<Float, AnimationVector1D>,
    viewState: SplashViewState,
    onFinished: () -> Unit = {}
) {
    val splashViewState by rememberUpdatedState(viewState)
    val onAnimFinishedState by rememberUpdatedState(onFinished)
    LaunchedEffect(key1 = true) {
        coroutineScope.launch {
            do {
                Timber.d("loading animation triggered, init state: $splashViewState")
                scaleFactor.animateTo(2f, tween(easing = FastOutSlowInEasing, durationMillis = 300))
                scaleFactor.animateTo(
                    1.5f,
                    tween(easing = FastOutSlowInEasing, durationMillis = 400)
                )
            } while (splashViewState.initState == InitState.LOADING_BACKUP || splashViewState.initState == InitState.INIT)
            awaitAll(
                async {
                    scaleFactor.animateTo(
                        160f,
                        tween(easing = FastOutSlowInEasing, durationMillis = 600)
                    )
                },
                async {
                    alpha.animateTo(
                        0f,
                        tween(easing = LinearOutSlowInEasing, durationMillis = 400)
                    )
                    onAnimFinishedState()
                }
            )
        }
    }
}

@PreviewMainScreen
@Composable
fun TaskDetailScreenPreview() {
    PileyTheme {
        Surface {
            SplashScreen(viewState = SplashViewState())
        }
    }
}
