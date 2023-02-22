package com.romandevyatov.bestfinance.db.roomdb.converters


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


class OffsetDateTimeRoomTypeConverter {

    @RequiresApi(Build.VERSION_CODES.O)
    private val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun iso8601DateStringToOffsetDateTime(iso8601DateString : String): OffsetDateTime {
        return OffsetDateTime.from(iso8601DateTimeFormatter.parse(iso8601DateString))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun offsetDateTimeToIso8601OffsetDateTimeString(date: OffsetDateTime): String {
        return date.format(iso8601DateTimeFormatter)
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
