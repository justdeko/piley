package com.dk.piley.model.pile

import androidx.sqlite.db.SimpleSQLiteQuery
import com.dk.piley.model.user.PileMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

class PileRepository @Inject constructor(
    private val pileDao: PileDao
) {
    fun getPilesWithTasks(): Flow<List<PileWithTasks>> = pileDao.getPilesWithTasks()

    fun getPileById(pileId: Long): Flow<PileWithTasks> = pileDao.getPileById(pileId).filterNotNull()

    suspend fun insertPile(pile: Pile): Long {
        return pileDao.insertPile(pile)
    }

    suspend fun deletePile(pile: Pile): Void {
        return pileDao.deletePile(pile)
    }

    suspend fun deleteAllPiles(): Void {
        return pileDao.deleteAllPiles()
    }

    suspend fun resetPileModes(defaultPileMode: PileMode = PileMode.FREE): Int {
        return pileDao.updateAllPileModes(defaultPileMode)
    }

    suspend fun deletePileData() = pileDao.deletePileData()

    suspend fun performDatabaseCheckpoint() {
        // wal checkpoint with truncating wal file
        pileDao.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(truncate)"))
    }
}