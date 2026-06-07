package com.apptolast.familyfilmapp.room.converters

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

// Uses kotlinx.serialization; on-disk JSON shape is identical to the prior Gson encoding.
class StringListConverter {

    @TypeConverter
    fun stringListToJson(stringList: List<String>): String = json.encodeToString(serializer, stringList)

    @TypeConverter
    fun toStringList(jsonString: String): List<String> = json.decodeFromString(serializer, jsonString)

    private companion object {
        val serializer = ListSerializer(String.serializer())
        val json = Json { ignoreUnknownKeys = true }
    }
}
