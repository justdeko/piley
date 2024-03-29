package com.dk.piley.ui.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.BigSpacer
import com.dk.piley.util.isDarkMode
import com.dk.piley.util.roundedOutline

/**
 * Intro page content
 *
 * @param modifier generic modifier
 * @param introPage intro page object with title and image resource ids
 * @param showButton whether the intro page has a button
 * @param buttonText intro page button text
 * @param onClickButton intro page button action
 */
@Composable
fun IntroPageContent(
    modifier: Modifier = Modifier,
    introPage: IntroPage,
    showButton: Boolean = false,
    buttonText: String = "",
    onClickButton: () -> Unit = {}
) {
    val dim = LocalDim.current
    val context = LocalContext.current
    val resourceId =
        if (context.isDarkMode()) introPage.imageNightResource else introPage.imageResource
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dim.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .weight(0.65f)
                .padding(horizontal = dim.large)
                .then(
                    if (introPage.isScreenshot) Modifier.roundedOutline() else Modifier
                ),
            painter = painterResource(id = resourceId),
            contentDescription = "intro page image"
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dim.large, horizontal = dim.veryLarge),
            text = stringResource(introPage.titleResource),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dim.extraLarge),
            text = stringResource(introPage.descriptionResource),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        if (showButton) {
            BigSpacer()
            Button(
                onClick = onClickButton,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Composable
@Preview
fun IntroPageContentPreview() {
    PileyTheme(useDarkTheme = true) {
        IntroPageContent(modifier = Modifier.fillMaxSize(), introPage = IntroPage.Welcome)
    }
}

@Composable
@Preview(showBackground = true)
fun IntroPageContentScreenshotPreview() {
    PileyTheme(useDarkTheme = false) {
        IntroPageContent(modifier = Modifier.fillMaxSize(), introPage = IntroPage.Piles)
    }
}

@Composable
@Preview
fun IntroPageContentWithButtonPreview() {
    PileyTheme(useDarkTheme = true) {
        IntroPageContent(
            modifier = Modifier.fillMaxSize(),
            introPage = IntroPage.End,
            showButton = true,
            buttonText = "Start Piling"
        )
    }
}


