package com.dk.piley.model.pile

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.dk.piley.model.user.PileMode
import kotlinx.coroutines.flow.Flow

/**
 * Pile dao with interfaces for database operations regarding piles
 *
 */
@Dao
interface PileDao {
    @Transaction
    @Query("SELECT * FROM Pile")
    fun getPilesWithTasks(): Flow<List<PileWithTasks>>

    @Query("SELECT * FROM Pile WHERE pileId=:pileId")
    fun getPileById(pileId: Long): Flow<PileWithTasks>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPile(pile: Pile): Long

    @Update
    suspend fun updatePile(pile: Pile)

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

    @Transaction
    @Query("DELETE FROM TASK WHERE status='DELETED'")
    suspend fun deleteDeletedTasks(): Void

    @RawQuery
    suspend fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int
}