package com.dk.piley.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.R
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.common.TwoButtonRow
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.usernameCharacterLimit

/**
 * Make user online content
 *
 * @param modifier generic modifier
 * @param existingName existing user name to set as initial value
 * @param onConfirm on confirm user online
 * @param onCancel on cancel make user online
 */
@Composable
fun MakeUserOnlineContent(
    modifier: Modifier = Modifier,
    existingName: String,
    onConfirm: (MakeUserOnlineResult) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var name by rememberSaveable { mutableStateOf(existingName) }
    var password by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var serverUrl by rememberSaveable { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    Column(
        modifier.padding(
            horizontal = LocalDim.current.veryLarge,
            vertical = LocalDim.current.large
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LocalDim.current.large)
    ) {
        Text(
            text = stringResource(R.string.make_user_online_title),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = {
                if (name.length <= usernameCharacterLimit) {
                    name = it
                }
            },
            placeholder = { Text(stringResource(R.string.user_name_hint)) },
            shape = MaterialTheme.shapes.large,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = email,
            onValueChange = { email = it },
            placeholder = { Text(stringResource(R.string.user_email_placeholder)) },
            shape = MaterialTheme.shapes.large,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            ),
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = { password = it },
            placeholder = { Text(stringResource(R.string.password_placeholder)) },
            shape = MaterialTheme.shapes.large,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                defaultKeyboardAction(ImeAction.Done)
            }),
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = serverUrl,
            onValueChange = { serverUrl = it },
            placeholder = { Text(stringResource(id = R.string.request_url_hint)) },
            shape = MaterialTheme.shapes.large,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                defaultKeyboardAction(ImeAction.Done)
            }),
        )
        TwoButtonRow(
            onRightClick = {
                onConfirm(
                    MakeUserOnlineResult(
                        name = name,
                        email = email,
                        password = password,
                        serverUrl = serverUrl
                    )
                )
            },
            onLeftClick = onCancel,
            rightText = stringResource(R.string.make_user_online_confirm_button),
            leftText = stringResource(R.string.edit_user_dialog_cancel_button),
            rightEnabled = password.isNotBlank() && name.isNotBlank() && email.isNotBlank() && serverUrl.isNotBlank()
        )
    }
}

data class MakeUserOnlineResult(
    val name: String,
    val email: String,
    val password: String,
    val serverUrl: String
)

@Preview
@Composable
fun MakeUserOnlineContentPreview() {
    PileyTheme(useDarkTheme = true) {
        MakeUserOnlineContent(
            modifier = Modifier.fillMaxWidth(),
            existingName = "Thomas"
        )
    }
}
