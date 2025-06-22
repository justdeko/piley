package com.dk.piley.model

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileDao
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskDao

internal const val PILE_DATABASE_NAME = "piley-db"

/**
 * Pile database containing user piles and tasks
 *
 */
@Database(
    entities = [Pile::class, Task::class],
    autoMigrations = [
        AutoMigration(1, 2),
        AutoMigration(2, 3),
        AutoMigration(3, 4),
        AutoMigration(4, 5),
        AutoMigration(
            from = 5,
            to = 6,
            spec = FiveToSixMigrationSpec::class
        ),
        AutoMigration(6, 7)
    ],
    version = 7,
    exportSchema = true
)
@TypeConverters(Converters::class)
@ConstructedBy(PileDatabaseConstructor::class)
abstract class PileDatabase : RoomDatabase() {
    // DAOs
    abstract fun taskDao(): TaskDao
    abstract fun pileDao(): PileDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object PileDatabaseConstructor : RoomDatabaseConstructor<PileDatabase> {
    override fun initialize(): PileDatabase
}

@DeleteColumn(tableName = "Pile", columnName = "deletedCount")
class FiveToSixMigrationSpec : AutoMigrationSpec