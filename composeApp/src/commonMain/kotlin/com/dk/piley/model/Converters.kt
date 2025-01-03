package com.dk.piley.model

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

/**
 * Converters to convert complex entity types to primitive ones
 *
 */
class Converters {
    @TypeConverter
    fun fromTimeStamp(value: Long?): Instant? {
        return value?.let { Instant.fromEpochMilliseconds(it) }
    }

    @TypeConverter
    fun instantToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilliseconds()
    }

    @TypeConverter
    fun instantListToString(instantList: List<Instant>?): String? {
        return instantList?.toString()
    }

    @TypeConverter
    fun fromStringToInstantList(instantListString: String?): List<Instant> {
        if (instantListString == "[]" || instantListString.isNullOrBlank()) return emptyList()
        return instantListString.drop(1).dropLast(1).split(", ").map { Instant.parse(it) }
    }
}