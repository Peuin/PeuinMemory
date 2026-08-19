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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
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
fun ProfileScreen(
    viewModel: PeuinViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val savedPlaces by viewModel.savedPlaces.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // User Profile Card Header
        item {
            Card(
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(80.dp)) {
                        AsyncImage(
                            model = userProfile.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80" },
                            contentDescription = userProfile.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                        Surface(
                            shape = CircleShape,
                            color = TealPrimary,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = userProfile.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = userProfile.email,
                        fontSize = 12.sp,
                        color = Slate500
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate100, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStatItem(title = "Chuyến đi", value = "${userProfile.totalTrips}")
                        Box(modifier = Modifier.width(1.dp).height(30.dp).background(Slate200))
                        ProfileStatItem(title = "Đã lưu", value = "${savedPlaces.size}")
                        Box(modifier = Modifier.width(1.dp).height(30.dp).background(Slate200))
                        ProfileStatItem(title = "Khám phá", value = "${userProfile.totalKmExplored} km")
                    }
                }
            }
        }

        // Travel Taste & Personalization
        item {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gu du lịch của bạn",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    IconButton(
                        onClick = { viewModel.isOnboardingPreferencesOpen.value = true },
                        modifier = Modifier.size(32.dp).testTag("edit_preferences_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TealPrimary, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Sở thích khám phá:",
                            fontSize = 11.sp,
                            color = Slate500,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(userProfile.interests) { interest ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TealContainer
                                ) {
                                    Text(
                                        text = interest,
                                        fontSize = 11.sp,
                                        color = TealDark,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Khẩu vị ẩm thực:",
                            fontSize = 11.sp,
                            color = Slate500,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(userProfile.foodPreferences) { food ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Slate100
                                ) {
                                    Text(
                                        text = food,
                                        fontSize = 11.sp,
                                        color = Slate700,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Phong cách & Ngân sách:",
                            fontSize = 11.sp,
                            color = Slate500,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${userProfile.travelStyle} • ${userProfile.expectedBudgetTier}",
                            fontSize = 12.sp,
                            color = Slate900,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Settings Section
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Cài đặt & Ngoại tuyến",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(10.dp))

                SettingsItem(
                    icon = Icons.Default.LocationOn,
                    title = "Định vị Google Maps & Tài khoản",
                    subtitle = "Quản lý GPS vị trí lúc đăng nhập và đồng bộ",
                    onClick = { viewModel.isAuthLocationSheetOpen.value = true }
                )
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = "Ngôn ngữ giao diện",
                    subtitle = "Tiếng Việt (Mặc định)",
                    onClick = {}
                )
                SettingsItem(
                    icon = Icons.Default.CloudDownload,
                    title = "Dữ liệu ngoại tuyến (Offline Trip)",
                    subtitle = "Đà Lạt 3N2Đ • Đã sẵn sàng dùng khi mất sóng",
                    onClick = {}
                )
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Thông báo cảnh báo thời tiết",
                    subtitle = "Bật cảnh báo mưa rào và kẹt xe",
                    onClick = {}
                )
                SettingsItem(
                    icon = Icons.Default.Security,
                    title = "Bảo mật & Quyền riêng tư",
                    subtitle = "Tài khoản và lưu trữ đám mây",
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun ProfileStatItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Slate900
        )
        Text(
            text = title,
            fontSize = 11.sp,
            color = Slate500
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(TealContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = TealDark, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Slate500
                )
            }
        }
    }
}
