package com.dk.piley.model.backup

import kotlinx.coroutines.flow.Flow

interface IDatabaseImporter {
    /**
     * Import pile database
     *
     * @return flow of import result
     */
    fun importPileDatabase(): Flow<ImportResult>
}

sealed interface ImportResult {
    data object Success : ExportResult
    data class Error(val message: String) : ExportResult
}