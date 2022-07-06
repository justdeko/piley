package com.dk.piley.model.task

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(private val taskDao: TaskDao) {
    fun getTasks(): Flow<List<Task>> = taskDao.getTasks()

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun deleteTask(task: Task): Void = taskDao.deleteTask(task)
}