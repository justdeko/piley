package com.dk.piley.util

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AlertDialogHelper(
    title: String,
    description: String,
    confirmText: String = "OK",
    dismissText: String = "Cancel",
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    AlertDialog(
        title = { Text(text = title) },
        text = { Text(text = description) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmText) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(dismissText) } },
        onDismissRequest = onDismiss
    )
}