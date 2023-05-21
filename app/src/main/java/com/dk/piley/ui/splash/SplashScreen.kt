package com.dk.piley.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.R
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.navigateClearBackstack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber

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
            Timber.d("anim finished")
            val destination = if (viewModel.isSignedIn()) Screen.Pile.route else Screen.SignIn.route
            navController.navigateClearBackstack(destination)
        }
    )
}


@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    viewState: SplashViewState,
    onAnimFinished: () -> Unit = {}
) {
    val scaleFactor = remember { Animatable(1.5f) }
    val alphaFactor = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = true) {
        runAnimation(
            coroutineScope = coroutineScope,
            scaleFactor = scaleFactor,
            alpha = alphaFactor,
            loading = viewState.loadingBackup,
            onFinished = onAnimFinished
        )
    }

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
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "piley",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier
                .padding(bottom = 32.dp)
                .alpha(alphaFactor.value),
            textAlign = TextAlign.Center
        )
    }
}

private fun runAnimation(
    coroutineScope: CoroutineScope,
    scaleFactor: Animatable<Float, AnimationVector1D>,
    alpha: Animatable<Float, AnimationVector1D>,
    loading: Boolean,
    onFinished: () -> Unit = {}
) {
    coroutineScope.launch {
        do {
            Timber.d("loading animation triggered")
            scaleFactor.animateTo(2f, tween(easing = FastOutSlowInEasing, durationMillis = 300))
            scaleFactor.animateTo(1.5f, tween(easing = FastOutSlowInEasing, durationMillis = 400))
        } while (loading)
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
                onFinished()
            }
        )
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
