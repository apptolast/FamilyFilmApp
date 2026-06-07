package com.apptolast.familyfilmapp.room.converters

import androidx.room.TypeConverter
import kotlin.time.Instant

// Persists Instants as epoch millis (INTEGER) — matches the prior java.util.Date column shape.
class DateConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? = value?.let { Instant.fromEpochMilliseconds(it) }

    @TypeConverter
    fun instantToTimestamp(instant: Instant?): Long? = instant?.toEpochMilliseconds()
}
