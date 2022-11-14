package com.dk.piley.model.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val userPrefs: DataStore<Preferences>
) {
    // preference keys
    private val signedInUser = longPreferencesKey("signed_in_user_id")

    fun getUsers(): Flow<List<User>> = userDao.getUsers()

    private fun getUserById(userId: Long): Flow<User?> = userDao.getUserById(userId)

    fun getUserByEmail(email: String): Flow<User?> = userDao.getUserByEmail(email)

    suspend fun setSignedInUser(id: Long) = userPrefs.edit { prefs ->
        prefs[signedInUser] = id
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSignedInUser(): Flow<User?> =
        userPrefs.data.map { prefs -> prefs[signedInUser] ?: -1 }.flatMapLatest { id ->
            getUserById(id)
        }

    fun getSignedInUserNotNull(): Flow<User> = getSignedInUser().filterNotNull()

    suspend fun insertUser(user: User): Long = userDao.insertUser(user)

    suspend fun deleteUser(user: User): Void = userDao.deleteUser(user)
}