package com.dk.piley.model.backup

import com.dk.piley.model.PILE_DATABASE_NAME
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.copyTo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

class DatabaseExporter : IDatabaseExporter {
    override fun exportPileDatabase(): Flow<ExportResult> = flow {
        try {
            val dbFile = File(getDatabasePath())
            val fileDialog = FileDialog(null as Frame?, "Save Database File", FileDialog.SAVE)
            fileDialog.file = "$PILE_DATABASE_NAME.db"
            fileDialog.isVisible = true

            // Check if a file was selected or if the dialog was canceled
            val selectedFile = if (fileDialog.file != null && fileDialog.directory != null) {
                File(fileDialog.directory, fileDialog.file)
            } else {
                null
            }

            if (selectedFile != null) {
                dbFile.copyTo(selectedFile, overwrite = true)
                emit(ExportResult.Success(path = selectedFile.absolutePath, showAction = false))
            } else {
                emit(ExportResult.Error("Export cancelled"))
            }
        } catch (e: Exception) {
            emit(ExportResult.Error(e.message ?: "Unknown error exporting database"))
        }
    }

    override fun importPileDatabase(file: PlatformFile): Flow<ImportResult> {
        return flow {
            try {
                val dbFile = File(System.getProperty("java.io.tmpdir"), PILE_DATABASE_NAME)
                file.copyTo(PlatformFile(dbFile))
                emit(ImportResult.Success)
            } catch (e: Exception) {
                emit(ImportResult.Error(e.message ?: "Unknown error importing database"))
            }
        }
    }

    override fun getDatabasePath(): String =
        File(System.getProperty("java.io.tmpdir"), PILE_DATABASE_NAME).absolutePath

    override fun shareFile(filePath: String) {
        // not used
    }
}