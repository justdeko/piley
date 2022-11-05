package com.dk.piley.model.pile

import androidx.room.*
import com.dk.piley.model.user.PileMode
import kotlinx.coroutines.flow.Flow

@Dao
interface PileDao {
    @Query("SELECT * FROM Pile")
    fun getPilesWithTasks(): Flow<List<PileWithTasks>>

    @Query("SELECT * FROM Pile WHERE pileId=:pileId")
    fun getPileById(pileId: Long): Flow<PileWithTasks>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPile(pile: Pile): Long

    @Query("UPDATE Pile SET pileMode=:pileMode")
    suspend fun updateAllPileModes(pileMode: PileMode): Int

    @Delete
    suspend fun deletePile(pile: Pile): Void
}