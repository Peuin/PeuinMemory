package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MemoryEntity
import com.example.data.local.PlaceEntity
import com.example.data.local.TripEntity
import com.example.data.model.ChatMessage
import com.example.data.model.DestinationHighlight
import com.example.data.model.PlaceCategory
import com.example.data.model.ProposedTripAction
import com.example.data.model.UserProfile
import com.example.data.repository.PeuinRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val iconRes: String) {
    EXPLORE("Khám phá", "explore"),
    MAP("Bản đồ", "map"),
    ITINERARY("Hành trình", "calendar_today"),
    MEMORIES("Kỷ niệm", "photo_camera"),
    PROFILE("Cá nhân", "person")
}

class PeuinViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PeuinRepository(application.applicationContext)

    val currentTab = MutableStateFlow(MainTab.EXPLORE)

    val destinations: List<DestinationHighlight> = repository.destinations
    val activeTrip: StateFlow<TripEntity?> = repository.activeTrip
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allPlaces: StateFlow<List<PlaceEntity>> = repository.allPlaces
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedPlaces: StateFlow<List<PlaceEntity>> = repository.savedPlaces
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMemories: StateFlow<List<MemoryEntity>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages

    // Filter states
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<PlaceCategory?>(null)
    val selectedDestination = MutableStateFlow("Đà Lạt")
    val selectedDayIndex = MutableStateFlow(0)

    // UI Interactive modals
    val selectedPlaceForDetail = MutableStateFlow<PlaceEntity?>(null)
    val isAskPeuinOpen = MutableStateFlow(false)
    val isTripPlannerOpen = MutableStateFlow(false)
    val isCreateMemoryOpen = MutableStateFlow(false)
    val isOnboardingPreferencesOpen = MutableStateFlow(false)
    val isAiGenerating = MutableStateFlow(false)

    // Filtered places for Explore & Map
    val filteredPlaces: StateFlow<List<PlaceEntity>> = combine(
        allPlaces,
        searchQuery,
        selectedCategory
    ) { places, query, category ->
        places.filter { place ->
            val matchesQuery = query.isBlank() ||
                    place.name.contains(query, ignoreCase = true) ||
                    place.summary.contains(query, ignoreCase = true) ||
                    place.address.contains(query, ignoreCase = true)
            val matchesCategory = category == null || place.category == category
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTab(tab: MainTab) {
        currentTab.value = tab
    }

    fun toggleSavePlace(place: PlaceEntity) {
        viewModelScope.launch {
            repository.toggleSavePlace(place.id, place.isSaved)
        }
    }

    fun toggleLikeMemory(memoryId: String) {
        viewModelScope.launch {
            repository.toggleLikeMemory(memoryId)
        }
    }

    fun addPlaceToItinerary(place: PlaceEntity, dayNumber: Int) {
        viewModelScope.launch {
            repository.addPlaceToItinerary(place, dayNumber)
        }
    }

    fun removeItineraryItem(dayNumber: Int, itemId: String) {
        viewModelScope.launch {
            repository.removeItineraryItem(dayNumber, itemId)
        }
    }

    fun toggleLockItem(dayNumber: Int, itemId: String) {
        viewModelScope.launch {
            repository.toggleLockItem(dayNumber, itemId)
        }
    }

    fun applyProposedAiAction(action: ProposedTripAction) {
        viewModelScope.launch {
            val tripId = activeTrip.value?.id ?: "trip_dalat_01"
            repository.applyProposedAction(action, tripId)
        }
    }

    fun sendChatMessage(message: String) {
        if (message.isBlank()) return
        viewModelScope.launch {
            isAiGenerating.value = true
            try {
                repository.sendMessageToPeuin(message)
            } finally {
                isAiGenerating.value = false
            }
        }
    }

    fun generateAiTrip(
        destination: String,
        durationDays: Int,
        budgetVnd: Long,
        travelers: Int,
        interests: List<String>,
        promptText: String
    ) {
        viewModelScope.launch {
            isAiGenerating.value = true
            try {
                repository.generateAiTrip(
                    destination,
                    durationDays,
                    budgetVnd,
                    travelers,
                    interests,
                    promptText
                )
                currentTab.value = MainTab.ITINERARY
                isTripPlannerOpen.value = false
            } finally {
                isAiGenerating.value = false
            }
        }
    }

    fun createMemory(
        placeName: String,
        note: String,
        mood: String,
        companions: String,
        imageUrl: String
    ) {
        viewModelScope.launch {
            repository.addMemory(placeName, note, mood, companions, imageUrl)
            isCreateMemoryOpen.value = false
        }
    }

    fun updatePreferences(profile: UserProfile) {
        repository.updateUserProfile(profile)
        isOnboardingPreferencesOpen.value = false
    }
}
