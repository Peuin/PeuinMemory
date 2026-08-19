package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.ItineraryDay
import com.example.data.model.ItineraryItem
import com.example.data.model.PlaceCategory
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        if (value == null) return ""
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun fromPlaceCategory(category: PlaceCategory?): String {
        return category?.name ?: PlaceCategory.ATTRACTION.name
    }

    @TypeConverter
    fun toPlaceCategory(value: String?): PlaceCategory {
        return try {
            PlaceCategory.valueOf(value ?: PlaceCategory.ATTRACTION.name)
        } catch (e: Exception) {
            PlaceCategory.ATTRACTION
        }
    }

    @TypeConverter
    fun fromItineraryDayList(value: List<ItineraryDay>?): String {
        if (value == null) return ""
        val type = Types.newParameterizedType(List::class.java, ItineraryDay::class.java)
        val adapter = moshi.adapter<List<ItineraryDay>>(type)
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toItineraryDayList(value: String?): List<ItineraryDay> {
        if (value.isNullOrEmpty()) return emptyList()
        val type = Types.newParameterizedType(List::class.java, ItineraryDay::class.java)
        val adapter = moshi.adapter<List<ItineraryDay>>(type)
        return adapter.fromJson(value) ?: emptyList()
    }
}
