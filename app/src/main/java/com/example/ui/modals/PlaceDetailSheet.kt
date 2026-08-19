package com.example.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.PeuinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailSheet(
    place: PlaceEntity,
    viewModel: PeuinViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedDayForAdd by remember { mutableIntStateOf(1) }
    var showDayPicker by remember { mutableStateOf(false) }
    var isAddedToItinerary by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxHeight(0.9f)
            .testTag("place_detail_sheet")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // 1. Cover Image with Close & Bookmark
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    AsyncImage(
                        model = place.imageUrl,
                        contentDescription = place.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                            .align(Alignment.TopStart)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate700)
                    }

                    IconButton(
                        onClick = { viewModel.toggleSavePlace(place) },
                        modifier = Modifier
                            .padding(16.dp)
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = if (place.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (place.isSaved) CoralSecondary else Slate700
                        )
                    }
                }
            }

            // 2. Info Header
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TealContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(getCategoryIcon(place.category), contentDescription = null, tint = TealDark, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = place.category.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TealDark
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${place.rating}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Slate900
                            )
                            Text(
                                text = " (${place.reviewCount} đánh giá)",
                                fontSize = 12.sp,
                                color = Slate500
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Slate500, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = place.address,
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Specs row (Hours, Duration, Price)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate100, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Mở cửa", fontSize = 11.sp, color = Slate500)
                            }
                            Text(place.openingHours, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WbSunny, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Thời lượng", fontSize = 11.sp, color = Slate500)
                            }
                            Text("${place.recommendedDurationMinutes} phút", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Payments, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Mức giá", fontSize = 11.sp, color = Slate500)
                            }
                            Text(place.priceRange, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // AI Match card
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = TealContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TealDark, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Đánh giá bởi Peuin AI",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TealDark
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = place.whyMatches,
                                fontSize = 12.sp,
                                color = TealDark,
                                lineHeight = 17.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Giới thiệu điểm đến",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = place.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate700,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Actions row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.sendChatMessage("Cho tôi thêm kinh nghiệm ăn uống và chụp ảnh đẹp tại ${place.name} nhé!")
                                onDismiss()
                                viewModel.isAskPeuinOpen.value = true
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Hỏi Peuin", color = TealPrimary, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.addPlaceToItinerary(place, selectedDayForAdd)
                                isAddedToItinerary = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAddedToItinerary) SuccessGreen else TealPrimary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f).testTag("add_place_to_itinerary_btn")
                        ) {
                            Icon(
                                imageVector = if (isAddedToItinerary) Icons.Default.Bookmark else Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAddedToItinerary) "Đã thêm vào lịch" else "Thêm vào lịch",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
