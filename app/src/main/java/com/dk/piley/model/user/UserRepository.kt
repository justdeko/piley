package com.dk.piley.model.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.dk.piley.model.remote.Resource
import com.dk.piley.model.remote.resourceSuccessfulFlow
import com.dk.piley.model.remote.user.UserApi
import com.dk.piley.model.remote.user.UserRequest
import com.dk.piley.model.remote.user.UserUpdateRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val SIGNED_OUT_USER_ID = -2L

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val userApi: UserApi,
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

    fun updateUserFlow(
        oldUser: User,
        newEmail: String? = null,
        newPassword: String? = null,
        newName: String? = null
    ): Flow<Resource<String>> = resourceSuccessfulFlow {
        val email = newEmail ?: oldUser.email
        val password = newPassword ?: oldUser.password
        val name = newName ?: oldUser.name
        val requestBody = UserUpdateRequest(
            oldEmail = oldUser.email,
            newEmail = email,
            name = name,
            oldPassword = oldUser.password,
            newPassword = password
        )
        userApi.updateUser(requestBody)
    }

    fun registerUserFlow(user: User): Flow<Resource<String>> = resourceSuccessfulFlow {
        val requestBody = UserRequest(user.email, user.name, user.password)
        userApi.createUser(requestBody)
    }

    fun deleteUserFlow(email: String): Flow<Resource<String>> = resourceSuccessfulFlow {
        userApi.deleteUser(email)
    }
}