package com.dk.piley.model.user

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
import timber.log.Timber
import javax.inject.Inject

/**
 * User repository for performing database operations regarding users
 *
 * @property userDao the user dao providing an interface to the database
 * @property userApi the api interface for performing remote user operations
 * @property userPrefsManager manager of user preferences
 */
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val userApi: UserApi,
    private val userPrefsManager: UserPrefsManager
) {
    fun getUsers(): Flow<List<User>> = userDao.getUsers()

    private fun getUserByEmailFlow(email: String): Flow<User?> = userDao.getUserByEmailFlow(email)

    private suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    suspend fun getUserPassword(email: String): String =
        getUserByEmail(email)?.password ?: ""

    suspend fun getSignedInUserEmail(): String =
        userPrefsManager.getUserPrefsEmail().firstOrNull() ?: ""

    suspend fun localCredentials(email: String): String {
        val user = getUserByEmail(email)
        Timber.d("generating credentials using user: $user")
        return credentials(user?.email, user?.password)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSignedInUser(): Flow<User?> =
        userPrefsManager.getUserPrefsEmail().flatMapLatest { email ->
            getUserByEmailFlow(email)
        }

    fun getSignedInUserNotNullFlow(): Flow<User> = getSignedInUser().filterNotNull()

    suspend fun getSignedInUserEntity(): User? = getSignedInUser().firstOrNull()

    suspend fun insertUser(user: User): Void = userDao.insertUser(user)

    suspend fun deleteUser(user: User): Void = userDao.deleteUser(user)

    suspend fun deleteUserTable(): Void = userDao.deleteUserTable()

    fun updateUserFlow(
        oldUser: User,
        newPassword: String? = null,
        newName: String? = null
    ): Flow<Resource<String>> = resourceSuccessfulFlow {
        val password = if (!newPassword.isNullOrBlank()) newPassword else oldUser.password
        val name = if (!newName.isNullOrBlank()) newName else oldUser.name
        val requestBody = UserUpdateRequest(
            email = oldUser.email,
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

    suspend fun setSignedInUser(userEmail: String) {
        userPrefsManager.setSignedInUser(userEmail)
    }

    suspend fun setBaseUrl(url: String) {
        userPrefsManager.setBaseUrl(url)
    }

    fun getBaseUrl() = userPrefsManager.getBaseUrl()
}