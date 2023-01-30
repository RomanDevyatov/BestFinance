package com.romandevyatov.bestfinance.db.roomdb.converters

import androidx.room.TypeConverter
import java.util.Date


class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(value) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

}
