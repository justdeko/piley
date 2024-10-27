package com.dk.piley.model.task

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Task dao with interfaces for database operations regarding tasks
 *
 */
@Dao
interface TaskDao {
    @Query("SELECT * FROM Task")
    fun getTasks(): Flow<List<Task>>

    @Query("SELECT * FROM Task WHERE id=:taskId")
    fun getTaskById(taskId: Long): Flow<Task?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Delete
    suspend fun deleteTask(task: Task)

    /**
     * Delete completed and deleted non-recurring tasks for its parent pile
     *
     * @param pileId the parent pile id
     */
    @Query("DELETE FROM Task WHERE pileId=:pileId AND status IN ('DONE', 'DELETED') AND isRecurring='0'")
    suspend fun deleteCompletedDeletedForPile(pileId: Long)
}