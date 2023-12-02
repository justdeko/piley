package com.dk.piley.ui.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.R
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.BigSpacer
import com.dk.piley.util.isDarkMode
import com.dk.piley.util.roundedOutline

/**
 * Intro page content containing a text field and a button
 *
 * @param modifier generic modifier
 * @param introPage intro page object with title and image resource ids
 * @param textFieldHint hint to be displayed in the text field
 * @param textMaxLength the maximum length of the text. infinite by default
 * @param buttonText intro page button text
 * @param buttonAlwaysEnabled whether the button is always enabled
 * @param onClickButton intro page button action that passes text entered into text field
 */
@Composable
fun IntroPageTextFieldContent(
    modifier: Modifier = Modifier,
    introPage: IntroPage,
    textFieldHint: String? = null,
    textMaxLength: Int = -1,
    buttonText: String,
    buttonAlwaysEnabled: Boolean = true,
    onClickButton: (String) -> Unit = {}
) {
    var textValue by rememberSaveable { mutableStateOf("") }
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
        BigSpacer()
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dim.extraLarge),
            value = textValue,
            onValueChange = {
                if (it.length <= textMaxLength) {
                    textValue = it
                }
            },
            placeholder = {
                if (textFieldHint != null) {
                    Text(textFieldHint)
                }
            },
            shape = MaterialTheme.shapes.large,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
        BigSpacer()
        Button(
            onClick = { onClickButton(textValue) },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = textValue.isNotBlank() || buttonAlwaysEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text(text = buttonText)
        }
    }
}

@Composable
@Preview
fun IntroPageTextFieldContentPreview() {
    PileyTheme(useDarkTheme = true) {
        IntroPageTextFieldContent(
            modifier = Modifier.fillMaxSize(),
            introPage = IntroPage.End,
            buttonText = stringResource(R.string.finish_intro_button)
        )
    }
}

@Composable
@Preview(device = "spec:parent=pixel_5,orientation=landscape")
fun IntroPageTextFieldContentPreviewHorizontal() {
    PileyTheme(useDarkTheme = true) {
        IntroPageTextFieldContent(
            modifier = Modifier.fillMaxSize(),
            introPage = IntroPage.End,
            buttonText = stringResource(R.string.finish_intro_button)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun IntroPageTextFieldContentLightModePreview() {
    PileyTheme(useDarkTheme = false) {
        IntroPageTextFieldContent(
            modifier = Modifier.fillMaxSize(),
            introPage = IntroPage.End,
            buttonText = stringResource(R.string.finish_intro_button)
        )
    }
}


