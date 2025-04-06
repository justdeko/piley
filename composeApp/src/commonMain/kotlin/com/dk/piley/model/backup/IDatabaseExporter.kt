package com.dk.piley.model.backup

import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

interface IDatabaseExporter {
    /**
     * Export pile database
     *
     * @return flow of export result
     */
    fun exportPileDatabase(): Flow<ExportResult>

    /**
     * Import database
     *
     * @param file file to import
     * @return flow of import result
     */
    fun importPileDatabase(file: PlatformFile): Flow<ImportResult>

    /**
     * Get the path of the database
     *
     * @return path of the database
     */
    fun getDatabasePath(): String

    /**
     * Share file
     *
     * @param filePath path of the file to share
     */
    fun shareFile(filePath: String)
}

sealed interface ImportResult {
    data object Success : ImportResult
    data class Error(val message: String) : ImportResult
}

sealed interface ExportResult {
    data class Success(val path: String, val showAction: Boolean) : ExportResult
    data class Error(val message: String) : ExportResult
}