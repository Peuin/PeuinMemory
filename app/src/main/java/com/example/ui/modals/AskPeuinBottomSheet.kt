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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.PlaceCategory
import com.example.data.model.ProposedTripAction
import com.example.ui.theme.HighDensityPrimary
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
import com.example.ui.viewmodel.PeuinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskPeuinBottomSheet(
    viewModel: PeuinViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isAiGenerating by viewModel.isAiGenerating.collectAsState()
    val activeTrip by viewModel.activeTrip.collectAsState()

    var inputMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val totalDays = activeTrip?.durationDays ?: 3

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxHeight(0.92f)
            .testTag("ask_peuin_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(HighDensityPrimary, SkyAccent))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Hỏi Peuin AI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = TealContainer
                            ) {
                                Text(
                                    text = "Trực tuyến",
                                    color = TealDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Trợ lý du lịch thông minh • Tự động thêm vào hành trình",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                }
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Slate100))

            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(chatMessages) { message ->
                    ChatMessageItem(
                        message = message,
                        totalTripDays = totalDays,
                        onAddActionToItinerary = { action, selectedDay ->
                            val modifiedAction = action.copy(dayNumber = selectedDay)
                            viewModel.applyProposedAiAction(modifiedAction)
                        }
                    )
                }

                if (isAiGenerating) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = HighDensityPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Peuin AI đang xử lý và phân tích lịch trình...",
                                fontSize = 12.sp,
                                color = Slate500
                            )
                        }
                    }
                }
            }

            // Quick Suggestions Chips
            val defaultSuggestions = listOf(
                "Gợi ý quán cà phê hoàng hôn chiều nay",
                "Ăn tối lẩu gà lá é ở đâu?",
                "Đổi lịch chiều nay vì trời mưa 🌧️",
                "Kiểm tra ngân sách chuyến đi"
            )
            val currentSuggestions = chatMessages.lastOrNull()?.suggestions?.ifEmpty { defaultSuggestions } ?: defaultSuggestions

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                items(currentSuggestions) { suggestion ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Slate100,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        modifier = Modifier
                            .clickable {
                                viewModel.sendChatMessage(suggestion)
                            }
                            .testTag("quick_suggestion_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = HighDensityPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = suggestion,
                                fontSize = 12.sp,
                                color = Slate700,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    placeholder = {
                        Text(
                            text = "Hỏi Peuin quán ăn, cà phê, thêm vào lịch trình...",
                            color = Slate400,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_chat_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HighDensityPrimary,
                        unfocusedBorderColor = Slate200,
                        focusedContainerColor = Slate100,
                        unfocusedContainerColor = Slate100
                    ),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputMessage.isNotBlank()) {
                            viewModel.sendChatMessage(inputMessage)
                            inputMessage = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(HighDensityPrimary, CircleShape)
                        .testTag("ai_chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    totalTripDays: Int,
    onAddActionToItinerary: (ProposedTripAction, Int) -> Unit
) {
    val isUser = message.sender == "user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (!isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(HighDensityPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Peuin AI Companion",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighDensityPrimary
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) HighDensityPrimary else Slate100,
            border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, Slate200) else null,
            modifier = Modifier.widthIn(max = 330.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    color = if (isUser) Color.White else Slate900,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                // Interactive Actionable Card if present
                if (message.proposedAction != null) {
                    val action = message.proposedAction
                    var isApplied by remember { mutableStateOf(false) }
                    var selectedDay by remember { mutableIntStateOf(action.dayNumber.coerceIn(1, totalTripDays.coerceAtLeast(1))) }

                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val icon = when (action.newActivity?.category) {
                                        PlaceCategory.FOOD -> "🍲"
                                        PlaceCategory.CAFE -> "☕"
                                        PlaceCategory.ATTRACTION -> "🏛️"
                                        else -> "🌿"
                                    }
                                    Text(text = icon, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = action.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Slate900
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = action.description,
                                fontSize = 11.sp,
                                color = Slate700,
                                lineHeight = 16.sp
                            )

                            // Day Selector
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Thêm vào:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Slate500
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                (1..totalTripDays).forEach { dayNum ->
                                    val isDaySelected = selectedDay == dayNum
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isDaySelected) HighDensityPrimary else Slate100,
                                        modifier = Modifier
                                            .padding(end = 4.dp)
                                            .clickable { selectedDay = dayNum }
                                    ) {
                                        Text(
                                            text = "Ngày $dayNum",
                                            fontSize = 10.sp,
                                            fontWeight = if (isDaySelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isDaySelected) Color.White else Slate700,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // One-tap Add to Itinerary Button
                            Button(
                                onClick = {
                                    onAddActionToItinerary(action, selectedDay)
                                    isApplied = true
                                },
                                enabled = !isApplied,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isApplied) SuccessGreen else HighDensityPrimary,
                                    disabledContainerColor = SuccessGreen.copy(alpha = 0.8f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("confirm_ai_itinerary_action")
                            ) {
                                Icon(
                                    imageVector = if (isApplied) Icons.Default.Check else Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isApplied) "✓ Đã thêm vào Ngày $selectedDay của Hành trình!" else "Thêm vào Ngày $selectedDay của Hành trình",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
