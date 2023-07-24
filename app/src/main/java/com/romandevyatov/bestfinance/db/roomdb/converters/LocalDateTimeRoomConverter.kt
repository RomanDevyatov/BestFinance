package com.romandevyatov.bestfinance.db.roomdb.converters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeRoomTypeConverter {

    @RequiresApi(Build.VERSION_CODES.O)
    private val iso8601DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")//DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun customFormatDateStringToLocalDateTime(iso8601DateString: String?): LocalDateTime? {
        if (iso8601DateString == null)
            return null
        return LocalDateTime.from(iso8601DateTimeFormatter.parse(iso8601DateString))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun localDateTimeToCustomFormatTimeString(date: LocalDateTime?): String? {
        if (date != null) {
            return date.format(iso8601DateTimeFormatter)
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
