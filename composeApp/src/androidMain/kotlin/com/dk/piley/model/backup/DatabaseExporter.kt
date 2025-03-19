package com.dk.piley.model.backup

import android.content.Context
import android.os.Environment
import com.dk.piley.model.PILE_DATABASE_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class DatabaseExporter(
    private val context: Context
) : IDatabaseExporter {
    override fun exportPileDatabase(): Flow<ExportResult> = flow {
        try {
            val dbPath = getDatabasePath()
            val dbFile = File(dbPath)

            val exportDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "")
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            val exportFile = File(exportDir, "$PILE_DATABASE_NAME.db")
            dbFile.copyTo(exportFile, overwrite = true)

            emit(ExportResult.Success(exportFile.absolutePath))
        } catch (e: Exception) {
            emit(ExportResult.Error(e.localizedMessage ?: "Unknown error exporting database"))
        }
    }

    override fun getDatabasePath(): String {
        return context.getDatabasePath(PILE_DATABASE_NAME).absolutePath
    }

    override fun shareFile(filePath: String) {
        TODO("Not yet implemented")
    }
}