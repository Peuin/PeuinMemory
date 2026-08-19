package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.modals.AiTripPlannerDialog
import com.example.ui.modals.AskPeuinBottomSheet
import com.example.ui.modals.AuthAndLocationSheet
import com.example.ui.modals.CreateMemoryDialog
import com.example.ui.modals.OnboardingPreferencesDialog
import com.example.ui.modals.PlaceDetailSheet
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.ItineraryScreen
import com.example.ui.screens.MapScreen
import com.example.ui.screens.MemoriesScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.BorderColor
import com.example.ui.theme.HighDensityContainer
import com.example.ui.theme.HighDensityDark
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.PeuinTheme
import com.example.ui.theme.Slate700
import com.example.ui.theme.SurfaceVariantLight
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.PeuinViewModel

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import com.example.ui.theme.HighDensityAccentPill
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate900

class MainActivity : ComponentActivity() {
    private val viewModel: PeuinViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PeuinTheme {
                PeuinApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PeuinApp(viewModel: PeuinViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isAskPeuinOpen by viewModel.isAskPeuinOpen.collectAsState()
    val isTripPlannerOpen by viewModel.isTripPlannerOpen.collectAsState()
    val isCreateMemoryOpen by viewModel.isCreateMemoryOpen.collectAsState()
    val isOnboardingPreferencesOpen by viewModel.isOnboardingPreferencesOpen.collectAsState()
    val isAuthLocationSheetOpen by viewModel.isAuthLocationSheetOpen.collectAsState()
    val selectedPlaceForDetail by viewModel.selectedPlaceForDetail.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("peuin_main_scaffold"),
        bottomBar = {
            PeuinBottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.isAskPeuinOpen.value = true },
                shape = RoundedCornerShape(20.dp),
                containerColor = HighDensityPrimary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 10.dp
                ),
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .size(54.dp)
                    .testTag("floating_ask_peuin_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Hỏi Peuin AI",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Directional animated transition between bottom navigation screens
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    val direction = targetState.ordinal.compareTo(initialState.ordinal)
                    if (direction >= 0) {
                        (slideInHorizontally(
                            animationSpec = tween(durationMillis = 280),
                            initialOffsetX = { fullWidth -> fullWidth / 4 }
                        ) + fadeIn(animationSpec = tween(280)))
                            .togetherWith(
                                slideOutHorizontally(
                                    animationSpec = tween(durationMillis = 280),
                                    targetOffsetX = { fullWidth -> -fullWidth / 4 }
                                ) + fadeOut(animationSpec = tween(200))
                            )
                    } else {
                        (slideInHorizontally(
                            animationSpec = tween(durationMillis = 280),
                            initialOffsetX = { fullWidth -> -fullWidth / 4 }
                        ) + fadeIn(animationSpec = tween(280)))
                            .togetherWith(
                                slideOutHorizontally(
                                    animationSpec = tween(durationMillis = 280),
                                    targetOffsetX = { fullWidth -> fullWidth / 4 }
                                ) + fadeOut(animationSpec = tween(200))
                            )
                    }.using(SizeTransform(clip = false))
                },
                label = "navigation_tab_transition"
            ) { tab ->
                when (tab) {
                    MainTab.EXPLORE -> ExploreScreen(viewModel = viewModel)
                    MainTab.MAP -> MapScreen(viewModel = viewModel)
                    MainTab.ITINERARY -> ItineraryScreen(viewModel = viewModel)
                    MainTab.MEMORIES -> MemoriesScreen(viewModel = viewModel)
                    MainTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                }
            }
        }

        // Modals & Bottom Sheets
        if (isAskPeuinOpen) {
            AskPeuinBottomSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.isAskPeuinOpen.value = false }
            )
        }

        if (isTripPlannerOpen) {
            AiTripPlannerDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.isTripPlannerOpen.value = false }
            )
        }

        if (isCreateMemoryOpen) {
            CreateMemoryDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.isCreateMemoryOpen.value = false }
            )
        }

        if (isOnboardingPreferencesOpen) {
            OnboardingPreferencesDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.isOnboardingPreferencesOpen.value = false }
            )
        }

        if (isAuthLocationSheetOpen) {
            AuthAndLocationSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.isAuthLocationSheetOpen.value = false }
            )
        }

        if (selectedPlaceForDetail != null) {
            PlaceDetailSheet(
                place = selectedPlaceForDetail!!,
                viewModel = viewModel,
                onDismiss = { viewModel.selectedPlaceForDetail.value = null }
            )
        }
    }
}

fun getTabIcon(tab: MainTab): ImageVector {
    return when (tab) {
        MainTab.EXPLORE -> Icons.Default.Explore
        MainTab.MAP -> Icons.Default.Place
        MainTab.ITINERARY -> Icons.Default.CalendarToday
        MainTab.MEMORIES -> Icons.Default.PhotoLibrary
        MainTab.PROFILE -> Icons.Default.Person
    }
}

@Composable
fun PeuinBottomNavigationBar(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, Color(0xFFECEFF1)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("peuin_bottom_navigation_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(66.dp)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainTab.values().forEach { tab ->
                val isSelected = currentTab == tab
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "tab_scale"
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) HighDensityContainer.copy(alpha = 0.7f) else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .scale(scale)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(tab) }
                        .testTag("nav_tab_${tab.name.lowercase()}")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = getTabIcon(tab),
                                contentDescription = tab.title,
                                tint = if (isSelected) HighDensityPrimary else Slate500,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = tab.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) HighDensityDark else Slate500,
                            maxLines = 1
                        )

                        // Subtle active indicator dot
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .background(HighDensityPrimary, CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}
