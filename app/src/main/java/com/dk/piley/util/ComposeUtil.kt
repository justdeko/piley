package com.dk.piley.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AlertDialogHelper(
    title: String,
    description: String,
    confirmText: String = "OK",
    dismissText: String? = "Cancel",
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    AlertDialog(
        title = { Text(text = title) },
        text = { Text(text = description) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmText) } },
        dismissButton = {
            if (dismissText != null) {
                TextButton(onClick = onDismiss) { Text(dismissText) }
            }
        },
        onDismissRequest = onDismiss
    )
}

@Composable
fun IndefiniteProgressBar(modifier: Modifier = Modifier, visible: Boolean = false) {
    AnimatedVisibility(visible) {
        LinearProgressIndicator(modifier = modifier.fillMaxWidth())
    }
}
