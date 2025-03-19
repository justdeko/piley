package com.dk.piley.model.backup

import com.dk.piley.model.PILE_DATABASE_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

class DatabaseExporter : IDatabaseExporter {
    override fun exportPileDatabase(): Flow<ExportResult> = flow {
        try {
            val dbPath = getDatabasePath()
            val dbFile = File(dbPath)

            val fileChooser = JFileChooser()
            fileChooser.dialogTitle = "Save Database File"
            fileChooser.fileFilter = FileNameExtensionFilter("Database Files", "db", "sqlite")
            fileChooser.selectedFile = File("$PILE_DATABASE_NAME.db")

            val result = fileChooser.showSaveDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                val selectedFile = fileChooser.selectedFile
                dbFile.copyTo(selectedFile, overwrite = true)
                emit(ExportResult.Success(selectedFile.absolutePath))
            } else {
                emit(ExportResult.Error("Export cancelled"))
            }
        } catch (e: Exception) {
            emit(ExportResult.Error(e.message ?: "Unknown error exporting database"))
        }
    }

    override fun getDatabasePath(): String {
        val userHome = System.getProperty("user.home")
        return "$userHome/.piley/$PILE_DATABASE_NAME.db"
    }

    override fun shareFile(filePath: String) {
        TODO("Not yet implemented")
    }
}