package com.dk.piley.model

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import java.io.File

fun getPileDatabaseBuilder(): RoomDatabase.Builder<PileDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), PILE_DATABASE_NAME)
    return Room.databaseBuilder<PileDatabase>(
        name = dbFile.absolutePath
    )
}

fun getUserDatabaseBuilder(): RoomDatabase.Builder<UserDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), USER_DATABASE_NAME)
    return Room.databaseBuilder<UserDatabase>(
        name = dbFile.absolutePath
    )
}

fun getPileDatabase(): PileDatabase {
    return getPileDatabaseBuilder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

fun getUserDatabase(): UserDatabase {
    return getUserDatabaseBuilder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}