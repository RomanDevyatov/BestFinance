package com.romandevyatov.bestfinance.db.roomdb.converters


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.util.*


class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    @TypeConverter
//    fun fromTimestamp(dateString: String?): LocalDateTime? {
//        return if (dateString == null) {
//            null
//        } else {
//            LocalDateTime.parse(dateString)
//        }
//    }
//
//    @TypeConverter
//    fun dateToTimestamp(date: LocalDateTime?): String? {
//        return date?.toString()
//    }

}
