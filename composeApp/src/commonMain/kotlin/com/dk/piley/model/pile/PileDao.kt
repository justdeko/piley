package com.dk.piley.model.pile

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.execSQL
import androidx.room.useWriterConnection
import com.dk.piley.model.PileDatabase
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
    fun getPileById(pileId: Long): Flow<PileWithTasks?>

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

    suspend fun mergeDatabases(db: PileDatabase, secondaryDbPath: String) {
        db.useWriterConnection { connection ->
            connection.execSQL("ATTACH DATABASE '$secondaryDbPath' AS db2")

            // --- Merge Piles ---
            // Insert new piles from db2 where name doesn't exist in main
            connection.execSQL(
                """
            INSERT INTO pile (name, description, pileMode, pileLimit, createdAt, modifiedAt, color)
            SELECT 
                p2.name, p2.description, p2.pileMode, p2.pileLimit,
                p2.createdAt, p2.modifiedAt, p2.color
            FROM db2.pile p2
            WHERE NOT EXISTS (
                SELECT 1 FROM pile p1 WHERE p1.name = p2.name
            )
            """
            )

            // Merge piles with same name or where pileId == 1
            connection.execSQL(
                """
            UPDATE pile
            SET 
                description = (
                    SELECT p2.description FROM db2.pile p2
                    WHERE (p2.name = pile.name OR (pile.pileId = 1 AND p2.pileId = 1))
                    AND p2.modifiedAt > pile.modifiedAt
                ),
                pileMode = (
                    SELECT p2.pileMode FROM db2.pile p2
                    WHERE (p2.name = pile.name OR (pile.pileId = 1 AND p2.pileId = 1))
                    AND p2.modifiedAt > pile.modifiedAt
                ),
                pileLimit = (
                    SELECT p2.pileLimit FROM db2.pile p2
                    WHERE (p2.name = pile.name OR (pile.pileId = 1 AND p2.pileId = 1))
                    AND p2.modifiedAt > pile.modifiedAt
                ),
                modifiedAt = (
                    SELECT p2.modifiedAt FROM db2.pile p2
                    WHERE (p2.name = pile.name OR (pile.pileId = 1 AND p2.pileId = 1))
                    AND p2.modifiedAt > pile.modifiedAt
                ),
                color = (
                    SELECT p2.color FROM db2.pile p2
                    WHERE (p2.name = pile.name OR (pile.pileId = 1 AND p2.pileId = 1))
                    AND p2.modifiedAt > pile.modifiedAt
                )
            WHERE EXISTS (
                SELECT 1 FROM db2.pile p2
                WHERE (p2.name = pile.name OR (pile.pileId = 1 AND p2.pileId = 1))
                AND p2.modifiedAt > pile.modifiedAt
            )
            """
            )

            // --- Merge Tasks ---
            // Insert tasks from db2 that don't exist in main (by title + pile name)
            connection.execSQL(
                """
            INSERT INTO task (
                title, pileId, description, createdAt, modifiedAt, completionTimes, reminder,
                isRecurring, recurringTimeRange, recurringFrequency, nowAsReminderTime, status,
                averageCompletionTimeInHours
            )
            SELECT 
                t2.title,
                (SELECT p1.pileId FROM pile p1 WHERE p1.name = p2.name),
                t2.description, t2.createdAt, t2.modifiedAt, t2.completionTimes,
                t2.reminder, t2.isRecurring, t2.recurringTimeRange, t2.recurringFrequency,
                t2.nowAsReminderTime, t2.status, t2.averageCompletionTimeInHours
            FROM db2.task t2
            JOIN db2.pile p2 ON t2.pileId = p2.pileId
            WHERE NOT EXISTS (
                SELECT 1 FROM task t1
                JOIN pile p1 ON t1.pileId = p1.pileId
                WHERE t1.title = t2.title AND p1.name = p2.name
            )
            """
            )

            // Update tasks if db2 version is newer (match by title + pile name)
            connection.execSQL(
                """
            UPDATE task
            SET 
                description = (
                    SELECT t2.description FROM db2.task t2
                    JOIN db2.pile p2 ON t2.pileId = p2.pileId
                    JOIN pile p1 ON p1.name = p2.name
                    WHERE t2.title = task.title AND p1.pileId = task.pileId
                ),
                modifiedAt = (
                    SELECT t2.modifiedAt FROM db2.task t2
                    JOIN db2.pile p2 ON t2.pileId = p2.pileId
                    JOIN pile p1 ON p1.name = p2.name
                    WHERE t2.title = task.title AND p1.pileId = task.pileId
                ),
                completionTimes = (
                    SELECT t2.completionTimes FROM db2.task t2
                    JOIN db2.pile p2 ON t2.pileId = p2.pileId
                    JOIN pile p1 ON p1.name = p2.name
                    WHERE t2.title = task.title AND p1.pileId = task.pileId
                ),
                reminder = (
                    SELECT t2.reminder FROM db2.task t2
                    JOIN db2.pile p2 ON t2.pileId = p2.pileId
                    JOIN pile p1 ON p1.name = p2.name
                    WHERE t2.title = task.title AND p1.pileId = task.pileId
                ),
                isRecurring = (
                    SELECT t2.isRecurring FROM db2.task t2
                    JOIN db2.pile p2 ON t2.pileId = p2.pileId
                    JOIN pile p1 ON p1.name = p2.name
                    WHERE t2.title = task.title AND p1.pileId = task.pileId
                ),
                recurringTimeRange = (
                    SELECT t2.recurringTimeRange FROM db2.task t2
                    JOIN db2.pile p2 ON t2.pileId = p2.pileId
                    JOIN pile p1 ON p1.name = p2.name
                    WHERE t2.title = task.title AND p1.pileId = task.pileId
                ),
                recurringFrequency = (
                    SELECT t2.recurringFrequency FROM db2.task t2
                    JOIN db2.pile p2 ON t2.pileId = p2.pileId
                    JOIN pile p1 ON p1.name = p2.name
                    WHERE t2.title = task.title AND p1.pileId = task.pileId
                ),
                nowAsReminderTime = (
                    SELECT t2.nowAsReminderTime FROM db2.task t2
                    JOIN db2.pile p2 ON t2.pileId = p2.pileId
                    JOIN pile p1 ON p1.name = p2.name
                    WHERE t2.title = task.title AND p1.pileId = task.pileId
                ),
                status = (
                    SELECT t2.status FROM db2.task t2
                    JOIN db2.pile p2 ON t2.pileId = p2.pileId
                    JOIN pile p1 ON p1.name = p2.name
                    WHERE t2.title = task.title AND p1.pileId = task.pileId
                ),
                averageCompletionTimeInHours = (
                    SELECT t2.averageCompletionTimeInHours FROM db2.task t2
                    JOIN db2.pile p2 ON t2.pileId = p2.pileId
                    JOIN pile p1 ON p1.name = p2.name
                    WHERE t2.title = task.title AND p1.pileId = task.pileId
                )
            WHERE EXISTS (
                SELECT 1 FROM db2.task t2
                JOIN db2.pile p2 ON t2.pileId = p2.pileId
                JOIN pile p1 ON p1.name = p2.name
                WHERE t2.title = task.title AND p1.pileId = task.pileId AND t2.modifiedAt > task.modifiedAt
            )
            """
            )
            connection.execSQL("DETACH DATABASE db2")
        }
    }
}