package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.CustomStatusBarView
import com.example.ui.components.EmojiThemeCardPreview

@Composable
fun HomeScreen(
    config: StatusBarConfig,
    simulation: SimulationState,
    presets: List<Preset>,
    isOverlayGranted: Boolean,
    isOverlayActive: Boolean,
    onSimulationChange: (SimulationState) -> Unit,
    onConfigChange: (StatusBarConfig) -> Unit = {},
    onSelectPreset: (Preset) -> Unit,
    onSelectProfile: (DeviceProfile) -> Unit,
    onToggleOverlay: () -> Unit,
    onNavigateToCustomize: () -> Unit,
    onNavigateToPresets: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("Football") }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showVipDialog by remember { mutableStateOf(false) }
    var showPermissionSheet by remember { mutableStateOf(false) }

    val filteredThemes = remember(selectedCategory) {
        EmojiThemeCatalog.ALL_THEMES.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Top Header: "Emoji Battery Themes", Crown icon, Help ? icon (Screenshot 1)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Emoji Battery Themes",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // VIP Crown Icon
                    IconButton(
                        onClick = { showVipDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF3C4))
                    ) {
                        Text(text = "👑", fontSize = 18.sp)
                    }

                    // Help ? Icon
                    IconButton(
                        onClick = { showHelpDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0F2FE))
                    ) {
                        Text(
                            text = "?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        )
                    }
                }
            }
        }

        // 2. "Enable emoji battery to begin" Switch Card (Screenshot 1)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("enable_emoji_battery_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOverlayActive) {
                        Color(0xFFF3E8FF)
                    } else {
                        Color(0xFFF8F0FF)
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable emoji battery to begin",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E1065)
                        )
                        Text(
                            text = if (isOverlayActive) {
                                "Active! Floating on your main screen & apps"
                            } else if (!isOverlayGranted) {
                                "Tap to grant permission & show over apps"
                            } else {
                                "Tap switch to float over your home screen"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6B21A8)
                        )
                    }

                    Switch(
                        checked = isOverlayActive,
                        onCheckedChange = { checked ->
                            if (checked && !isOverlayGranted) {
                                showPermissionSheet = true
                            } else {
                                onToggleOverlay()
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF7C3AED),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE9D5FF)
                        ),
                        modifier = Modifier.testTag("overlay_service_switch")
                    )
                }
            }
        }

        // 3. Live Phone Status Bar Preview Header (showing real-time simulation / live clock & mascot)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Live Status Bar Preview",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isOverlayActive) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = "● LIVE ON SCREEN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF15803D),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Status Bar Preview Bar
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Black,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomStatusBarView(
                        config = config,
                        simulation = simulation,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Live Data Telemetry Strip: Real Battery, Real Speed, Real Signal, Real Time
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (simulation.isCharging) "⚡" else "🔋",
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${simulation.batteryLevel}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "🚀", fontSize = 12.sp)
                            Text(
                                text = "${simulation.liveNetworkSpeed} ${simulation.liveNetworkUnit}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "📶", fontSize = 12.sp)
                            Text(
                                text = "${simulation.liveNetworkBadge} (${simulation.liveVoBadge})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "🕒", fontSize = 12.sp)
                            Text(
                                text = simulation.simulatedTime,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Quick Action Card: Moto 5G++ Reference Style from Screenshot
                val isScreenshotStyleActive = !config.isEmojiBattery &&
                    config.batteryDesign == BatteryIconDesign.VERTICAL_CAPSULE &&
                    config.showNetworkSpeed &&
                    config.showVoNr &&
                    config.showNetworkBadge

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isScreenshotStyleActive) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    border = if (isScreenshotStyleActive) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF16A34A)) else null
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Screenshot Reference Style",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isScreenshotStyleActive) Color(0xFF14532D) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isScreenshotStyleActive) Color(0xFF16A34A) else Color(0xFF0284C7)
                                    ) {
                                        Text(
                                            text = if (isScreenshotStyleActive) "ACTIVE" else "MOTO 5G++",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Facebook, WhatsApp, Snapchat (x2), Dot • 23.9 KB/s, VoNR, 5G++, Triangle & 64% Battery",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isScreenshotStyleActive) Color(0xFF166534) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = {
                                    onConfigChange(
                                        config.copy(
                                            batteryDesign = BatteryIconDesign.VERTICAL_CAPSULE,
                                            batteryFillStyle = BatteryFillStyle.SOLID,
                                            percentageVisibility = BatteryPercentageVisibility.ALWAYS,
                                            showBatteryPercentage = true,
                                            showAppNotifications = true,
                                            notificationIcons = listOf("facebook", "whatsapp", "snapchat", "snapchat", "dot"),
                                            showNetworkSpeed = true,
                                            networkSpeedValue = "23.9",
                                            networkSpeedUnit = "KB/s",
                                            showVoNr = true,
                                            voNrText = "NR",
                                            showNetworkBadge = true,
                                            networkBadgeText = "5G++",
                                            showTrafficArrows = true,
                                            useTriangleSignal = true,
                                            deviceProfile = DeviceProfile.MOTOROLA,
                                            isEmojiBattery = false,
                                            colorNormal = 0xFFFFFFFF,
                                            textColor = 0xFFFFFFFF
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isScreenshotStyleActive) Color(0xFF16A34A) else MaterialTheme.colorScheme.primary
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isScreenshotStyleActive) "Applied" else "Apply Style",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Preview of the exact status bar on turquoise gradient matching Screenshot
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF34D399),
                                            Color(0xFF2DD4BF),
                                            Color(0xFF38BDF8)
                                        )
                                    )
                                )
                                .padding(vertical = 4.dp)
                        ) {
                            CustomStatusBarView(
                                config = config.copy(
                                    batteryDesign = BatteryIconDesign.VERTICAL_CAPSULE,
                                    showAppNotifications = true,
                                    notificationIcons = listOf("facebook", "whatsapp", "snapchat", "snapchat", "dot"),
                                    showNetworkSpeed = true,
                                    networkSpeedValue = "23.9",
                                    networkSpeedUnit = "KB/s",
                                    showVoNr = true,
                                    voNrText = "NR",
                                    showNetworkBadge = true,
                                    networkBadgeText = "5G++",
                                    showTrafficArrows = true,
                                    useTriangleSignal = true,
                                    isEmojiBattery = false,
                                    showBatteryPercentage = true,
                                    textColor = 0xFFFFFFFF,
                                    colorNormal = 0xFFFFFFFF,
                                    barBackgroundColor = 0x00000000,
                                    barAlpha = 0f
                                ),
                                simulation = simulation.copy(batteryLevel = 64, simulatedTime = "10:03")
                            )
                        }
                    }
                }
            }
        }

        // 4. Hero Carousel Banner: "Battery Emoji" (Screenshot 1)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFFDDE1),
                                    Color(0xFFEE9CA7),
                                    Color(0xFFE0C3FC),
                                    Color(0xFF8EC5FC)
                                )
                            )
                        )
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Battery Emoji",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black
                                ),
                                color = Color(0xFF4338CA)
                            )
                            Text(
                                text = "Cute mascots & live battery indicator",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF312E81)
                            )

                            // Cute illustration emojis
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(text = "🐰💕", fontSize = 24.sp)
                                Text(text = "🥕", fontSize = 20.sp)
                                Text(text = "🐹", fontSize = 24.sp)
                                Text(text = "✨", fontSize = 18.sp)
                            }
                        }

                        // Mascot in battery graphic
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.85f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🐰🔋", fontSize = 32.sp)
                                Text(
                                    text = "${simulation.batteryLevel}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEC4899)
                                )
                            }
                        }
                    }
                }
            }

            // Pager dots indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF6366F1))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCBD5E1))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCBD5E1))
                )
            }
        }

        // 5. Category Tabs Row: Football, New Year, Valentine, Christmas, Characters, Cute Animals, Anime, Hearts (Screenshot 1)
        item {
            ScrollableTabRow(
                selectedTabIndex = EmojiThemeCatalog.CATEGORIES.indexOf(selectedCategory).coerceAtLeast(0),
                edgePadding = 16.dp,
                divider = {},
                containerColor = Color.Transparent,
                indicator = { tabPositions ->
                    val index = EmojiThemeCatalog.CATEGORIES.indexOf(selectedCategory).coerceAtLeast(0)
                    if (index < tabPositions.size) {
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[index])
                                .height(3.dp)
                                .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                .background(Color(0xFF4F46E5))
                        )
                    }
                }
            ) {
                EmojiThemeCatalog.CATEGORIES.forEach { category ->
                    val isSelected = selectedCategory == category
                    Tab(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        text = {
                            Text(
                                text = category,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF1E1B4B) else Color(0xFF64748B),
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }
        }

        // 6. Grid of 3-column Emoji Battery Themes (Screenshot 1)
        val chunks = filteredThemes.chunked(3)
        chunks.forEach { rowThemes ->
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (theme in rowThemes) {
                        val isSelected = config.emojiThemeId == theme.id

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onConfigChange(
                                        config.copy(
                                            isEmojiBattery = true,
                                            emojiThemeId = theme.id,
                                            emojiMascot = theme.mascotEmoji,
                                            emojiSecondary = theme.secondaryEmoji,
                                            emojiBatteryFillColor = theme.fillColor,
                                            customSignalColor = theme.signalColor,
                                            statusEmojis = theme.defaultClockEmojis
                                        )
                                    )
                                }
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF4F46E5) else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(18.dp)
                                ),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFFF5F3FF) else Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (isSelected) 3.dp else 1.dp
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .padding(6.dp)
                            ) {
                                // Top-Left Crown VIP badge
                                if (theme.isVip) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFFEF08A),
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.TopStart)
                                    ) {
                                        Text(
                                            text = "👑",
                                            fontSize = 11.sp,
                                            modifier = Modifier.wrapContentSize(Alignment.Center)
                                        )
                                    }
                                }

                                // Top-Right Ad or Selected check badge
                                if (isSelected) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF4F46E5),
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.TopEnd)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White,
                                            modifier = Modifier.padding(3.dp)
                                        )
                                    }
                                } else if (theme.hasAdBadge) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFFFEF08A),
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    ) {
                                        Text(
                                            text = "Ad",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF854D0E),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }

                                // Central Mascot & Battery Graphic
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    EmojiThemeCardPreview(
                                        theme = theme,
                                        batteryLevel = simulation.batteryLevel
                                    )
                                }
                            }
                        }
                    }

                    // Fill remainder of row if less than 3
                    repeat(3 - rowThemes.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // 7. Quick Overlay Controls & Main Screen Testing
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Home Screen Mask & Position",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Background mask choices so native status bar is covered cleanly
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isSolidBlack = config.barAlpha >= 0.95f && config.barBackgroundColor == 0xFF000000
                        val isTransparent = config.barAlpha < 0.1f
                        val isTinted = config.barAlpha in 0.6f..0.94f

                        FilterChip(
                            selected = isSolidBlack,
                            onClick = {
                                onConfigChange(
                                    config.copy(
                                        barBackgroundColor = 0xFF000000,
                                        barAlpha = 1.0f
                                    )
                                )
                            },
                            label = { Text("AMOLED Black", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = isTinted,
                            onClick = {
                                onConfigChange(
                                    config.copy(
                                        barBackgroundColor = 0xFF000000,
                                        barAlpha = 0.85f
                                    )
                                )
                            },
                            label = { Text("Dark Tint", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = isTransparent,
                            onClick = {
                                onConfigChange(config.copy(barAlpha = 0.0f))
                            },
                            label = { Text("Transparent", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Height Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status Bar Height",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${config.statusBarHeight.toInt()} dp",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = config.statusBarHeight,
                        onValueChange = { onConfigChange(config.copy(statusBarHeight = it)) },
                        valueRange = 24f..52f,
                        steps = 13
                    )

                    // Go to Phone Main Screen button
                    if (isOverlayActive) {
                        Button(
                            onClick = {
                                val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                                    addCategory(Intent.CATEGORY_HOME)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(homeIntent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4F46E5)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Go to Phone Main Screen to View Live")
                        }
                    }
                }
            }
        }
    }

    // Permission Dialog / Bottom Sheet
    if (showPermissionSheet) {
        AlertDialog(
            onDismissRequest = { showPermissionSheet = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Display Over Other Apps",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "To show your custom emoji battery and status bar directly on your phone's home screen and over all apps, please grant the 'Display over other apps' permission in Android Settings.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionSheet = false
                        try {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val fallbackIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                            context.startActivity(fallbackIntent)
                        }
                    }
                ) {
                    Text("Open Android Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionSheet = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Help Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Text(
                    text = "How to Use Emoji Battery Themes",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("1. Turn on 'Enable emoji battery to begin' at the top.")
                    Text("2. When prompted, allow 'Display over other apps' in Android settings.")
                    Text("3. Choose your favorite theme from categories like Football, Cute Animals, Valentine, or Anime.")
                    Text("4. Tap 'Go to Phone Main Screen' to see your custom emoji battery floating live over your phone!")
                    Text("5. If your phone's native icons peek through, select 'AMOLED Black' under Home Screen Mask to cover them completely.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Got it!")
                }
            }
        )
    }

    // VIP Dialog
    if (showVipDialog) {
        AlertDialog(
            onDismissRequest = { showVipDialog = false },
            title = {
                Text(
                    text = "👑 All Emoji Themes Unlocked",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("All premium mascot battery themes, charging animations, and status emojis are completely free and unlocked for you!")
            },
            confirmButton = {
                Button(onClick = { showVipDialog = false }) {
                    Text("Awesome!")
                }
            }
        )
    }
}
