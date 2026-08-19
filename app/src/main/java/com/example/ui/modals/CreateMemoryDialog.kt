package com.example.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.PeuinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMemoryDialog(
    viewModel: PeuinViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var placeName by remember { mutableStateOf("Hồ Xuân Hương, Đà Lạt") }
    var note by remember { mutableStateOf("Một chiều hoàng hôn tuyệt đẹp bên bờ hồ, không khí se lạnh và ly sữa đậu nành nóng hổi...") }
    var selectedMood by remember { mutableStateOf("Chill & Bình yên 🌿") }
    var selectedCompanion by remember { mutableStateOf("Cùng người thương 💖") }

    val moods = listOf("Chill & Bình yên 🌿", "Lãng mạn 💖", "Phiêu lưu 🚀", "Hạnh phúc ☀️", "Ấm cúng ☕")
    val companions = listOf("Cùng người thương 💖", "Gia đình 👨‍👩‍👧", "Hội bạn thân 🎒", "Một mình 📸")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.testTag("create_memory_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ghi lại kỷ niệm du lịch",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("Địa điểm", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate700)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = placeName,
                onValueChange = { placeName = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Slate100,
                    unfocusedContainerColor = Slate100,
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = Slate200
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Cảm xúc & Tâm trạng", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate700)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(moods) { mood ->
                    val isSelected = selectedMood == mood
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) TealPrimary else Slate100,
                        modifier = Modifier.clickable { selectedMood = mood }
                    ) {
                        Text(
                            text = mood,
                            color = if (isSelected) Color.White else Slate700,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Bạn đồng hành", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate700)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(companions) { comp ->
                    val isSelected = selectedCompanion == comp
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) CoralSecondary else Slate100,
                        modifier = Modifier.clickable { selectedCompanion = comp }
                    ) {
                        Text(
                            text = comp,
                            color = if (isSelected) Color.White else Slate700,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Chia sẻ cảm nghĩ & câu chuyện", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate700)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Slate100,
                    unfocusedContainerColor = Slate100,
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = Slate200
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    viewModel.createMemory(
                        placeName = placeName,
                        note = note,
                        mood = selectedMood,
                        companions = selectedCompanion,
                        imageUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=800&auto=format&fit=crop&q=80"
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_memory_button")
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Lưu kỷ niệm & Tạo AI Memory Card", fontWeight = FontWeight.Bold)
            }
        }
    }
}
