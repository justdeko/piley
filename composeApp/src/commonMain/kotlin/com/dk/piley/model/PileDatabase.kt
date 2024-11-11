package com.dk.piley.model

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
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
        AutoMigration(
            from = 1,
            to = 2
        ),
        AutoMigration(
            from = 2,
            to = 3
        ),
        AutoMigration(
            from = 3,
            to = 4
        )
    ],
    version = 4,
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