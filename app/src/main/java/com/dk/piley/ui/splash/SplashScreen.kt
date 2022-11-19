package com.dk.piley.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: SplashViewModel = hiltViewModel()
) {
    SplashScreen(onAnimFinished = {
        val destination = if (viewModel.isSignedIn()) Screen.Pile.route else Screen.SignIn.route
        navController.navigate(destination) {
            popUpTo(0)
        }
    })
}


@Composable
fun SplashScreen(onAnimFinished: () -> Unit = {}) {
    val scaleFactor = remember { Animatable(1.5f) }
    val alphaFactor = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = true) {
        runAnimation(
            coroutineScope,
            scaleFactor,
            alphaFactor,
            onAnimFinished
        )
    }

    Column(
        modifier = Modifier
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
    onFinished: () -> Unit = {}
) {
    coroutineScope.launch {
        scaleFactor.animateTo(2f, tween(easing = FastOutSlowInEasing, durationMillis = 300))
        scaleFactor.animateTo(1.5f, tween(easing = FastOutSlowInEasing, durationMillis = 400))
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
            SplashScreen()
        }
    }
}
