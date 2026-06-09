package com.example.a210288_syaima_drnazatulaini_project2.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromTimeStamp(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(",").filter { it.isNotBlank() }
    }
}