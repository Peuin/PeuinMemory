package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ItineraryDay
import com.example.data.model.ItineraryItem
import com.example.data.model.ProposedTripAction
import com.example.ui.components.AiBudgetProgressBar
import com.example.ui.components.WeatherWarningBanner
import com.example.ui.components.formatVnd
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.PeuinViewModel

@Composable
fun ItineraryScreen(
    viewModel: PeuinViewModel,
    modifier: Modifier = Modifier
) {
    val activeTrip by viewModel.activeTrip.collectAsState()
    val selectedDayIndex by viewModel.selectedDayIndex.collectAsState()

    if (activeTrip == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Chưa có lịch trình nào",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Hãy để Peuin AI tạo lịch trình thông minh phù hợp với ngân sách của bạn.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.isTripPlannerOpen.value = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Tạo lịch trình cùng Peuin AI")
                }
            }
        }
        return
    }

    val trip = activeTrip!!
    val currentDay: ItineraryDay? = trip.days.getOrNull(selectedDayIndex) ?: trip.days.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("itinerary_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // 1. Trip Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = trip.coverImage,
                    contentDescription = trip.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Slate900.copy(alpha = 0.3f), Slate900.copy(alpha = 0.9f)),
                                startY = 30f
                            )
                        )
                )

                // Top action bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Slate900.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "Hành trình hiện tại",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { viewModel.isAskPeuinOpen.value = true },
                            modifier = Modifier
                                .size(36.dp)
                                .background(TealPrimary, CircleShape)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Ask Peuin AI", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Title & Trip Specs
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Text(
                        text = trip.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${trip.startDate} - ${trip.endDate} • ${trip.travelersCount} người • ${trip.pace}",
                        color = Slate200,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 2. Budget AI Progress Card
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                AiBudgetProgressBar(
                    totalBudget = trip.totalBudget,
                    spentBudget = trip.days.sumOf { it.totalCost },
                    durationDays = trip.durationDays
                )
            }
        }

        // 3. Day Tabs Row
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                itemsIndexed(trip.days) { index, day ->
                    val isSelected = selectedDayIndex == index
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) TealPrimary else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) TealPrimary else Slate200
                        ),
                        shadowElevation = if (isSelected) 3.dp else 1.dp,
                        modifier = Modifier
                            .clickable { viewModel.selectedDayIndex.value = index }
                            .testTag("day_tab_$index")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Ngày ${day.dayNumber}",
                                color = if (isSelected) Color.White else Slate900,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = day.dateString,
                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else Slate500,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatVnd(day.totalCost),
                                color = if (isSelected) CoralSecondary else Slate700,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 4. Day Summary & Route Optimization Bar
        if (currentDay != null) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentDay.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${currentDay.items.size} điểm dừng • Chi phí: ${formatVnd(currentDay.totalCost)}",
                                fontSize = 12.sp,
                                color = Slate500
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = TealPrimary,
                            modifier = Modifier
                                .clickable {
                                    viewModel.sendChatMessage("Tối ưu lại thứ tự di chuyển ngày ${currentDay.dayNumber} để ít đi bộ nhất")
                                    viewModel.isAskPeuinOpen.value = true
                                }
                                .testTag("recalculate_route_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Tối ưu lộ trình",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Weather Warning for Day 2
            val weatherItem = currentDay.items.firstOrNull { it.weatherWarning != null }
            if (weatherItem != null) {
                item {
                    Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                        WeatherWarningBanner(
                            warningText = weatherItem.weatherWarning ?: "",
                            onApplyAlternative = {
                                viewModel.applyProposedAiAction(
                                    ProposedTripAction(
                                        actionType = "WEATHER_OPTIMIZE",
                                        title = "Tối ưu lịch trình tránh mưa chiều",
                                        description = "Chuyển sang Workshop Trà Dinh III",
                                        dayNumber = currentDay.dayNumber,
                                        oldActivityTitle = weatherItem.title
                                    )
                                )
                            }
                        )
                    }
                }
            }

            // 5. Timeline of Itinerary Items
            itemsIndexed(currentDay.items) { index, item ->
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    // Timeline Item Card
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("itinerary_item_${item.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    if (item.imageUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = item.imageUrl,
                                            contentDescription = item.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                    }
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = TealContainer
                                            ) {
                                                Text(
                                                    text = item.startTime,
                                                    color = TealDark,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${item.durationMinutes} phút",
                                                fontSize = 11.sp,
                                                color = Slate500
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate900
                                        )
                                    }
                                }

                                Row {
                                    IconButton(
                                        onClick = { viewModel.toggleLockItem(currentDay.dayNumber, item.id) },
                                        modifier = Modifier.size(32.dp).testTag("lock_button_${item.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (item.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                            contentDescription = "Lock slot",
                                            tint = if (item.isLocked) AmberAccent else Slate400,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.removeItineraryItem(currentDay.dayNumber, item.id) },
                                        modifier = Modifier.size(32.dp).testTag("delete_item_${item.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Remove",
                                            tint = Slate400,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            if (item.note.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item.note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate700,
                                    lineHeight = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Chi phí: ${formatVnd(item.estimatedCost)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                                Text(
                                    text = "Mở cửa: ${item.openingHours}",
                                    fontSize = 11.sp,
                                    color = Slate500
                                )
                            }
                        }
                    }

                    // Travel Time connector to next stop
                    if (index < currentDay.items.size - 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, top = 2.dp, bottom = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(26.dp)
                                    .background(Slate200)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Slate100
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsBus,
                                        contentDescription = null,
                                        tint = Slate500,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${item.suggestedTransport} • ${item.travelTimeToNextMin} phút",
                                        fontSize = 10.sp,
                                        color = Slate700,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Add Activity & AI Planner Action
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.isAskPeuinOpen.value = true },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).testTag("ask_ai_for_activities")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = TealPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Gợi ý thêm", color = TealPrimary, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.isTripPlannerOpen.value = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        modifier = Modifier.weight(1f).testTag("create_new_trip_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tạo chuyến mới", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
