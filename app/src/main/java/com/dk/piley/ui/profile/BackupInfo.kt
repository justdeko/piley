package com.dk.piley.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.ui.util.dateTimeString
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.LocalDateTime

@Composable
fun BackupInfo(
    modifier: Modifier = Modifier,
    lastBackup: LocalDateTime?,
    onClickBackup: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            if (lastBackup != null) {
                Text(
                    text = "Last backup",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Start
                )
            }
            Text(
                text = lastBackup?.dateTimeString()
                    ?: "No backup yet.\nClick on the icon on the right side to create your first backup",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start
            )
        }
        IconButton(onClick = onClickBackup) {
            Icon(
                Icons.Filled.Backup,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = "create a new backup"
            )
        }
    }
}

@Preview
@Composable
fun BackupInfoPreview() {
    AndroidThreeTen.init(LocalContext.current)
    PileyTheme(useDarkTheme = true) {
        BackupInfo(
            lastBackup = LocalDateTime.now()
        )
    }
}

@Preview
@Composable
fun BackupInfoNoBackupPreview() {
    AndroidThreeTen.init(LocalContext.current)
    PileyTheme(useDarkTheme = true) {
        BackupInfo(
            lastBackup = null
        )
    }
}