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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.UserProfile
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.PeuinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingPreferencesDialog(
    viewModel: PeuinViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentProfile: UserProfile by viewModel.userProfile.collectAsState()

    var departureCity by remember { mutableStateOf(currentProfile.departureCity) }
    var travelStyle by remember { mutableStateOf(currentProfile.travelStyle) }
    var budgetTier by remember { mutableStateOf(currentProfile.expectedBudgetTier) }

    val allInterests: List<String> = listOf("Cà phê view đẹp", "Thiên nhiên & Rừng thông", "Văn hoá & Lịch sử", "Ẩm thực địa phương", "Chụp ảnh check-in", "Workshop thủ công")
    val selectedInterests = remember { mutableStateListOf<String>().apply { addAll(currentProfile.interests) } }

    val allFoods: List<String> = listOf("Đặc sản nóng", "Món chay thanh đạm", "Hải sản tươi", "Bánh ngọt & Trà", "Nướng ngói", "Ẩm thực đường phố")
    val selectedFoods = remember { mutableStateListOf<String>().apply { addAll(currentProfile.foodPreferences) } }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.testTag("preferences_sheet")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Cá nhân hoá gu du lịch",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "Giúp Peuin AI đề xuất chính xác theo sở thích của bạn",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("Sở thích khám phá", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val interestPairs: List<List<String>> = allInterests.chunked(2)
                    interestPairs.forEach { rowItems: List<String> ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowItems.forEach { interest: String ->
                                val isSelected = selectedInterests.contains(interest)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) TealPrimary else Slate100,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            if (isSelected) selectedInterests.remove(interest)
                                            else selectedInterests.add(interest)
                                        }
                                ) {
                                    Text(
                                        text = interest,
                                        color = if (isSelected) Color.White else Slate700,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("Khẩu vị ẩm thực", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val foodPairs: List<List<String>> = allFoods.chunked(2)
                    foodPairs.forEach { rowItems: List<String> ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowItems.forEach { food: String ->
                                val isSelected = selectedFoods.contains(food)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) TealPrimary else Slate100,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            if (isSelected) selectedFoods.remove(food)
                                            else selectedFoods.add(food)
                                        }
                                ) {
                                    Text(
                                        text = food,
                                        color = if (isSelected) Color.White else Slate700,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Button(
                    onClick = {
                        val updated = currentProfile.copy(
                            departureCity = departureCity,
                            interests = selectedInterests.toList(),
                            foodPreferences = selectedFoods.toList(),
                            travelStyle = travelStyle,
                            expectedBudgetTier = budgetTier
                        )
                        viewModel.updatePreferences(updated)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_preferences_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Lưu thiết lập cá nhân", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
