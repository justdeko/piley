package com.dk.piley.model.backup

import com.dk.piley.model.PILE_DATABASE_NAME
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

class DatabaseExporter : IDatabaseExporter {
    override fun exportPileDatabase(): Flow<ExportResult> = flow {
        try {
            val dbFile = File(System.getProperty("java.io.tmpdir"), PILE_DATABASE_NAME)
            println("file path: ${dbFile.absolutePath}")
            // Use AWT FileDialog instead of JFileChooser for native look and feel
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
        TODO("Not yet implemented")
    }

    override fun getDatabasePath(): String {
        val userHome = System.getProperty("user.home")
        return "$userHome/.piley/$PILE_DATABASE_NAME.db"
    }

    override fun shareFile(filePath: String) {
        TODO("Not yet implemented")
    }
}