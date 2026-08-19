package com.example.ui.modals

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderColor
import com.example.ui.theme.HighDensityContainer
import com.example.ui.theme.HighDensityDark
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.PeuinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthAndLocationSheet(
    viewModel: PeuinViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val userLocation by viewModel.userLocation.collectAsState()
    val isLocating by viewModel.isLocating.collectAsState()
    val isLoggedInWithGoogle by viewModel.isLoggedInWithGoogle.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var permissionGranted by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            permissionGranted = true
            viewModel.refreshUserLocation()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.testTag("auth_and_location_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(HighDensityContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = null,
                            tint = HighDensityDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Định vị & Đăng nhập",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = HighDensityDark
                        )
                        Text(
                            text = "Google Maps API Location Service",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location Status Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Slate100.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "VỊ TRÍ HIỆN TẠI CỦA BẠN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate700,
                            letterSpacing = 1.sp
                        )
                        if (isLocating) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 2.dp,
                                    color = HighDensityPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Đang quét GPS...",
                                    fontSize = 10.sp,
                                    color = HighDensityPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SuccessGreen.copy(alpha = 0.12f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Đã định vị",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = HighDensityPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = userLocation?.cityName ?: "Đà Lạt, Lâm Đồng",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = HighDensityDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = userLocation?.fullAddress ?: "Tọa độ: 11.9404° N, 108.4583° E",
                                fontSize = 11.sp,
                                color = Slate700
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tọa độ: ${"%.4f".format(userLocation?.latitude ?: 11.9404)}° N, ${"%.4f".format(userLocation?.longitude ?: 108.4583)}° E",
                                fontSize = 10.sp,
                                color = Slate500
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Button(
                onClick = {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HighDensityPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("request_location_gps_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Lấy định vị GPS tức thì (Google Maps)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Google Login Section
            OutlinedButton(
                onClick = {
                    viewModel.loginWithGoogle(
                        userName = "Nguyễn Minh Châu",
                        email = "minhchau.travel@gmail.com"
                    )
                },
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("google_login_locate_btn")
            ) {
                Text(
                    text = "G",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4285F4)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isLoggedInWithGoogle) "Đã liên kết Google: ${userProfile.email}" else "Đăng nhập Google & Tự động định vị",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = HighDensityDark
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "💡 Tọa độ được sử dụng để tối ưu lộ trình di chuyển và hiển thị các quán ăn, điểm tham quan gần bạn nhất.",
                fontSize = 11.sp,
                color = Slate500,
                lineHeight = 15.sp
            )
        }
    }
}
