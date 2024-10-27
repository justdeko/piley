package com.dk.piley.model

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.dk.piley.model.user.User
import com.dk.piley.model.user.UserDao

const val USER_DATABASE_NAME = "piley-db-users"

/**
 * User database containing the user and its preferences
 *
 */
@Database(
    entities = [User::class],
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2
        ),
        AutoMigration(
            from = 2,
            to = 3,
            spec = TwoToThreeMigrationSpec::class
        ),
        AutoMigration(
            from = 3,
            to = 4,
            spec = ThreeToFourMigrationSpec::class
        ),
    ],
    version = 4,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class UserDatabase : RoomDatabase() {
    // DAOs
    abstract fun userDao(): UserDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): UserDatabase {
            return Room.databaseBuilder(context, UserDatabase::class.java, USER_DATABASE_NAME)
                .build()
        }
    }
}

@DeleteColumn(tableName = "User", columnName = "defaultReminderDelay")
class TwoToThreeMigrationSpec : AutoMigrationSpec

@DeleteColumn(tableName = "User", columnName = "password")
@DeleteColumn(tableName = "User", columnName = "lastBackup")
@DeleteColumn(tableName = "User", columnName = "lastBackupQuery")
@DeleteColumn(tableName = "User", columnName = "defaultBackupFrequency")
@DeleteColumn(tableName = "User", columnName = "isOffline")
@DeleteColumn(tableName = "User", columnName = "loadBackupAfterDays")
class ThreeToFourMigrationSpec : AutoMigrationSpec