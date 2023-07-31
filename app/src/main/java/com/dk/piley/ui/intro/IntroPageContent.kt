package com.dk.piley.ui.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun IntroPageContent(
    modifier: Modifier = Modifier,
    introPage: IntroPage,
    showButton: Boolean = false,
    buttonText: String = "",
    onClickButton: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxHeight(0.65f)
                .fillMaxWidth(0.8f),
            painter = painterResource(id = introPage.imageResource),
            contentDescription = "intro page image"
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 32.dp),
            text = stringResource(introPage.titleResource),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .padding(top = 16.dp),
            text = stringResource(introPage.descriptionResource),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        if (showButton) {
            Spacer(modifier = Modifier.size(16.dp))
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
@Preview
fun IntroPageContentWithButtonPreview() {
    PileyTheme(useDarkTheme = true) {
        IntroPageContent(
            modifier = Modifier.fillMaxSize(),
            introPage = IntroPage.Welcome,
            showButton = true,
            "Start Piling"
        )
    }
}


