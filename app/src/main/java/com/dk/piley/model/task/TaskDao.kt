package com.dk.piley.model.task

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM Task")
    fun getTasks(): Flow<List<Task>>

    @Query("SELECT * FROM Task WHERE id=:taskId")
    fun getTaskById(taskId: Long): Flow<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Delete
    suspend fun deleteTask(task: Task): Void

    @Query("DELETE FROM Task WHERE pileId=:pileId AND status IN ('DONE', 'DELETED')")
    suspend fun deleteCompletedDeletedForPile(pileId: Long): Void
}