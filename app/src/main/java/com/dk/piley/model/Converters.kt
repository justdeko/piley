package com.dk.piley.model

import androidx.room.TypeConverter
import com.dk.piley.util.toLocalDateTime
import com.dk.piley.util.toTimestamp
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.toLocalDateTime()
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toTimestamp()
    }

    @TypeConverter
    fun fromTimeStamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun instantToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilli()
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