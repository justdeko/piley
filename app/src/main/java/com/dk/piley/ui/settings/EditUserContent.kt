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
 * Edit user dialog content
 *
 * @param modifier generic modifier
 * @param existingName existing user name to set as initial value
 * @param userIsOffline whether the user is an offline user
 * @param onConfirm on confirm user edit
 * @param onCancel on cancel user edit
 */
@Composable
fun EditUserContent(
    modifier: Modifier = Modifier,
    existingName: String,
    userIsOffline: Boolean = false,
    onConfirm: (EditUserResult) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var name by rememberSaveable { mutableStateOf(existingName) }
    var oldPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
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
            text = stringResource(R.string.edit_user_dialog_title),
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
        if (!userIsOffline) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = newPassword,
                onValueChange = { newPassword = it },
                visualTransformation = PasswordVisualTransformation(),
                placeholder = { Text(stringResource(R.string.user_new_password_hint)) },
                shape = MaterialTheme.shapes.large,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Password
                ),
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = oldPassword,
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = { oldPassword = it },
                placeholder = { Text(stringResource(R.string.user_current_password_hint)) },
                shape = MaterialTheme.shapes.large,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    defaultKeyboardAction(ImeAction.Done)
                }),
            )
        }
        TwoButtonRow(
            onRightClick = {
                onConfirm(
                    EditUserResult(
                        name = name,
                        oldPassword = oldPassword,
                        newPassword = newPassword
                    )
                )
            },
            onLeftClick = onCancel,
            rightText = stringResource(R.string.edit_user_dialog_confirm_button),
            leftText = stringResource(R.string.edit_user_dialog_cancel_button),
            rightEnabled = (oldPassword.isNotBlank() && name.isNotBlank()) || (userIsOffline && name.isNotBlank())
        )
    }
}

data class EditUserResult(
    val name: String,
    val oldPassword: String,
    val newPassword: String
)

@Preview
@Composable
fun EditUserContentPreview() {
    PileyTheme(useDarkTheme = true) {
        EditUserContent(
            modifier = Modifier.fillMaxWidth(),
            existingName = "Thomas"
        )
    }
}

@Preview
@Composable
fun EditUserContentOfflinePreview() {
    PileyTheme(useDarkTheme = true) {
        EditUserContent(
            modifier = Modifier.fillMaxWidth(),
            userIsOffline = true,
            existingName = "Thomas"
        )
    }
}