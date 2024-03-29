package com.dk.piley.model.user

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * User dao with interfaces for database operations regarding users
 *
 * @constructor Create empty User dao
 */
@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    fun getUsers(): Flow<List<User>>

    @Query("SELECT * FROM User WHERE email=:email")
    fun getUserByEmailFlow(email: String): Flow<User?>

    @Query("SELECT * FROM User WHERE email=:email")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Void

    @Delete
    suspend fun deleteUser(user: User): Void

    @Query("DELETE FROM User")
    suspend fun deleteUserTable(): Void
}