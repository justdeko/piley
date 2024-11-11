package com.dk.piley.model

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}

fun getPileDatabaseBuilder(): RoomDatabase.Builder<PileDatabase> {
    val dbFile = documentDirectory() + "/" + PILE_DATABASE_NAME
    return Room.databaseBuilder<PileDatabase>(
        name = dbFile
    )
}

fun getUserDatabaseBuilder(): RoomDatabase.Builder<UserDatabase> {
    val dbFile = documentDirectory() + "/" + USER_DATABASE_NAME
    return Room.databaseBuilder<UserDatabase>(
        name = dbFile
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