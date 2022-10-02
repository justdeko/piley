package com.dk.piley.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dk.piley.R
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileDao
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Pile::class, Task::class], version = 1, exportSchema = false)
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
            val callback: Callback = object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    CoroutineScope(Dispatchers.IO).launch {
                        getInstance(context).pileDao().insertPile(
                            Pile(
                                pileId = 1, name = context.getString(R.string.default_pile_name)
                            )
                        )
                    }
                }
            }
            return Room.databaseBuilder(context, PileDatabase::class.java, "piley-db")
                .addCallback(callback).build()
        }
    }
}