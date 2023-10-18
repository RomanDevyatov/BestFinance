package com.romandevyatov.bestfinance.data.roomdb.converters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class LocalDateTimeRoomTypeConverter {

    companion object {

        @RequiresApi(Build.VERSION_CODES.O)
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        @RequiresApi(Build.VERSION_CODES.O)
        fun getNowFormatted() : String {
            return (LocalDateTime.now()).format(dateTimeFormatter)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun dateTimeStringToLocalDateTime(dateTimeString: String?): LocalDateTime? {
        if (dateTimeString == null)
            return null
        return LocalDateTime.from(dateTimeFormatter.parse(dateTimeString))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun localDateTimeToCustomFormatTimeString(dateLocalDateTime: LocalDateTime?): String? {
        if (dateLocalDateTime != null) {
            return dateLocalDateTime.format(dateTimeFormatter)
        }
        return null
    }
}
