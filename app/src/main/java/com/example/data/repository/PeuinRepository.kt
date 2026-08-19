package com.example.data.repository

import android.content.Context
import com.example.data.datasource.SampleData
import com.example.data.local.MemoryEntity
import com.example.data.local.PeuinDatabase
import com.example.data.local.PlaceEntity
import com.example.data.local.TripEntity
import com.example.data.model.ChatMessage
import com.example.data.model.DestinationHighlight
import com.example.data.model.ItineraryDay
import com.example.data.model.ItineraryItem
import com.example.data.model.PlaceCategory
import com.example.data.model.ProposedTripAction
import com.example.data.model.Trip
import com.example.data.model.UserProfile
import com.example.data.remote.GeminiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PeuinRepository(private val context: Context) {
    private val db = PeuinDatabase.getDatabase(context)
    private val geminiService = GeminiService()
    private val repoScope = CoroutineScope(Dispatchers.IO)

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile = _userProfile.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "welcome_msg",
                sender = "peuin",
                text = "Xin chào Minh Châu! Mình là Peuin – Bạn đồng hành AI của bạn. Mình đã sẵn sàng hỗ trợ bạn khám phá Đà Lạt: gợi ý điểm đến độc đáo, tối ưu hoá lịch trình, giải thích văn hoá ẩm thực và xử lý thay đổi thời tiết linh hoạt.",
                suggestions = listOf(
                    "Đổi lịch chiều nay vì trời mưa 🌧️",
                    "Gần đây có quán ăn nào ngon?",
                    "Tôi còn bao nhiêu ngân sách?",
                    "Tối ưu tuyến đường đi xe máy"
                )
            )
        )
    )
    val chatMessages = _chatMessages.asStateFlow()

    init {
        repoScope.launch {
            initSampleDataIfNeeded()
        }
    }

    private suspend fun initSampleDataIfNeeded() {
        withContext(Dispatchers.IO) {
            db.tripDao().insertTrip(SampleData.sampleTrip)
            db.placeDao().insertPlaces(SampleData.samplePlaces)
            db.memoryDao().insertMemories(SampleData.sampleMemories)
        }
    }

    val destinations: List<DestinationHighlight> = SampleData.destinations

    val activeTrip: Flow<TripEntity?> = db.tripDao().getActiveTrip()

    val allPlaces: Flow<List<PlaceEntity>> = db.placeDao().getAllPlaces()

    val savedPlaces: Flow<List<PlaceEntity>> = db.placeDao().getSavedPlaces()

    val allMemories: Flow<List<MemoryEntity>> = db.memoryDao().getAllMemories()

    suspend fun toggleSavePlace(placeId: String, currentSaved: Boolean) {
        db.placeDao().updateSavedStatus(placeId, !currentSaved)
    }

    suspend fun toggleLikeMemory(memoryId: String) {
        db.memoryDao().toggleLike(memoryId)
    }

    suspend fun addMemory(
        placeName: String,
        note: String,
        mood: String,
        companions: String,
        imageUrl: String
    ) {
        val newMemory = MemoryEntity(
            id = "mem_${UUID.randomUUID()}",
            placeName = placeName,
            location = "Đà Lạt, Lâm Đồng",
            date = "Vừa xong",
            author = _userProfile.value.name,
            authorAvatar = _userProfile.value.avatarUrl,
            note = note,
            mood = mood,
            companions = companions,
            imageUrl = if (imageUrl.isBlank()) "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=800&auto=format&fit=crop&q=80" else imageUrl,
            likesCount = 1,
            commentsCount = 0,
            isLikedByUser = true,
            isAiMemoryCardGenerated = true,
            aiInsight = "💡 Peuin AI: Khoảnh khắc mới được ghi lại tự động đồng bộ vào nhật ký du lịch!"
        )
        db.memoryDao().insertMemory(newMemory)
    }

    suspend fun updateTripDays(tripId: String, updatedDays: List<ItineraryDay>) {
        val current = db.tripDao().getTripById(tripId) ?: return
        val updated = current.copy(days = updatedDays)
        db.tripDao().updateTrip(updated)
    }

    suspend fun applyProposedAction(action: ProposedTripAction, tripId: String) {
        val current = db.tripDao().getTripById(tripId) ?: return
        val days = current.days.map { day ->
            if (day.dayNumber == action.dayNumber && action.newActivity != null) {
                val newItems = day.items.map { item ->
                    if (item.category == PlaceCategory.LOCAL_EXPERIENCE || item.weatherWarning != null || item.id == "it_06") {
                        action.newActivity
                    } else {
                        item
                    }
                }
                day.copy(items = newItems)
            } else {
                day
            }
        }
        db.tripDao().updateTrip(current.copy(days = days))
    }

    suspend fun addPlaceToItinerary(place: PlaceEntity, dayNumber: Int) {
        val currentTrip = db.tripDao().getTripById("trip_dalat_01") ?: return
        val newItem = ItineraryItem(
            id = "it_${UUID.randomUUID()}",
            placeId = place.id,
            title = place.name,
            category = place.category,
            startTime = "16:00",
            durationMinutes = place.recommendedDurationMinutes,
            suggestedTransport = "Xe máy (${place.distanceKm} km)",
            estimatedCost = 100000L,
            openingHours = place.openingHours,
            note = place.summary,
            lat = place.lat,
            lng = place.lng,
            imageUrl = place.imageUrl
        )

        val updatedDays = currentTrip.days.map { day ->
            if (day.dayNumber == dayNumber) {
                day.copy(items = day.items + newItem)
            } else {
                day
            }
        }
        db.tripDao().updateTrip(currentTrip.copy(days = updatedDays))
    }

    suspend fun removeItineraryItem(dayNumber: Int, itemId: String) {
        val currentTrip = db.tripDao().getTripById("trip_dalat_01") ?: return
        val updatedDays = currentTrip.days.map { day ->
            if (day.dayNumber == dayNumber) {
                day.copy(items = day.items.filterNot { it.id == itemId })
            } else {
                day
            }
        }
        db.tripDao().updateTrip(currentTrip.copy(days = updatedDays))
    }

    suspend fun toggleLockItem(dayNumber: Int, itemId: String) {
        val currentTrip = db.tripDao().getTripById("trip_dalat_01") ?: return
        val updatedDays = currentTrip.days.map { day ->
            if (day.dayNumber == dayNumber) {
                day.copy(items = day.items.map {
                    if (it.id == itemId) it.copy(isLocked = !it.isLocked) else it
                })
            } else {
                day
            }
        }
        db.tripDao().updateTrip(currentTrip.copy(days = updatedDays))
    }

    suspend fun generateAiTrip(
        destination: String,
        durationDays: Int,
        budgetVnd: Long,
        travelers: Int,
        interests: List<String>,
        promptText: String
    ) {
        val newTrip = TripEntity(
            id = "trip_${UUID.randomUUID()}",
            title = "Hành trình $destination ${durationDays}N${durationDays - 1}Đ",
            destination = destination,
            startDate = "25/11/2026",
            endDate = "27/11/2026",
            durationDays = durationDays,
            travelersCount = travelers,
            travelStyle = "Tuỳ chỉnh AI • $destination",
            totalBudget = budgetVnd,
            pace = "Thư thả & Linh hoạt",
            transportation = "Xe máy / Taxi công nghệ",
            coverImage = SampleData.destinations.firstOrNull { it.name.contains(destination, ignoreCase = true) }?.imageUrl
                ?: "https://images.unsplash.com/photo-1596401057633-54a8fe8ef647?w=1000&auto=format&fit=crop&q=80",
            isCurrentActive = true,
            days = (1..durationDays).map { dayNum ->
                ItineraryDay(
                    dayNumber = dayNum,
                    title = "Ngày $dayNum: Khám phá $destination cùng Peuin",
                    dateString = "${24 + dayNum}/11/2026",
                    dailyBudgetLimit = budgetVnd / durationDays,
                    items = listOf(
                        ItineraryItem(
                            id = "ai_it_${dayNum}_1",
                            placeId = "pl_05",
                            title = "Thưởng thức đặc sản sáng $destination",
                            category = PlaceCategory.FOOD,
                            startTime = "08:00",
                            durationMinutes = 60,
                            estimatedCost = 60000L,
                            note = "Gợi ý AI dựa trên gu ẩm thực của bạn.",
                            imageUrl = "https://images.unsplash.com/photo-1509722747041-616f39b57569?w=400&auto=format&fit=crop&q=80"
                        ),
                        ItineraryItem(
                            id = "ai_it_${dayNum}_2",
                            placeId = "pl_01",
                            title = "Khám phá điểm check-in tiêu biểu",
                            category = PlaceCategory.ATTRACTION,
                            startTime = "09:30",
                            durationMinutes = 120,
                            estimatedCost = 150000L,
                            note = "Thời điểm ánh sáng đẹp nhất trong ngày.",
                            imageUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400&auto=format&fit=crop&q=80"
                        ),
                        ItineraryItem(
                            id = "ai_it_${dayNum}_3",
                            placeId = "pl_01",
                            title = "Cà phê thư giãn & ngắm cảnh",
                            category = PlaceCategory.CAFE,
                            startTime = "15:30",
                            durationMinutes = 90,
                            estimatedCost = 120000L,
                            note = "Không gian yên tĩnh phù hợp lịch trình.",
                            imageUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400&auto=format&fit=crop&q=80"
                        )
                    )
                )
            }
        )
        db.tripDao().insertTrip(newTrip)
    }

    suspend fun sendMessageToPeuin(userMessage: String) {
        val userMsg = ChatMessage(
            id = "msg_${UUID.randomUUID()}",
            sender = "user",
            text = userMessage
        )
        _chatMessages.value = _chatMessages.value + userMsg

        val currentTrip = db.tripDao().getTripById("trip_dalat_01")
        val contextInfo = "Điểm đến: ${currentTrip?.destination ?: "Đà Lạt"}, Ngân sách: ${currentTrip?.totalBudget ?: 6000000} VND, Số ngày: ${currentTrip?.durationDays ?: 3}N, Thời tiết: Có dự báo mưa chiều ngày 2 lúc 14h30."

        val aiResponse = geminiService.askPeuin(userMessage, contextInfo)
        _chatMessages.value = _chatMessages.value + aiResponse
    }

    fun updateUserProfile(profile: UserProfile) {
        _userProfile.value = profile
    }
}
