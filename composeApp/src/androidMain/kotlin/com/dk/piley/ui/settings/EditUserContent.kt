package com.dk.piley.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.common.TwoButtonRow
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.usernameCharacterLimit
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.edit_user_dialog_cancel_button
import piley.composeapp.generated.resources.edit_user_dialog_confirm_button
import piley.composeapp.generated.resources.edit_user_dialog_title
import piley.composeapp.generated.resources.user_name_hint

/**
 * Edit user dialog content
 *
 * @param modifier generic modifier
 * @param existingName existing user name to set as initial value
 * @param onConfirm on confirm user edit
 * @param onCancel on cancel user edit
 */
@Composable
fun EditUserContent(
    modifier: Modifier = Modifier,
    existingName: String,
    onConfirm: (EditUserResult) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var name by rememberSaveable { mutableStateOf(existingName) }
    Column(
        modifier.padding(
            horizontal = LocalDim.current.veryLarge,
            vertical = LocalDim.current.large
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LocalDim.current.large)
    ) {
        Text(
            text = stringResource(Res.string.edit_user_dialog_title),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = {
                if (it.length <= usernameCharacterLimit) {
                    name = it
                }
            },
            placeholder = { Text(stringResource(Res.string.user_name_hint)) },
            shape = MaterialTheme.shapes.large,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        TwoButtonRow(
            onRightClick = {
                onConfirm(
                    EditUserResult(
                        name = name
                    )
                )
            },
            onLeftClick = onCancel,
            rightText = stringResource(Res.string.edit_user_dialog_confirm_button),
            leftText = stringResource(Res.string.edit_user_dialog_cancel_button),
            rightEnabled = name.isNotBlank()
        )
    }
}

data class EditUserResult(
    val name: String
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
            existingName = "Thomas"
        )
    }
}