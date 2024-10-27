package com.dk.piley.model.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
    suspend fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM User")
    suspend fun deleteUserTable()
}