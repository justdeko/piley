package com.dk.piley.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskDao

@Database(entities = [Pile::class, Task::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PileDatabase : RoomDatabase() {
    // DAOs
    abstract fun taskDao(): TaskDao

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
            val callback: Callback = object : Callback() {}
            return Room.databaseBuilder(context, PileDatabase::class.java, "piley-db")
                .addCallback(callback)
                .build()
        }
    }
}