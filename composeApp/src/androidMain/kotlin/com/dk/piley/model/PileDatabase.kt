package com.dk.piley.model

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileDao
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskDao

const val DATABASE_NAME = "piley-db"

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
abstract class PileDatabase : RoomDatabase() {
    // DAOs
    abstract fun taskDao(): TaskDao
    abstract fun pileDao(): PileDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: PileDatabase? = null

        fun getInstance(context: Context): PileDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): PileDatabase {
            return Room
                .databaseBuilder(context, PileDatabase::class.java, DATABASE_NAME)
                .setJournalMode(JournalMode.TRUNCATE)
                .build()
        }
    }
}