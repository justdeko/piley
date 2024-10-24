package com.dk.piley.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.R
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.dateTimeString
import com.dk.piley.util.toLocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime

/**
 * User backup info section
 *
 * @param modifier generic modifier
 * @param lastBackup last backup timestamp
 * @param onClickBackup on click backup action
 */
@Composable
fun BackupInfo(
    modifier: Modifier = Modifier,
    lastBackup: LocalDateTime?,
    onClickBackup: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .padding(horizontal = LocalDim.current.large)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            if (lastBackup != null) {
                Text(
                    text = stringResource(R.string.last_backup_label),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Start
                )
            }
            Text(
                text = lastBackup?.dateTimeString() ?: stringResource(R.string.no_backup_text),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start
            )
        }
        IconButton(onClick = onClickBackup) {
            Icon(
                Icons.Filled.Backup,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "create a new backup"
            )
        }
    }
}

@Preview
@Composable
fun BackupInfoPreview() {
    PileyTheme(useDarkTheme = true) {
        BackupInfo(
            lastBackup = Clock.System.now().toLocalDateTime()
        )
    }
}

@Preview
@Composable
fun BackupInfoNoBackupPreview() {
    PileyTheme(useDarkTheme = true) {
        BackupInfo(
            lastBackup = null
        )
    }
}