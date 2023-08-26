package com.dk.piley.ui.common

import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Alert dialog with custom content
 *
 * @param modifier default modifier
 * @param onDismiss action on dialog dismiss
 * @param content dialog content
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentAlertDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            modifier = modifier
                .wrapContentHeight()
                .wrapContentWidth(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            content()
        }
    }
}