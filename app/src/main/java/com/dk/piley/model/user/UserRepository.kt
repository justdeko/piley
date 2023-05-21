package com.dk.piley.model.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.dk.piley.model.common.Resource
import com.dk.piley.model.common.resourceSuccessfulFlow
import com.dk.piley.model.remote.user.UserApi
import com.dk.piley.model.remote.user.UserRequest
import com.dk.piley.model.remote.user.UserResponse
import com.dk.piley.model.remote.user.UserUpdateRequest
import com.dk.piley.util.credentials
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val userApi: UserApi,
    private val userPrefs: DataStore<Preferences>
) {
    // preference keys
    private val signedInUser = stringPreferencesKey("signed_in_user_id")

    fun getUsers(): Flow<List<User>> = userDao.getUsers()

    // TODO: make these non-flows because we don't need them as flows
    private fun getUserByEmail(email: String): Flow<User?> = userDao.getUserByEmail(email)

    suspend fun getUserPassword(email: String): String =
        getUserByEmail(email).firstOrNull()?.password ?: ""

    suspend fun getSignedInUserEmail(): String =
        getUserPrefsEmail().firstOrNull() ?: ""

    suspend fun localCredentials(email: String): String {
        val user = getUserByEmail(email).firstOrNull()
        Timber.d("generating credentials using user: $user")
        return credentials(user?.email, user?.password)
    }

    suspend fun setSignedInUser(email: String) = userPrefs.edit { prefs ->
        prefs[signedInUser] = email
    }

    private fun getUserPrefsEmail(): Flow<String> =
        userPrefs.data.map { prefs -> prefs[signedInUser] ?: "" }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSignedInUser(): Flow<User?> =
        getUserPrefsEmail().flatMapLatest { email ->
            getUserByEmail(email)
        }

    fun getSignedInUserNotNull(): Flow<User> = getSignedInUser().filterNotNull()

    suspend fun insertUser(user: User): Void = userDao.insertUser(user)

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
        userApi.updateUser(requestBody, credentials(oldUser.email, oldUser.password))
    }

    fun registerUserFlow(user: User): Flow<Resource<String>> = resourceSuccessfulFlow {
        val requestBody = UserRequest(user.email, user.name, user.password)
        userApi.createUser(requestBody)
    }

    fun deleteUserFlow(email: String, password: String): Flow<Resource<String>> =
        resourceSuccessfulFlow {
            userApi.deleteUser(email, credentials(email, password))
        }

    fun getUserFromRemoteFlow(email: String, password: String): Flow<Resource<UserResponse>> =
        resourceSuccessfulFlow {
            userApi.getUser(email, credentials(email, password))
        }
}