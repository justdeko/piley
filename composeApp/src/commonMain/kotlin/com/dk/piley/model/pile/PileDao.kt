package com.dk.piley.model.pile

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
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
    suspend fun deleteAllPiles()

    @Delete
    suspend fun deletePile(pile: Pile)

    @Query("DELETE FROM Pile")
    suspend fun deletePileTable()


    @Query("DELETE FROM Task")
    suspend fun deleteTaskTable()

    @Query("DELETE FROM sqlite_sequence")
    suspend fun clearSequence()

    @Transaction
    suspend fun deletePileData() {
        deletePileTable()
        deleteTaskTable()
        // relevant to reset autoincrement ids for pile and task
        // as otherwise first new pile entry starts where autoincrement left off
        // https://stackoverflow.com/q/50878734
        clearSequence()
    }

    @Transaction
    @Query("DELETE FROM TASK WHERE status='DELETED'")
    suspend fun deleteDeletedTasks()
}