package com.dk.piley.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileDao
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskDao
import com.dk.piley.model.user.User
import com.dk.piley.model.user.UserDao

@Database(entities = [Pile::class, Task::class, User::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PileDatabase : RoomDatabase() {
    // DAOs
    abstract fun taskDao(): TaskDao
    abstract fun pileDao(): PileDao
    abstract fun userDao(): UserDao

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
            return Room.databaseBuilder(context, PileDatabase::class.java, "piley-db").build()
        }
    }
}