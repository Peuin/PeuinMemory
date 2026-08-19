package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.PlaceEntity
import com.example.ui.components.CategoryChipsRow
import com.example.ui.components.CompactPlaceCard
import com.example.ui.components.DestinationHeroCard
import com.example.ui.components.HighDensityAiPlannerBanner
import com.example.ui.components.PeuinTopBar
import com.example.ui.components.PlaceCard
import com.example.ui.components.StitchSearchBar
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.HighDensityContainer
import com.example.ui.theme.HighDensityDark
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.PeuinViewModel

@Composable
fun ExploreScreen(
    viewModel: PeuinViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredPlaces by viewModel.filteredPlaces.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val activeTrip by viewModel.activeTrip.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("explore_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // 1. High-Density Header
        item {
            PeuinTopBar(
                selectedCity = "Đà Lạt, Lâm Đồng",
                onCityClick = { viewModel.isOnboardingPreferencesOpen.value = true },
                onNotificationClick = { viewModel.isAskPeuinOpen.value = true },
                userName = userProfile.name.split(" ").lastOrNull() ?: "Minh",
                avatarUrl = userProfile.avatarUrl.ifEmpty { "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?q=80&w=120&auto=format&fit=crop" }
            )
        }

        // 2. High-Density Search Field
        item {
            StitchSearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.searchQuery.value = it },
                onVoiceClick = { viewModel.isAskPeuinOpen.value = true }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 3. Category Chips Row
        item {
            CategoryChipsRow(
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.selectedCategory.value = it }
            )
        }

        // 4. Peuin AI Planner Hero Card
        item {
            HighDensityAiPlannerBanner(
                onClick = { viewModel.isTripPlannerOpen.value = true }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 5. High-Density 2-Column Grid ("Gợi ý cho bạn" / "Xem thêm")
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gợi ý cho bạn",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HighDensityDark
                    )
                    Text(
                        text = "Xem thêm",
                        color = HighDensityPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { viewModel.selectedCategory.value = null }
                            .testTag("explore_see_more_btn")
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                val topPlaces = filteredPlaces.take(4)
                val pairs = topPlaces.chunked(2)
                pairs.forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { place ->
                            CompactPlaceCard(
                                place = place,
                                onClick = { viewModel.selectedPlaceForDetail.value = place },
                                onSaveToggle = { viewModel.toggleSavePlace(place) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // 6. Active Trip Snapshot
        if (activeTrip != null) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Chuyến đi sắp tới",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = HighDensityDark
                        )
                        Text(
                            text = "Chi tiết",
                            color = HighDensityPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { viewModel.setTab(MainTab.ITINERARY) }
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setTab(MainTab.ITINERARY) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = activeTrip?.coverImage,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = HighDensityContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${activeTrip?.durationDays}N${(activeTrip?.durationDays ?: 1) - 1}Đ • ${activeTrip?.travelStyle}",
                                        color = HighDensityDark,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = activeTrip?.title ?: "",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = HighDensityDark,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Ngày 2: Cảnh báo mưa chiều • Đã tối ưu",
                                    fontSize = 11.sp,
                                    color = CoralSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // 7. Top Destinations Section
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Điểm đến nổi bật",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HighDensityDark
                    )
                    Text(
                        text = "Xem tất cả",
                        color = HighDensityPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(viewModel.destinations) { destination ->
                        DestinationHeroCard(
                            destination = destination,
                            onClick = {
                                viewModel.selectedDestination.value = destination.name
                                viewModel.setTab(MainTab.MAP)
                            }
                        )
                    }
                }
            }
        }

        // 8. Full Detailed List
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Khám phá địa điểm yêu thích",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HighDensityDark
                    )
                    Text(
                        text = "Tối ưu hoá theo gu khám phá của bạn",
                        fontSize = 11.sp,
                        color = Slate500
                    )
                }
            }
        }

        items(filteredPlaces.drop(4)) { place ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                PlaceCard(
                    place = place,
                    onClick = { viewModel.selectedPlaceForDetail.value = place },
                    onSaveToggle = { viewModel.toggleSavePlace(place) }
                )
            }
        }
    }
}
