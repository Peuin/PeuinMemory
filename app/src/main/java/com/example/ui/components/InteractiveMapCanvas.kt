package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.PlaceEntity
import com.example.data.model.PlaceCategory
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

@Composable
fun InteractiveMapCanvas(
    places: List<PlaceEntity>,
    selectedPlace: PlaceEntity?,
    onSelectPlace: (PlaceEntity?) -> Unit,
    onOpenDetail: (PlaceEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var zoomLevel by remember { mutableFloatStateOf(1f) }
    var showItineraryRoute by remember { mutableStateOf(true) }

    // Map bounds in Dalat coordinates
    val minLat = 11.885
    val maxLat = 11.965
    val minLng = 108.410
    val maxLng = 108.490

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE8ECEF))
            .testTag("interactive_map_canvas")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(places) {
                    detectTapGestures { tapOffset ->
                        val w = size.width
                        val h = size.height

                        // Find closest place
                        val clickedPlace = places.minByOrNull { p ->
                            val px = ((p.lng - minLng) / (maxLng - minLng) * w).toFloat()
                            val py = ((maxLat - p.lat) / (maxLat - minLat) * h).toFloat()
                            val dx = px - tapOffset.x
                            val dy = py - tapOffset.y
                            dx * dx + dy * dy
                        }

                        if (clickedPlace != null) {
                            val px = ((clickedPlace.lng - minLng) / (maxLng - minLng) * w).toFloat()
                            val py = ((maxLat - clickedPlace.lat) / (maxLat - minLat) * h).toFloat()
                            val dist = kotlin.math.sqrt((px - tapOffset.x) * (px - tapOffset.x) + (py - tapOffset.y) * (py - tapOffset.y))
                            if (dist < 80f) {
                                onSelectPlace(clickedPlace)
                            } else {
                                onSelectPlace(null)
                            }
                        } else {
                            onSelectPlace(null)
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            // 1. Terrain & Pine forest background patches
            drawRoundRect(
                color = Color(0xFFD4E7D6),
                topLeft = Offset(width * 0.05f, height * 0.55f),
                size = Size(width * 0.45f, height * 0.4f),
                cornerRadius = CornerRadius(80f, 80f)
            )
            drawRoundRect(
                color = Color(0xFFDCEAD8),
                topLeft = Offset(width * 0.5f, height * 0.1f),
                size = Size(width * 0.45f, height * 0.35f),
                cornerRadius = CornerRadius(90f, 90f)
            )

            // 2. Xuan Huong Lake & Tuyen Lam Lake water features
            val xuanHuongLake = Path().apply {
                moveTo(width * 0.45f, height * 0.35f)
                cubicTo(
                    width * 0.58f, height * 0.32f,
                    width * 0.65f, height * 0.45f,
                    width * 0.52f, height * 0.52f
                )
                cubicTo(
                    width * 0.42f, height * 0.56f,
                    width * 0.38f, height * 0.40f,
                    width * 0.45f, height * 0.35f
                )
            }
            drawPath(xuanHuongLake, color = Color(0xFF90CAF9))

            // Tuyen Lam Lake (South-West)
            val tuyenLamLake = Path().apply {
                moveTo(width * 0.12f, height * 0.72f)
                cubicTo(
                    width * 0.28f, height * 0.68f,
                    width * 0.35f, height * 0.85f,
                    width * 0.22f, height * 0.92f
                )
                cubicTo(
                    width * 0.10f, height * 0.90f,
                    width * 0.08f, height * 0.78f,
                    width * 0.12f, height * 0.72f
                )
            }
            drawPath(tuyenLamLake, color = Color(0xFF90CAF9))

            // 3. Roads & Passways
            val mainRoad1 = Path().apply {
                moveTo(0f, height * 0.42f)
                lineTo(width * 0.45f, height * 0.45f)
                lineTo(width * 0.85f, height * 0.65f)
                lineTo(width, height * 0.75f)
            }
            drawPath(
                mainRoad1,
                color = Color(0xFFFFFFFF),
                style = Stroke(width = 14f)
            )
            drawPath(
                mainRoad1,
                color = Color(0xFFCBD5E1),
                style = Stroke(width = 10f)
            )

            val mainRoad2 = Path().apply {
                moveTo(width * 0.5f, 0f)
                lineTo(width * 0.48f, height * 0.45f)
                lineTo(width * 0.25f, height * 0.75f)
                lineTo(width * 0.15f, height)
            }
            drawPath(
                mainRoad2,
                color = Color(0xFFFFFFFF),
                style = Stroke(width = 12f)
            )
            drawPath(
                mainRoad2,
                color = Color(0xFFCBD5E1),
                style = Stroke(width = 8f)
            )

            // 4. Connected Itinerary Route Path (Cyan & Coral pulse)
            if (showItineraryRoute && places.size >= 2) {
                val routePath = Path()
                places.take(5).forEachIndexed { index, place ->
                    val x = ((place.lng - minLng) / (maxLng - minLng) * width).toFloat()
                    val y = ((maxLat - place.lat) / (maxLat - minLat) * height).toFloat()
                    if (index == 0) routePath.moveTo(x, y) else routePath.lineTo(x, y)
                }

                // Dotted route line
                drawPath(
                    routePath,
                    color = TealPrimary,
                    style = Stroke(
                        width = 6f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f)
                    )
                )
            }

            // 5. Place Markers
            places.forEachIndexed { index, place ->
                val x = ((place.lng - minLng) / (maxLng - minLng) * width).toFloat()
                val y = ((maxLat - place.lat) / (maxLat - minLat) * height).toFloat()
                val isSelected = selectedPlace?.id == place.id

                val markerColor = when (place.category) {
                    PlaceCategory.CAFE -> CoralSecondary
                    PlaceCategory.ATTRACTION -> TealPrimary
                    PlaceCategory.FOOD -> AmberAccent
                    PlaceCategory.LOCAL_EXPERIENCE -> SkyAccent
                    PlaceCategory.EMERGENCY -> Color(0xFFDC2626)
                    else -> TealDark
                }

                // Ambient glow for selected
                if (isSelected) {
                    drawCircle(
                        color = markerColor.copy(alpha = 0.3f),
                        radius = 36f,
                        center = Offset(x, y)
                    )
                }

                // Outer Pin Circle
                drawCircle(
                    color = Color.White,
                    radius = if (isSelected) 22f else 18f,
                    center = Offset(x, y)
                )
                drawCircle(
                    color = markerColor,
                    radius = if (isSelected) 18f else 14f,
                    center = Offset(x, y)
                )

                // Sequence number on marker
                drawCircle(
                    color = Color.White,
                    radius = if (isSelected) 8f else 6f,
                    center = Offset(x, y)
                )
            }
        }

        // Floating Map Controls (Top Right)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column {
                    IconButton(
                        onClick = { zoomLevel += 0.2f },
                        modifier = Modifier.size(38.dp).testTag("map_zoom_in")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Slate700)
                    }
                    Box(modifier = Modifier.width(38.dp).height(1.dp).background(Slate200))
                    IconButton(
                        onClick = { zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.8f) },
                        modifier = Modifier.size(38.dp).testTag("map_zoom_out")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Slate700)
                    }
                }
            }

            Surface(
                shape = CircleShape,
                color = if (showItineraryRoute) TealPrimary else Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { showItineraryRoute = !showItineraryRoute }
                    .testTag("toggle_route_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = "Toggle Route",
                        tint = if (showItineraryRoute) Color.White else Slate700,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onSelectPlace(places.firstOrNull()) }
                    .testTag("center_my_location")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.LocationSearching,
                        contentDescription = "My Location",
                        tint = TealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Live location pill (Top Left)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Slate900.copy(alpha = 0.8f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(SuccessGreen, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "GPS: Đà Lạt • Đang định vị",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Bottom Place Preview Bottom Sheet Card when a place is tapped
        if (selectedPlace != null) {
            Card(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 0.dp)
                    .testTag("map_place_preview_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(modifier = Modifier.weight(1f)) {
                            AsyncImage(
                                model = selectedPlace.imageUrl,
                                contentDescription = selectedPlace.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = TealContainer
                                ) {
                                    Text(
                                        text = selectedPlace.category.displayName,
                                        color = TealDark,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = selectedPlace.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = AmberAccent,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${selectedPlace.rating}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• Cách bạn ${selectedPlace.distanceKm} km",
                                        fontSize = 11.sp,
                                        color = Slate500
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = { onSelectPlace(null) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = selectedPlace.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate700,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Slate100,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectPlace(null) }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Directions, contentDescription = null, tint = Slate700, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Chỉ đường",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate700
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = TealPrimary,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onOpenDetail(selectedPlace) }
                                .testTag("view_place_detail_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Explore, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Xem chi tiết",
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
