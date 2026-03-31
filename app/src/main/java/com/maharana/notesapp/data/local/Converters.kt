package com.maharana.notesapp.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.maharana.notesapp.data.local.entity.ChecklistItem

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: String): List<String> {
        return gson.fromJson(value, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromChecklistItemList(value: String): List<ChecklistItem> {
        return gson.fromJson(value, object : TypeToken<List<ChecklistItem>>() {}.type)
    }

    @TypeConverter
    fun toChecklistItemList(list: List<ChecklistItem>): String {
        return gson.toJson(list)
    }
}
