package com.dk.piley.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import com.dk.piley.util.defaultPadding

/**
 * Delete user content
 *
 * @param modifier generic modifier
 * @param onConfirm on delete confirm
 * @param userIsOffline whether the user is an offline user
 * @param onCancel on delete cancel
 */
@Composable
fun DeleteUserContent(
    modifier: Modifier = Modifier,
    userIsOffline: Boolean = false,
    onConfirm: (String) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var password by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier.defaultPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LocalDim.current.large)
    ) {
        Text(
            text = stringResource(R.string.delete_user_dialog_title),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.delete_user_dialog_description),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        if (!userIsOffline) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                placeholder = { Text(stringResource(R.string.user_password_confirm_hint)) },
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
                visualTransformation = PasswordVisualTransformation(),
            )
        }
        TwoButtonRow(
            onRightClick = { onConfirm(password) },
            onLeftClick = onCancel,
            rightText = stringResource(R.string.delete_user_dialog_confirm_button),
            leftText = stringResource(R.string.delete_user_dialog_cancel_button),
            rightEnabled = password.isNotBlank() || userIsOffline,
            rightColors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        )
    }
}

@Preview
@Composable
fun DeleteUserContentPreview() {
    PileyTheme(useDarkTheme = true) {
        DeleteUserContent(
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
fun DeleteUserOfflineContentPreview() {
    PileyTheme(useDarkTheme = true) {
        DeleteUserContent(
            modifier = Modifier.fillMaxWidth(),
            userIsOffline = true
        )
    }
}