package com.dk.piley.model.task

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM Task")
    abstract fun getTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTask(task: Task): Long

    @Delete
    abstract suspend fun deleteTask(task: Task): Void
}