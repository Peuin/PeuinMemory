package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.PlaceEntity
import com.example.data.model.DestinationHighlight
import com.example.data.model.ItineraryItem
import com.example.data.model.PlaceCategory
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.HighDensityAccentPill
import com.example.ui.theme.HighDensityContainer
import com.example.ui.theme.HighDensityDark
import com.example.ui.theme.HighDensityLight
import com.example.ui.theme.HighDensityOnContainer
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.SearchBarBg
import com.example.ui.theme.SearchBarBorder
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary
import java.text.NumberFormat
import java.util.Locale

fun formatVnd(amount: Long): String {
    val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"))
    return "${formatter.format(amount)} đ"
}

fun getCategoryIcon(category: PlaceCategory): ImageVector {
    return when (category) {
        PlaceCategory.ATTRACTION -> Icons.Default.Explore
        PlaceCategory.FOOD -> Icons.Default.Restaurant
        PlaceCategory.CAFE -> Icons.Default.LocalCafe
        PlaceCategory.HOTEL -> Icons.Default.Hotel
        PlaceCategory.SHOPPING -> Icons.Default.ShoppingBag
        PlaceCategory.ENTERTAINMENT -> Icons.Default.SportsEsports
        PlaceCategory.LOCAL_EXPERIENCE -> Icons.Default.AutoAwesome
        PlaceCategory.TRANSPORT -> Icons.Default.DirectionsBus
        PlaceCategory.EMERGENCY -> Icons.Default.LocalHospital
    }
}

@Composable
fun PeuinTopBar(
    selectedCity: String,
    onCityClick: () -> Unit,
    onNotificationClick: () -> Unit,
    userName: String = "Minh Châu",
    avatarUrl: String = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?q=80&w=120&auto=format&fit=crop"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.clickable { onCityClick() }
        ) {
            Text(
                text = "CHÀO BUỔI SÁNG",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate700,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${userName} ơi, đi đâu nào?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = HighDensityDark
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(42.dp)
                    .background(HighDensityAccentPill, CircleShape)
                    .testTag("notification_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = HighDensityDark,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .shadow(1.dp, CircleShape)
                    .clickable { onCityClick() }
            ) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun StitchSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Bạn muốn đi đâu?",
    onVoiceClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = SearchBarBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, SearchBarBorder),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(48.dp)
            .testTag("search_input_container")
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Slate700,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        color = Slate500,
                        fontSize = 13.sp
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_input")
            )
            IconButton(
                onClick = onVoiceClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice search",
                    tint = HighDensityPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryChipsRow(
    selectedCategory: PlaceCategory?,
    onCategorySelected: (PlaceCategory?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        item {
            val isAllSelected = selectedCategory == null
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isAllSelected) HighDensityPrimary else HighDensityContainer,
                modifier = Modifier
                    .clickable { onCategorySelected(null) }
                    .testTag("category_all")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = "Gần bạn",
                        tint = if (isAllSelected) Color.White else HighDensityDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Gần bạn",
                        color = if (isAllSelected) Color.White else HighDensityDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        items(PlaceCategory.values()) { category ->
            val isSelected = selectedCategory == category
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) HighDensityPrimary else HighDensityContainer,
                modifier = Modifier
                    .clickable { onCategorySelected(category) }
                    .testTag("category_${category.name.lowercase()}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getCategoryIcon(category),
                        contentDescription = category.displayName,
                        tint = if (isSelected) Color.White else HighDensityDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = category.displayName,
                        color = if (isSelected) Color.White else HighDensityDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CompactPlaceCard(
    place: PlaceEntity,
    onClick: () -> Unit,
    onSaveToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("compact_place_card_${place.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
            ) {
                AsyncImage(
                    model = place.imageUrl,
                    contentDescription = place.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                // Favorite Heart Button
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .size(28.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        .align(Alignment.TopEnd)
                        .clickable { onSaveToggle() }
                        .testTag("bookmark_button_${place.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (place.isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (place.isSaved) Color(0xFFEF4444) else Slate400,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Category pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = HighDensityDark.copy(alpha = 0.75f),
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = "${place.distanceKm} km",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = place.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = HighDensityDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = HighDensityPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${place.rating}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = HighDensityDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${place.reviewCount})",
                        fontSize = 10.sp,
                        color = Slate500
                    )
                }
            }
        }
    }
}

@Composable
fun PlaceCard(
    place: PlaceEntity,
    onClick: () -> Unit,
    onSaveToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("place_card_${place.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = place.imageUrl,
                    contentDescription = place.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = HighDensityDark.copy(alpha = 0.8f),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(place.category),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = place.category.displayName,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(
                    onClick = onSaveToggle,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(34.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        .align(Alignment.TopEnd)
                        .testTag("bookmark_button_${place.id}")
                ) {
                    Icon(
                        imageVector = if (place.isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Save place",
                        tint = if (place.isSaved) Color(0xFFEF4444) else Slate700,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = HighDensityPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${place.rating}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = HighDensityDark
                        )
                        Text(
                            text = " (${place.reviewCount})",
                            fontSize = 10.sp,
                            color = Slate500
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${place.distanceKm} km",
                            fontSize = 10.sp,
                            color = HighDensityPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = HighDensityDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = place.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate700,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = HighDensityContainer
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Match",
                            tint = HighDensityDark,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = place.whyMatches,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = HighDensityDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HighDensityAiPlannerBanner(
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() }
            .testTag("ai_trip_planner_banner"),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(HighDensityPrimary, HighDensityLight)
                    )
                )
                .padding(20.dp)
        ) {
            // Watermark icon
            Icon(
                imageVector = Icons.Default.FlightTakeoff,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.12f),
                modifier = Modifier
                    .size(130.dp)
                    .align(Alignment.BottomEnd)
                    .rotate(12f)
            )

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PEUIN AI PLANNER",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lên kế hoạch Đà Lạt\n3 ngày 2 đêm?",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tối ưu lộ trình và ngân sách chỉ trong vài giây.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = HighDensityPrimary
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Bắt đầu ngay",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DestinationHeroCard(
    destination: DestinationHighlight,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        modifier = Modifier
            .width(220.dp)
            .height(145.dp)
            .clickable { onClick() }
            .testTag("destination_card_${destination.id}"),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = destination.imageUrl,
                contentDescription = destination.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, HighDensityDark.copy(alpha = 0.85f)),
                            startY = 40f
                        )
                    )
            )

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = HighDensityDark.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    text = destination.weather,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }

            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = destination.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = destination.tagLine,
                    color = Slate100,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun WeatherWarningBanner(
    warningText: String,
    onApplyAlternative: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CoralSecondary.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, CoralSecondary.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("weather_warning_banner")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Weather Alert",
                    tint = CoralSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cảnh Báo Thời Tiết & Tối Ưu Tự Động",
                        fontWeight = FontWeight.Bold,
                        color = CoralSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = warningText,
                        color = Slate700,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onApplyAlternative,
                colors = ButtonDefaults.buttonColors(containerColor = CoralSecondary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier
                    .align(Alignment.End)
                    .testTag("apply_weather_alternative_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Áp dụng phương án trong nhà",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AiBudgetProgressBar(
    totalBudget: Long,
    spentBudget: Long,
    durationDays: Int
) {
    val progress = (spentBudget.toFloat() / totalBudget.toFloat()).coerceIn(0f, 1f)
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate50),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("budget_progress_card")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Budget AI",
                        tint = HighDensityPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Quản lý ngân sách thông minh",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = HighDensityDark
                    )
                }
                Text(
                    text = "Còn lại: ${formatVnd(totalBudget - spentBudget)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = SuccessGreen
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (progress > 0.85f) CoralSecondary else HighDensityPrimary,
                trackColor = Slate200,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Dự tính chi: ${formatVnd(spentBudget)} / ${formatVnd(totalBudget)}",
                    fontSize = 10.sp,
                    color = Slate700
                )
                Text(
                    text = "TB ${(totalBudget / durationDays) / 1000}k/ngày",
                    fontSize = 10.sp,
                    color = Slate700,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
