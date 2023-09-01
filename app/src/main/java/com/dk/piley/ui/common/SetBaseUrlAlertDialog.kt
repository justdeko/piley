package com.dk.piley.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.R
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.MediumSpacer

/**
 * Alert dialog for setting the http request url
 *
 * @param modifier generic modifier
 * @param initialUrlValue initial url text value
 * @param onDismiss on dialog dismiss
 * @param onConfirm on confirm button click with url value as parameter
 */
@Composable
fun CreateBaseUrlAlertDialog(
    modifier: Modifier = Modifier,
    initialUrlValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var urlTextValue by rememberSaveable { mutableStateOf(initialUrlValue) }

    AlertDialog(
        modifier = modifier,
        title = { Text(stringResource(R.string.base_url_dialog_title)) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val focusManager = LocalFocusManager.current // dialog has own focus manager
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = urlTextValue,
                    onValueChange = { urlTextValue = it },
                    placeholder = { Text(stringResource(R.string.base_url_dialog_hint)) },
                    shape = MaterialTheme.shapes.large,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        defaultKeyboardAction(ImeAction.Done)
                    })
                )
                MediumSpacer()
                Text(text = stringResource(R.string.base_url_dialog_hint_2))
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(urlTextValue) },
                enabled = android.util.Patterns.WEB_URL.matcher(urlTextValue).matches()
            ) {
                Text(stringResource(R.string.base_url_dialog_confirm))
            }
        }, dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.base_url_dialog_cancel))
            }
        })
}

@Preview
@Composable
fun CreateBaseUrlAlertDialogPreview() {
    PileyTheme {
        CreateBaseUrlAlertDialog(
            initialUrlValue = "",
            onDismiss = {},
            onConfirm = {},
        )
    }
}