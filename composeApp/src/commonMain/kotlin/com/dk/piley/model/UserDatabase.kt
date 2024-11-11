package com.dk.piley.model

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
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
@ConstructedBy(UserDatabaseConstructor::class)
abstract class UserDatabase : RoomDatabase() {
    // DAOs
    abstract fun userDao(): UserDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object UserDatabaseConstructor : RoomDatabaseConstructor<UserDatabase> {
    override fun initialize(): UserDatabase
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