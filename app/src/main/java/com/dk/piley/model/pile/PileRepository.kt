package com.dk.piley.model.pile

import com.dk.piley.model.user.PileMode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PileRepository @Inject constructor(
    private val pileDao: PileDao
) {
    fun getPilesWithTasks(): Flow<List<PileWithTasks>> = pileDao.getPilesWithTasks()

    fun getPileById(pileId: Long): Flow<PileWithTasks> = pileDao.getPileById(pileId)

    suspend fun insertPile(pile: Pile): Long {
        return pileDao.insertPile(pile)
    }

    suspend fun deletePile(pile: Pile): Void {
        return pileDao.deletePile(pile)
    }

    suspend fun resetPileModes(defaultPileMode: PileMode = PileMode.FREE): Int {
        return pileDao.updateAllPileModes(defaultPileMode)
    }
}