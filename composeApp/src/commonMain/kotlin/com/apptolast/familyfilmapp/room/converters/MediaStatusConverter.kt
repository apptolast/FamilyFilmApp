package com.apptolast.familyfilmapp.room.converters

import androidx.room.TypeConverter
import com.apptolast.familyfilmapp.model.local.types.MediaStatus

class MediaStatusConverter {
    @TypeConverter
    fun fromMediaStatus(status: MediaStatus): String = status.name

    @TypeConverter
    fun toMediaStatus(value: String): MediaStatus = MediaStatus.valueOf(value)
}
