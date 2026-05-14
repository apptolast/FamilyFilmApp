package com.apptolast.familyfilmapp.room.converters

import androidx.room.TypeConverter
import kotlin.time.Instant

/**
 * Persists [Instant]s as epoch milliseconds (INTEGER column) — same SQL shape
 * as the legacy java.util.Date based converter, so the v1..v10 migrations and
 * any data carried over from a previous install keep working.
 */
class DateConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? = value?.let { Instant.fromEpochMilliseconds(it) }

    @TypeConverter
    fun instantToTimestamp(instant: Instant?): Long? = instant?.toEpochMilliseconds()
}
