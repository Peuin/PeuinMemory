package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.MemoryEntity
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.PeuinViewModel

@Composable
fun MemoriesScreen(
    viewModel: PeuinViewModel,
    modifier: Modifier = Modifier
) {
    val memories by viewModel.allMemories.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("memories_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Nhật ký Kỷ niệm",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "Lưu giữ khoảnh khắc & chia sẻ cùng cộng đồng",
                        fontSize = 12.sp,
                        color = Slate500
                    )
                }

                Button(
                    onClick = { viewModel.isCreateMemoryOpen.value = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("create_memory_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ghi lại", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Memory Feed
        items(memories) { memory ->
            MemoryCard(
                memory = memory,
                onLikeToggle = { viewModel.toggleLikeMemory(memory.id) },
                onShare = { /* Share intent */ }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun MemoryCard(
    memory: MemoryEntity,
    onLikeToggle: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("memory_card_${memory.id}")
    ) {
        Column(modifier = Modifier.padding(vertical = 14.dp)) {
            // Author & Place Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    AsyncImage(
                        model = memory.authorAvatar.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80" },
                        contentDescription = memory.author,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = memory.author,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Slate900,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${memory.placeName} • ${memory.date}",
                                fontSize = 12.sp,
                                color = Slate500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Mood pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = TealContainer
                ) {
                    Text(
                        text = memory.mood,
                        fontSize = 11.sp,
                        color = TealDark,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Photo
            if (memory.imageUrl.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .aspectRatio(1.42f)
                        .clip(RoundedCornerShape(18.dp))
                ) {
                    AsyncImage(
                        model = memory.imageUrl,
                        contentDescription = memory.placeName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (memory.isAiMemoryCardGenerated) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color.White.copy(alpha = 0.92f),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AmberAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "AI Memory",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate700
                                )
                            }
                        }
                    }
                }
            }

            // Note text
            Text(
                text = memory.note,
                style = MaterialTheme.typography.bodyMedium,
                color = Slate900,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // AI Memory Card Insight Badge
            if (!memory.aiInsight.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = TealContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = memory.aiInsight,
                            fontSize = 12.sp,
                            color = Slate700,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactions Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (memory.isLikedByUser) CoralSecondary.copy(alpha = 0.12f) else Slate100,
                        modifier = Modifier.clickable { onLikeToggle() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Icon(
                                imageVector = if (memory.isLikedByUser) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (memory.isLikedByUser) CoralSecondary else Slate400,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "${memory.likesCount}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (memory.isLikedByUser) CoralSecondary else Slate500
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Slate100)
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${memory.commentsCount}",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }
                }

                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Slate500, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
