package com.dk.piley.model

import androidx.room.TypeConverter
import com.dk.piley.ui.util.toLocalDateTime
import com.dk.piley.ui.util.toTimestamp
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
}