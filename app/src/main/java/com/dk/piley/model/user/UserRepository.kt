package com.dk.piley.model.user

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDao) {
    fun getUsers(): Flow<List<User>> = userDao.getUsers()

    fun getUserById(userId: Long): Flow<User> = userDao.getUserById(userId)

    suspend fun insertUser(user: User): Long = userDao.insertUser(user)

    suspend fun deleteUser(user: User): Void = userDao.deleteUser(user)
}