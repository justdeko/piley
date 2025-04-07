package com.dk.piley.model

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

fun getPileDatabaseBuilder(ctx: Context): RoomDatabase.Builder<PileDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath(PILE_DATABASE_NAME)
    return Room.databaseBuilder<PileDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    ).setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
}

fun getUserDatabaseBuilder(ctx: Context): RoomDatabase.Builder<UserDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath(USER_DATABASE_NAME)
    return Room.databaseBuilder<UserDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}

fun getPileDatabase(
    context: Context
): PileDatabase {
    return getPileDatabaseBuilder(context)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

fun getUserDatabase(
    context: Context
): UserDatabase {
    return getUserDatabaseBuilder(context)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}