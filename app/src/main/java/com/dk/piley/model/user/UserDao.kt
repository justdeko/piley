package com.dk.piley.model.user

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    fun getUsers(): Flow<List<User>>

    @Query("SELECT * FROM User WHERE userId=:userId")
    fun getUserById(userId: Long): Flow<User>

    @Query("SELECT * FROM User WHERE email=:email")
    fun getUserByEmail(email: String): Flow<User?>

    @Query("SELECT * FROM User WHERE userId=:userId")
    fun getUserWithPilesById(userId: Long): Flow<UserWithPiles>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Delete
    suspend fun deleteUser(user: User): Void
}