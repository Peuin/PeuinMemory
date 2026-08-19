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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.ui.components.formatVnd
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.PeuinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiTripPlannerDialog(
    viewModel: PeuinViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isAiGenerating by viewModel.isAiGenerating.collectAsState()

    var destination by remember { mutableStateOf("Đà Lạt") }
    var durationDays by remember { mutableIntStateOf(3) }
    var budgetVnd by remember { mutableLongStateOf(6000000L) }
    var travelersCount by remember { mutableIntStateOf(2) }
    var travelStyle by remember { mutableStateOf("Cặp đôi lãng mạn") }
    var promptNote by remember { mutableStateOf("Ưu tiên các quán cà phê view đẹp, đồi thông, ẩm thực nướng ấm áp và hạn chế di chuyển dốc nhiều.") }

    val destinations = listOf("Đà Lạt", "Đà Nẵng & Hội An", "Phú Quốc", "Sa Pa", "Ninh Bình", "Hà Giang")
    val durations = listOf(2 to "2N1Đ", 3 to "3N2Đ", 4 to "4N3Đ", 5 to "5N4Đ")
    val budgetOptions = listOf(3000000L, 6000000L, 10000000L, 15000000L)
    val styles = listOf("Cặp đôi lãng mạn", "Bạn bè check-in", "Gia đình nghỉ dưỡng", "Solo thư giãn")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxHeight(0.92f)
            .testTag("ai_trip_planner_sheet")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // Header
            item {
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(TealPrimary, SkyAccent))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Lập lịch trình thông minh",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = "Peuin AI tối ưu theo ngân sách & gu của bạn",
                                fontSize = 11.sp,
                                color = Slate500
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Slate100))
            }

            // Form inputs
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    // 1. Destination
                    Text("1. Bạn muốn đi đâu?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(destinations) { dest ->
                            val isSelected = destination == dest
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) TealPrimary else Slate100,
                                modifier = Modifier
                                    .clickable { destination = dest }
                                    .testTag("planner_dest_$dest")
                            ) {
                                Text(
                                    text = dest,
                                    color = if (isSelected) Color.White else Slate700,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Duration
                    Text("2. Thời gian chuyến đi", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        durations.forEach { (days, label) ->
                            val isSelected = durationDays == days
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) TealPrimary else Slate100,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { durationDays = days }
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else Slate700,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Style & Travelers
                    Text("3. Phong cách chuyến đi", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(styles) { style ->
                            val isSelected = travelStyle == style
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) TealPrimary else Slate100,
                                modifier = Modifier.clickable { travelStyle = style }
                            ) {
                                Text(
                                    text = style,
                                    color = if (isSelected) Color.White else Slate700,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Budget
                    Text("4. Ngân sách dự kiến (VND)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        budgetOptions.forEach { amount ->
                            val isSelected = budgetVnd == amount
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) TealPrimary else Slate100,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { budgetVnd = amount }
                            ) {
                                Text(
                                    text = "${amount / 1000000} triệu",
                                    color = if (isSelected) Color.White else Slate700,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 5. Prompt note
                    Text("5. Yêu cầu chi tiết cho Peuin AI", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = promptNote,
                        onValueChange = { promptNote = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Slate100,
                            unfocusedContainerColor = Slate100,
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = Slate200
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Generate button
                    Button(
                        onClick = {
                            viewModel.generateAiTrip(
                                destination = destination,
                                durationDays = durationDays,
                                budgetVnd = budgetVnd,
                                travelers = travelersCount,
                                interests = listOf("Cà phê", "Ẩm thực", "Ngắm cảnh", "Chill"),
                                promptText = promptNote
                            )
                        },
                        enabled = !isAiGenerating,
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("generate_ai_trip_button")
                    ) {
                        if (isAiGenerating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Peuin đang tính toán và tối ưu lộ trình...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tạo lịch trình ${durationDays}N cùng Peuin AI", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
