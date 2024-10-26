package com.dk.piley.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
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
 * @param onCancel on delete cancel
 */
@Composable
fun DeleteUserContent(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
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
        TwoButtonRow(
            onRightClick = onConfirm,
            onLeftClick = onCancel,
            rightText = stringResource(R.string.delete_user_dialog_confirm_button),
            leftText = stringResource(R.string.delete_user_dialog_cancel_button),
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
            modifier = Modifier.fillMaxWidth()
        )
    }
}