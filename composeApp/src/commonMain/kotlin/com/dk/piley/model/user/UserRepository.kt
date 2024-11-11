package com.dk.piley.model.user

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest

/**
 * User repository for performing database operations regarding users
 *
 * @property userDao the user dao providing an interface to the database
 * @property userPrefsManager manager of user preferences
 */
class UserRepository(
    private val userDao: UserDao,
    private val userPrefsManager: UserPrefsManager
) {
    private fun getUserByEmailFlow(email: String): Flow<User?> = userDao.getUserByEmailFlow(email)

    suspend fun getSignedInUserEmail(): String =
        userPrefsManager.getUserPrefsEmail().firstOrNull() ?: ""

    suspend fun getTutorialShown(): Boolean =
        userPrefsManager.getTutorialShown().firstOrNull() ?: false

    suspend fun getTasksDeleted(): Boolean =
        userPrefsManager.getTasksDeleted().firstOrNull() ?: false

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSignedInUser(): Flow<User?> =
        userPrefsManager.getUserPrefsEmail().flatMapLatest { email ->
            getUserByEmailFlow(email)
        }

    fun getSignedInUserNotNullFlow(): Flow<User> = getSignedInUser().filterNotNull()

    suspend fun getSignedInUserEntity(): User? = getSignedInUser().firstOrNull()

    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun deleteUser(user: User) = userDao.deleteUser(user)

    suspend fun deleteUserTable() = userDao.deleteUserTable()

    suspend fun setSignedInUser(userEmail: String) {
        userPrefsManager.setSignedInUser(userEmail)
    }

    suspend fun setTutorialShown(shown: Boolean = true) {
        userPrefsManager.setTutorialShown(shown)
    }

    suspend fun setTasksDeleted(deleted: Boolean = false) {
        userPrefsManager.setTasksDeleted(deleted)
    }
}