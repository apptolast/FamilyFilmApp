package com.apptolast.familyfilmapp.room.converters

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * Replaces the legacy Gson-backed converter with kotlinx.serialization so the
 * persistence layer doesn't drag a JVM-only JSON parser into commonMain. The
 * JSON shape on disk is identical (`["a","b"]`), so existing rows decode
 * untouched after the migration.
 */
class StringListConverter {

    @TypeConverter
    fun stringListToJson(stringList: List<String>): String =
        json.encodeToString(serializer, stringList)

    @TypeConverter
    fun toStringList(jsonString: String): List<String> =
        json.decodeFromString(serializer, jsonString)

    private companion object {
        val serializer = ListSerializer(String.serializer())
        val json = Json { ignoreUnknownKeys = true }
    }
}
