package com.dk.piley.ui.piles

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.R
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.pileTitleCharacterLimit

/**
 * Alert dialog for creating a new pile
 *
 * @param modifier generic modifier
 * @param pileTitleValue title text value
 * @param onTitleValueChange on title text change
 * @param onDismiss on dialog dismiss
 * @param onConfirm on confirm button click
 * @param confirmEnabled whether  confirm button enabled
 */
@Composable
fun CreatePileAlertDialog(
    modifier: Modifier = Modifier,
    pileTitleValue: String,
    onTitleValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmEnabled: Boolean
) {
    AlertDialog(
        modifier = modifier,
        title = { Text(stringResource(R.string.create_pile_dialog_title)) },
        text = {
            val focusManager = LocalFocusManager.current // dialog has own focus manager
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = pileTitleValue,
                onValueChange = {
                    if (it.length <= pileTitleCharacterLimit) {
                        onTitleValueChange(it)
                    }
                },
                placeholder = { Text(stringResource(R.string.pile_title_hint)) },
                shape = MaterialTheme.shapes.large,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    defaultKeyboardAction(ImeAction.Done)
                })
            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = confirmEnabled
            ) {
                Text(stringResource(R.string.pile_create_dialog_confirm_button_text))
            }
        }, dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.pile_create_dialog_dismiss_button_text))
            }
        })
}

@Preview
@Composable
fun CreatePileAlertDialogPreview() {
    PileyTheme {
        CreatePileAlertDialog(
            pileTitleValue = "some pile name",
            onTitleValueChange = {},
            onDismiss = {},
            onConfirm = {},
            confirmEnabled = true
        )
    }
}