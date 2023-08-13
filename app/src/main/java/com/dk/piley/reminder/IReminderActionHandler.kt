package com.dk.piley.reminder

import com.dk.piley.model.task.Task
import kotlinx.coroutines.flow.Flow

interface IReminderActionHandler {
    fun show(taskId: Long): Flow<Task>
    fun restartAll(): Flow<List<Task>>
    suspend fun complete(taskId: Long): Flow<Task?>
    suspend fun delay(taskId: Long): Flow<Task?>
}
