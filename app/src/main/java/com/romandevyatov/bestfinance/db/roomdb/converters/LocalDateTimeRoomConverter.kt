package com.romandevyatov.bestfinance.db.roomdb.converters

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


//ISO_LOCAL_TIME = new DateTimeFormatterBuilder()
//.appendValue(HOUR_OF_DAY, 2)
//.appendLiteral(':')
//.appendValue(MINUTE_OF_HOUR, 2)
//.optionalStart()
//.appendLiteral(':')
//.appendValue(SECOND_OF_MINUTE, 2)
//.optionalStart()
//.appendFraction(NANO_OF_SECOND, 0, 9, true)
//.toFormatter(ResolverStyle.STRICT, null);
