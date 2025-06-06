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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.dk.piley.util.pileTitleCharacterLimit
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.create_pile_dialog_title
import piley.composeapp.generated.resources.pile_create_dialog_confirm_button_text
import piley.composeapp.generated.resources.pile_create_dialog_dismiss_button_text
import piley.composeapp.generated.resources.pile_title_hint

/**
 * Alert dialog for creating a new pile
 *
 * @param modifier generic modifier
 * @param existingPileTitles list of existing pile titles
 * @param onDismiss on dialog dismiss
 * @param onConfirm on confirm button click with title text
 * @param confirmEnabled whether  confirm button enabled
 */
@Composable
fun CreatePileAlertDialog(
    modifier: Modifier = Modifier,
    existingPileTitles: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    confirmEnabled: Boolean = true
) {
    var title by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        modifier = modifier,
        title = { Text(stringResource(Res.string.create_pile_dialog_title)) },
        text = {
            val focusManager = LocalFocusManager.current // dialog has own focus manager
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = title,
                onValueChange = {
                    if (it.length <= pileTitleCharacterLimit) {
                        title = it
                    }
                },
                placeholder = { Text(stringResource(Res.string.pile_title_hint)) },
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
                onClick = {
                    onConfirm(title)
                    title = ""
                },
                enabled = confirmEnabled && title.isNotBlank() && !existingPileTitles.contains(title)
            ) {
                Text(stringResource(Res.string.pile_create_dialog_confirm_button_text))
            }
        }, dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.pile_create_dialog_dismiss_button_text))
            }
        })
}
