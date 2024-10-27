package com.dk.piley.model.pile

import androidx.sqlite.db.SimpleSQLiteQuery
import com.dk.piley.model.user.PileMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull

/**
 * Pile repository for performing database operations regarding piles
 *
 * @property pileDao the pile dao to use when performing operations
 */
class PileRepository(private val pileDao: PileDao) {
    fun getPilesWithTasks(): Flow<List<PileWithTasks>> = pileDao.getPilesWithTasks()

    fun getPileById(pileId: Long): Flow<PileWithTasks> = pileDao.getPileById(pileId).filterNotNull()

    suspend fun insertPile(pile: Pile): Long {
        return pileDao.insertPile(pile)
    }

    suspend fun updatePile(pile: Pile) {
        pileDao.updatePile(pile)
    }

    suspend fun deletePile(pile: Pile) = pileDao.deletePile(pile)

    suspend fun deleteAllPiles() = pileDao.deleteAllPiles()

    /**
     * Reset pile modes to a specific default pile mode
     *
     * @param defaultPileMode the default pile mode (free if not specified)
     * @return query integer
     */
    suspend fun resetPileModes(defaultPileMode: PileMode = PileMode.FREE): Int {
        return pileDao.updateAllPileModes(defaultPileMode)
    }

    /**
     * Delete pile data by deleting all tasks belonging to pile and pile itself
     *
     */
    suspend fun deletePileData() = pileDao.deletePileData()

    /**
     * Perform database checkpoint by forcing a write to the wal file and thus keeping the main
     * database file up-to-date
     *
     */
    suspend fun performDatabaseCheckpoint() {
        // wal checkpoint with truncating wal file
        pileDao.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(truncate)"))
    }

    /**
     * Delete all tasks with status DELETED
     *
     */
    suspend fun deleteDeletedTasks() = pileDao.deleteDeletedTasks()
}