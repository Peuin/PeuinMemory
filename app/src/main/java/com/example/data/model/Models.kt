package com.example.data.model

import com.squareup.moshi.JsonClass

enum class PlaceCategory(val displayName: String, val iconName: String) {
    ATTRACTION("Điểm tham quan", "landmark"),
    FOOD("Ăn uống", "restaurant"),
    CAFE("Cà phê", "coffee"),
    HOTEL("Khách sạn", "hotel"),
    SHOPPING("Mua sắm", "shopping_bag"),
    ENTERTAINMENT("Giải trí", "sports_esports"),
    LOCAL_EXPERIENCE("Trải nghiệm địa phương", "explore"),
    TRANSPORT("Trạm phương tiện", "directions_bus"),
    EMERGENCY("Y tế & Khẩn cấp", "local_hospital")
}

@JsonClass(generateAdapter = true)
data class Place(
    val id: String,
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
    val amenities: List<String> = emptyList(),
    val tips: List<String> = emptyList(),
    val imageUrl: String,
    val lat: Double,
    val lng: Double,
    val isSaved: Boolean = false,
    val destination: String = "Đà Lạt"
)

@JsonClass(generateAdapter = true)
data class ItineraryItem(
    val id: String,
    val placeId: String,
    val title: String,
    val category: PlaceCategory,
    val startTime: String, // e.g. "08:30"
    val durationMinutes: Int, // e.g. 90
    val travelTimeToNextMin: Int = 15,
    val suggestedTransport: String = "Xe máy",
    val estimatedCost: Long = 150000, // VND
    val openingHours: String = "07:00 - 22:00",
    val note: String = "",
    val isLocked: Boolean = false,
    val weatherWarning: String? = null, // e.g. "Dự báo mưa rào lúc 14:00 - Cần ô hoặc chuyển điểm trong nhà"
    val alternativeSuggestions: List<String> = emptyList(),
    val lat: Double = 11.9404,
    val lng: Double = 108.4583,
    val imageUrl: String = ""
)

@JsonClass(generateAdapter = true)
data class ItineraryDay(
    val dayNumber: Int,
    val title: String,
    val dateString: String,
    val items: List<ItineraryItem>,
    val dailyBudgetLimit: Long = 2000000
) {
    val totalCost: Long get() = items.sumOf { it.estimatedCost }
}

@JsonClass(generateAdapter = true)
data class Trip(
    val id: String,
    val title: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val durationDays: Int,
    val travelersCount: Int,
    val travelStyle: String, // "Cặp đôi", "Gia đình", "Một mình", "Nhóm bạn"
    val totalBudget: Long, // 6,000,000 VND
    val pace: String, // "Thư thả", "Cân bằng", "Dày đặc"
    val transportation: String, // "Xe máy & Đi bộ"
    val coverImage: String,
    val days: List<ItineraryDay>,
    val isCurrentActive: Boolean = true
) {
    val totalEstimatedCost: Long get() = days.sumOf { it.totalCost }
    val remainingBudget: Long get() = totalBudget - totalEstimatedCost
}

@JsonClass(generateAdapter = true)
data class Memory(
    val id: String,
    val tripId: String? = null,
    val placeName: String,
    val location: String,
    val date: String,
    val author: String,
    val authorAvatar: String,
    val note: String,
    val mood: String, // "Chill 🌿", "Hạnh phúc 💖", "Phiêu lưu 🚀"
    val companions: String, // "Cùng người yêu"
    val imageUrl: String,
    val likesCount: Int = 12,
    val commentsCount: Int = 3,
    val isLikedByUser: Boolean = false,
    val isAiMemoryCardGenerated: Boolean = false,
    val aiInsight: String? = null
)

@JsonClass(generateAdapter = true)
data class UserProfile(
    val id: String = "user_01",
    val name: String = "Nguyễn Minh Châu",
    val email: String = "minhchau.travel@example.com",
    val avatarUrl: String = "",
    val departureCity: String = "TP. Hồ Chí Minh",
    val language: String = "Tiếng Việt",
    val interests: List<String> = listOf("Cà phê chill", "Thiên nhiên", "Ẩm thực địa phương", "Chụp ảnh"),
    val foodPreferences: List<String> = listOf("Đặc sản vùng miền", "Bánh tráng nướng", "Lẩu gà lá é"),
    val travelStyle: String = "Thư thả & Cặp đôi",
    val expectedBudgetTier: String = "Tiết kiệm - Trung bình (2 - 3tr/ngày)",
    val totalTrips: Int = 6,
    val placesSaved: Int = 24,
    val totalKmExplored: Int = 1420
)

data class ChatMessage(
    val id: String,
    val sender: String, // "user" or "peuin"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestions: List<String> = emptyList(),
    val proposedAction: ProposedTripAction? = null
)

data class ProposedTripAction(
    val actionType: String, // "REPLACE_ACTIVITY", "WEATHER_OPTIMIZE", "ADD_PLACE", "RECALCULATE_ROUTE"
    val title: String,
    val description: String,
    val dayNumber: Int = 1,
    val oldActivityTitle: String = "",
    val newActivity: ItineraryItem? = null
)

data class DestinationHighlight(
    val id: String,
    val name: String,
    val province: String,
    val tagLine: String,
    val imageUrl: String,
    val rating: Double,
    val tripCount: Int,
    val weather: String
)
