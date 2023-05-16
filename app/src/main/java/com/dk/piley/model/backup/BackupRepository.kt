package com.dk.piley.model.backup

import com.dk.piley.model.remote.backup.BackupApi
import javax.inject.Inject

class BackupRepository @Inject constructor(
    private val backupApi: BackupApi
) {
}