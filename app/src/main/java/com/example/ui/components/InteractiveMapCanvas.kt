package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.PlaceEntity
import com.example.data.location.UserLocation
import com.example.data.model.PlaceCategory
import com.example.ui.theme.BorderColor
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900

enum class MapLayerType(val label: String) {
    DEFAULT("Mặc định"),
    SATELLITE("Vệ tinh"),
    TERRAIN("Địa hình")
}

@Composable
fun InteractiveMapCanvas(
    places: List<PlaceEntity>,
    selectedPlace: PlaceEntity?,
    onSelectPlace: (PlaceEntity?) -> Unit,
    onOpenDetail: (PlaceEntity) -> Unit,
    modifier: Modifier = Modifier,
    userLocation: UserLocation? = null,
    onRequestLocationPermission: () -> Unit = {}
) {
    var zoomLevel by remember { mutableFloatStateOf(1f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }
    var showItineraryRoute by remember { mutableStateOf(true) }
    var showTraffic by remember { mutableStateOf(true) }
    var currentMapLayer by remember { mutableStateOf(MapLayerType.DEFAULT) }
    var showLayersMenu by remember { mutableStateOf(false) }

    // Map bounds in Dalat coordinates
    val minLat = 11.885
    val maxLat = 11.965
    val minLng = 108.410
    val maxLng = 108.490

    // Google Maps Color Palettes
    val baseMapColor = when (currentMapLayer) {
        MapLayerType.DEFAULT -> Color(0xFFF1EFE8)
        MapLayerType.SATELLITE -> Color(0xFF1F2E24)
        MapLayerType.TERRAIN -> Color(0xFFE5DECE)
    }

    val parkColor = when (currentMapLayer) {
        MapLayerType.DEFAULT -> Color(0xFFCCEADA)
        MapLayerType.SATELLITE -> Color(0xFF183820)
        MapLayerType.TERRAIN -> Color(0xFFC5E3CE)
    }

    val waterColor = when (currentMapLayer) {
        MapLayerType.DEFAULT -> Color(0xFFAADAFF)
        MapLayerType.SATELLITE -> Color(0xFF143046)
        MapLayerType.TERRAIN -> Color(0xFF98CAEE)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseMapColor)
            .testTag("interactive_map_canvas")
    ) {
        // Main Google Maps Vector Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(places) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomLevel = (zoomLevel * zoom).coerceIn(0.7f, 2.5f)
                        panOffsetX += pan.x
                        panOffsetY += pan.y
                    }
                }
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
                            val dist = kotlin.math.sqrt(
                                (px - tapOffset.x) * (px - tapOffset.x) + (py - tapOffset.y) * (py - tapOffset.y)
                            )
                            if (dist < 90f) {
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

            // 1. Urban Blocks / Building footprint patches
            val blockPaint = Color(0xFFE8E5DD)
            drawRoundRect(
                color = blockPaint,
                topLeft = Offset(width * 0.35f, height * 0.25f),
                size = Size(width * 0.30f, height * 0.22f),
                cornerRadius = CornerRadius(16f, 16f)
            )
            drawRoundRect(
                color = blockPaint,
                topLeft = Offset(width * 0.40f, height * 0.50f),
                size = Size(width * 0.25f, height * 0.18f),
                cornerRadius = CornerRadius(16f, 16f)
            )

            // 2. Parks & Pine Hill Natural Reserves
            // Prenn & Tuyen Lam Valley Forest
            drawRoundRect(
                color = parkColor,
                topLeft = Offset(width * 0.04f, height * 0.55f),
                size = Size(width * 0.48f, height * 0.42f),
                cornerRadius = CornerRadius(80f, 80f)
            )
            // Robin Hill & Cable Car forest
            drawRoundRect(
                color = parkColor,
                topLeft = Offset(width * 0.52f, height * 0.08f),
                size = Size(width * 0.44f, height * 0.38f),
                cornerRadius = CornerRadius(90f, 90f)
            )
            // City Flower Garden & Golf Course Park
            drawRoundRect(
                color = parkColor,
                topLeft = Offset(width * 0.48f, height * 0.26f),
                size = Size(width * 0.38f, height * 0.18f),
                cornerRadius = CornerRadius(40f, 40f)
            )

            // 3. Google Maps Lakes & Water Bodies
            // Xuan Huong Lake (Organic curved crescent shape)
            val xuanHuongLake = Path().apply {
                moveTo(width * 0.42f, height * 0.36f)
                cubicTo(
                    width * 0.56f, height * 0.31f,
                    width * 0.68f, height * 0.42f,
                    width * 0.58f, height * 0.51f
                )
                cubicTo(
                    width * 0.44f, height * 0.55f,
                    width * 0.36f, height * 0.42f,
                    width * 0.42f, height * 0.36f
                )
            }
            // Shoreline highlight
            drawPath(
                xuanHuongLake,
                color = waterColor.copy(alpha = 0.5f),
                style = Stroke(width = 8f)
            )
            drawPath(xuanHuongLake, color = waterColor)

            // Tuyen Lam Lake (South-West)
            val tuyenLamLake = Path().apply {
                moveTo(width * 0.10f, height * 0.70f)
                cubicTo(
                    width * 0.26f, height * 0.65f,
                    width * 0.36f, height * 0.82f,
                    width * 0.24f, height * 0.94f
                )
                cubicTo(
                    width * 0.08f, height * 0.92f,
                    width * 0.06f, height * 0.76f,
                    width * 0.10f, height * 0.70f
                )
            }
            drawPath(
                tuyenLamLake,
                color = waterColor.copy(alpha = 0.5f),
                style = Stroke(width = 8f)
            )
            drawPath(tuyenLamLake, color = waterColor)

            // Than Tho Lake (East)
            val thanThoLake = Path().apply {
                moveTo(width * 0.82f, height * 0.28f)
                cubicTo(
                    width * 0.92f, height * 0.26f,
                    width * 0.95f, height * 0.38f,
                    width * 0.85f, height * 0.40f
                )
                close()
            }
            drawPath(thanThoLake, color = waterColor)

            // 4. Google Maps Road Network
            // Arterial Highway (QL20 - Tran Phu - Hung Vuong) [Google Yellow/Orange]
            val nationalHighway = Path().apply {
                moveTo(0f, height * 0.42f)
                lineTo(width * 0.42f, height * 0.45f)
                lineTo(width * 0.70f, height * 0.58f)
                lineTo(width, height * 0.70f)
            }
            // Highway Border
            drawPath(
                nationalHighway,
                color = Color(0xFFE2B357),
                style = Stroke(width = 16f)
            )
            // Highway Surface (Google Maps Yellow)
            drawPath(
                nationalHighway,
                color = Color(0xFFFED980),
                style = Stroke(width = 12f)
            )

            // Secondary Arterial Road (3 Thang 4 - Prenn Pass to Dalat Center)
            val arterialRoad = Path().apply {
                moveTo(width * 0.48f, 0f)
                lineTo(width * 0.46f, height * 0.44f)
                lineTo(width * 0.24f, height * 0.74f)
                lineTo(width * 0.14f, height)
            }
            drawPath(
                arterialRoad,
                color = Color(0xFFDADCE0),
                style = Stroke(width = 14f)
            )
            drawPath(
                arterialRoad,
                color = Color.White,
                style = Stroke(width = 10f)
            )

            // Local Street Grid & Ring Roads
            val localRoads = Path().apply {
                // Xuan Huong lakeside promenade
                moveTo(width * 0.34f, height * 0.34f)
                lineTo(width * 0.60f, height * 0.28f)
                lineTo(width * 0.68f, height * 0.46f)
                lineTo(width * 0.48f, height * 0.58f)
                close()

                // Route to Trai Mat / Cau Dat
                moveTo(width * 0.70f, height * 0.58f)
                lineTo(width * 0.95f, height * 0.48f)

                // Route to Lang Biang
                moveTo(width * 0.46f, height * 0.20f)
                lineTo(width * 0.28f, height * 0.08f)
            }
            drawPath(
                localRoads,
                color = Color(0xFFE0E0E0),
                style = Stroke(width = 8f)
            )
            drawPath(
                localRoads,
                color = Color(0xFFFAFAFA),
                style = Stroke(width = 6f)
            )

            // 5. Google Maps Real-Time Traffic Overlays (if enabled)
            if (showTraffic) {
                // Smooth traffic flow (Google Green)
                val trafficGreen = Path().apply {
                    moveTo(width * 0.12f, height * 0.43f)
                    lineTo(width * 0.36f, height * 0.44f)
                }
                drawPath(
                    trafficGreen,
                    color = Color(0xFF0F9D58),
                    style = Stroke(width = 4f)
                )

                // Moderate traffic (Google Amber)
                val trafficAmber = Path().apply {
                    moveTo(width * 0.36f, height * 0.44f)
                    lineTo(width * 0.46f, height * 0.46f)
                }
                drawPath(
                    trafficAmber,
                    color = Color(0xFFFBBC04),
                    style = Stroke(width = 4f)
                )
            }

            // 6. Google Maps Active Itinerary Route (Google Blue polyline)
            if (showItineraryRoute && places.size >= 2) {
                val routePath = Path()
                places.take(6).forEachIndexed { index, place ->
                    val x = ((place.lng - minLng) / (maxLng - minLng) * width).toFloat()
                    val y = ((maxLat - place.lat) / (maxLat - minLat) * height).toFloat()
                    if (index == 0) routePath.moveTo(x, y) else routePath.lineTo(x, y)
                }

                // Google Blue Route Outer Border
                drawPath(
                    routePath,
                    color = Color(0xFF185ABC),
                    style = Stroke(width = 8f)
                )
                // Google Blue Route Line
                drawPath(
                    routePath,
                    color = Color(0xFF4285F4),
                    style = Stroke(width = 5f)
                )
            }

            // 7. Render Google Maps Standard POI & Teardrop Pins
            places.forEachIndexed { index, place ->
                val x = ((place.lng - minLng) / (maxLng - minLng) * width).toFloat()
                val y = ((maxLat - place.lat) / (maxLat - minLat) * height).toFloat()
                val isSelected = selectedPlace?.id == place.id

                // Google Maps Category Pin Colors
                val pinColor = when (place.category) {
                    PlaceCategory.FOOD -> Color(0xFFEA4335) // Google Red
                    PlaceCategory.CAFE -> Color(0xFFF29900) // Google Amber Orange
                    PlaceCategory.ATTRACTION -> Color(0xFF1A73E8) // Google Blue
                    PlaceCategory.LOCAL_EXPERIENCE -> Color(0xFF1E8E3E) // Google Green
                    PlaceCategory.HOTEL -> Color(0xFF9334E6) // Google Purple
                    PlaceCategory.SHOPPING -> Color(0xFF185ABC) // Dark Blue
                    PlaceCategory.EMERGENCY -> Color(0xFFD93025) // Red
                    else -> Color(0xFF1A73E8)
                }

                // Shadow under pin
                drawCircle(
                    color = Color(0x33000000),
                    radius = if (isSelected) 14f else 10f,
                    center = Offset(x, y + 2f)
                )

                if (isSelected) {
                    // Pulsing selection ring
                    drawCircle(
                        color = Color(0xFF4285F4).copy(alpha = 0.25f),
                        radius = 42f,
                        center = Offset(x, y - 18f)
                    )
                }

                // Classic Google Maps Teardrop Shape Path
                val pinPath = Path().apply {
                    val topCenterY = y - (if (isSelected) 26f else 20f)
                    val pinRadius = if (isSelected) 18f else 14f
                    moveTo(x, y)
                    lineTo(x - pinRadius * 0.85f, topCenterY + pinRadius * 0.4f)
                    cubicTo(
                        x - pinRadius * 1.2f, topCenterY - pinRadius,
                        x + pinRadius * 1.2f, topCenterY - pinRadius,
                        x + pinRadius * 0.85f, topCenterY + pinRadius * 0.4f
                    )
                    close()
                }

                // Draw Pin Body
                drawPath(pinPath, color = pinColor)
                // White outline on pin for contrast
                drawPath(pinPath, color = Color.White, style = Stroke(width = 2.5f))

                // Inner white badge dot
                drawCircle(
                    color = Color.White,
                    radius = if (isSelected) 7f else 5.5f,
                    center = Offset(x, y - (if (isSelected) 26f else 20f))
                )

                // Sequence badge
                drawCircle(
                    color = pinColor,
                    radius = if (isSelected) 3.5f else 2.5f,
                    center = Offset(x, y - (if (isSelected) 26f else 20f))
                )
            }

            // 8. User Current GPS Location Marker (Google Blue Halo & Arrow)
            if (userLocation != null) {
                val uLat = userLocation.latitude.coerceIn(minLat, maxLat)
                val uLng = userLocation.longitude.coerceIn(minLng, maxLng)
                val ux = ((uLng - minLng) / (maxLng - minLng) * width).toFloat()
                val uy = ((maxLat - uLat) / (maxLat - minLat) * height).toFloat()

                // Google Blue Accuracy Halo
                drawCircle(
                    color = Color(0xFF4285F4).copy(alpha = 0.20f),
                    radius = 36f,
                    center = Offset(ux, uy)
                )
                // White Border
                drawCircle(
                    color = Color.White,
                    radius = 12f,
                    center = Offset(ux, uy)
                )
                // Blue GPS Dot (Google Blue)
                drawCircle(
                    color = Color(0xFF1A73E8),
                    radius = 8.5f,
                    center = Offset(ux, uy)
                )
                // Center Pulse core
                drawCircle(
                    color = Color.White,
                    radius = 3f,
                    center = Offset(ux, uy)
                )
            }
        }

        // --- Google Maps Floating Controls ---

        // Top Right: Compass, Layer Selector, Traffic Toggle, Zoom Controls
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 90.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Google Maps Compass Button
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 5.dp,
                modifier = Modifier
                    .size(42.dp)
                    .clickable {
                        panOffsetX = 0f
                        panOffsetY = 0f
                        zoomLevel = 1f
                    }
                    .testTag("map_compass_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Compass",
                        tint = Color(0xFFEA4335), // Google Red North Arrow
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(-45f)
                    )
                }
            }

            // Google Maps Layers Toggle FAB
            Surface(
                shape = CircleShape,
                color = if (showLayersMenu) Color(0xFF1A73E8) else Color.White,
                shadowElevation = 5.dp,
                modifier = Modifier
                    .size(42.dp)
                    .clickable { showLayersMenu = !showLayersMenu }
                    .testTag("map_layers_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Layers",
                        tint = if (showLayersMenu) Color.White else Slate700,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Live Traffic Toggle FAB
            Surface(
                shape = CircleShape,
                color = if (showTraffic) Color(0xFF0F9D58) else Color.White,
                shadowElevation = 5.dp,
                modifier = Modifier
                    .size(42.dp)
                    .clickable { showTraffic = !showTraffic }
                    .testTag("map_traffic_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Traffic,
                        contentDescription = "Traffic",
                        tint = if (showTraffic) Color.White else Slate700,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Zoom in / out controls
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 5.dp
            ) {
                Column {
                    IconButton(
                        onClick = { zoomLevel = (zoomLevel + 0.25f).coerceAtMost(2.5f) },
                        modifier = Modifier.size(40.dp).testTag("map_zoom_in")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Slate700)
                    }
                    Box(modifier = Modifier.width(40.dp).height(1.dp).background(Slate200))
                    IconButton(
                        onClick = { zoomLevel = (zoomLevel - 0.25f).coerceAtLeast(0.7f) },
                        modifier = Modifier.size(40.dp).testTag("map_zoom_out")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Slate700)
                    }
                }
            }
        }

        // Bottom Right: Google Maps My Location GPS Button & Route Toggle
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = if (selectedPlace != null) 230.dp else 90.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Toggle Route Visibility Button
            Surface(
                shape = CircleShape,
                color = if (showItineraryRoute) Color(0xFF1A73E8) else Color.White,
                shadowElevation = 5.dp,
                modifier = Modifier
                    .size(46.dp)
                    .clickable { showItineraryRoute = !showItineraryRoute }
                    .testTag("toggle_route_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = "Toggle Route",
                        tint = if (showItineraryRoute) Color.White else Slate700,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Google Maps GPS Centering Button
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 5.dp,
                modifier = Modifier
                    .size(46.dp)
                    .clickable {
                        onRequestLocationPermission()
                        panOffsetX = 0f
                        panOffsetY = 0f
                        onSelectPlace(places.firstOrNull())
                    }
                    .testTag("center_my_location")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "My Location",
                        tint = Color(0xFF1A73E8), // Google Blue GPS
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Layer Selection Popup Menu
        AnimatedVisibility(
            visible = showLayersMenu,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 140.dp, end = 68.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier.width(180.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Loại bản đồ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    MapLayerType.values().forEach { layer ->
                        val isSelected = currentMapLayer == layer
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFE8F0FE) else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentMapLayer = layer
                                    showLayersMenu = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = null,
                                    tint = if (isSelected) Color(0xFF1A73E8) else Slate500,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = layer.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF1A73E8) else Slate700
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Google Maps Place Preview Card (Bottom Floating Sheet) ---
        if (selectedPlace != null) {
            Card(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .testTag("map_place_preview_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Top drag pill
                    Box(
                        modifier = Modifier
                            .size(36.dp, 4.dp)
                            .background(Slate200, RoundedCornerShape(2.dp))
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedPlace.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${selectedPlace.rating}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                                Text(
                                    text = " (${selectedPlace.reviewCount} đánh giá)",
                                    fontSize = 11.sp,
                                    color = Slate500
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${selectedPlace.category.displayName}",
                                    fontSize = 11.sp,
                                    color = Slate700
                                )
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFE6F4EA)
                                ) {
                                    Text(
                                        text = "Đang mở cửa",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF137333),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• Đóng cửa ${selectedPlace.openingHours.substringAfter("- ", "22:00")}",
                                    fontSize = 11.sp,
                                    color = Slate500
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        AsyncImage(
                            model = selectedPlace.imageUrl,
                            contentDescription = selectedPlace.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = selectedPlace.address,
                        fontSize = 11.sp,
                        color = Slate500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Google Maps Iconic Action Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Blue "Đường đi / Directions" Google Maps Button
                        Button(
                            onClick = { onOpenDetail(selectedPlace) },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Directions,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Đường đi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // "Bắt đầu / Start" Pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFE8F0FE),
                            modifier = Modifier
                                .height(38.dp)
                                .clickable { onOpenDetail(selectedPlace) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Navigation,
                                    contentDescription = null,
                                    tint = Color(0xFF1A73E8),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Bắt đầu", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A73E8))
                            }
                        }

                        // "Lưu / Save" Pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Slate100,
                            modifier = Modifier
                                .height(38.dp)
                                .clickable { /* Save */ }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = null,
                                    tint = Slate700,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Lưu", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                            }
                        }

                        // "Chi tiết" Button
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Slate100,
                            modifier = Modifier
                                .height(38.dp)
                                .clickable { onOpenDetail(selectedPlace) }
                                .testTag("view_place_detail_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = null,
                                    tint = Slate700,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chi tiết", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                            }
                        }
                    }
                }
            }
        }
    }
}
