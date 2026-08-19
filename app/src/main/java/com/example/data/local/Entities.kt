package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ItineraryDay
import com.example.data.model.PlaceCategory

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val title: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val durationDays: Int,
    val travelersCount: Int,
    val travelStyle: String,
    val totalBudget: Long,
    val pace: String,
    val transportation: String,
    val coverImage: String,
    val days: List<ItineraryDay>,
    val isCurrentActive: Boolean = true
)

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: PlaceCategory,
    val rating: Double,
    val reviewCount: Int,
    val address: String,
    val distanceKm: Double,
    val openingHours: String,
    val priceRange: String,
    val summary: String,
    val whyMatches: String,
    val recommendedDurationMinutes: Int,
    val bestTimeToVisit: String,
    val amenities: List<String>,
    val tips: List<String>,
    val imageUrl: String,
    val lat: Double,
    val lng: Double,
    val isSaved: Boolean = false,
    val destination: String = "Đà Lạt"
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val tripId: String? = null,
    val placeName: String,
    val location: String,
    val date: String,
    val author: String,
    val authorAvatar: String,
    val note: String,
    val mood: String,
    val companions: String,
    val imageUrl: String,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLikedByUser: Boolean = false,
    val isAiMemoryCardGenerated: Boolean = false,
    val aiInsight: String? = null
)
