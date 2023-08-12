package com.dk.piley.model.pile

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.dk.piley.model.user.PileMode
import kotlinx.coroutines.flow.Flow


@Dao
interface PileDao {
    @Transaction
    @Query("SELECT * FROM Pile")
    fun getPilesWithTasks(): Flow<List<PileWithTasks>>

    @Query("SELECT * FROM Pile WHERE pileId=:pileId")
    fun getPileById(pileId: Long): Flow<PileWithTasks>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPile(pile: Pile): Long

    @Query("UPDATE Pile SET pileMode=:pileMode")
    suspend fun updateAllPileModes(pileMode: PileMode): Int

    @Query("DELETE FROM Pile")
    suspend fun deleteAllPiles(): Void

    @Delete
    suspend fun deletePile(pile: Pile): Void

    @Query("DELETE FROM Pile")
    suspend fun deletePileTable(): Void


    @Query("DELETE FROM Task")
    suspend fun deleteTaskTable(): Void

    @Transaction
    suspend fun deletePileData() {
        deletePileTable()
        deleteTaskTable()
    }

    @RawQuery
    suspend fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int
}