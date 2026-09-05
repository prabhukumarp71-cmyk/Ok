package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*

@Composable
fun PhonePreviewCard(
    config: StatusBarConfig,
    simulation: SimulationState,
    onSimulationChange: (SimulationState) -> Unit,
    modifier: Modifier = Modifier,
    showControls: Boolean = true
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("phone_preview_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Device Profile Tag & Dark/Light Preview Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.testTag("device_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = config.deviceProfile.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Text(
                        text = "Live Simulation",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Dark/Light wallpaper preview toggle
                IconButton(
                    onClick = {
                        onSimulationChange(simulation.copy(isDarkModePreview = !simulation.isDarkModePreview))
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("dark_light_preview_toggle")
                ) {
                    Icon(
                        imageVector = if (simulation.isDarkModePreview) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = "Toggle Dark/Light Wallpaper",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Realistic Simulated Phone Frame with Wallpaper
            val wallpaperBrush = if (simulation.isDarkModePreview) {
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Midnight slate
                        Color(0xFF1E1B4B), // Indigo dark
                        Color(0xFF0F2027)  // Deep cyber teal
                    )
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE0E7FF), // Soft sky lavender
                        Color(0xFFF1F5F9), // Pearl light
                        Color(0xFFBAE6FD)  // Fresh sky blue
                    )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(wallpaperBrush)
                    .testTag("simulated_screen_frame"),
                contentAlignment = Alignment.TopCenter
            ) {
                // Background subtle app icons or widget aesthetic in simulated phone
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = (config.statusBarHeight + 16).dp, start = 20.dp, end = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = simulation.simulatedTime,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (simulation.isDarkModePreview) Color.White.copy(alpha = 0.9f) else Color(0xFF1E293B)
                    )
                    Text(
                        text = "Friday, Sep 4",
                        fontSize = 12.sp,
                        color = if (simulation.isDarkModePreview) Color.White.copy(alpha = 0.65f) else Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mini simulated app dock icons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(4) { idx ->
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when (idx) {
                                            0 -> Color(0xFF3B82F6).copy(alpha = 0.7f)
                                            1 -> Color(0xFF10B981).copy(alpha = 0.7f)
                                            2 -> Color(0xFFF59E0B).copy(alpha = 0.7f)
                                            else -> Color(0xFF8B5CF6).copy(alpha = 0.7f)
                                        }
                                    )
                            )
                        }
                    }
                }

                // Status Bar View overlaid at the exact top
                CustomStatusBarView(
                    config = config,
                    simulation = simulation,
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                // Notch simulation overlaid at the top center/left
                when (config.notchStyle) {
                    NotchStyle.CENTER_HOLE_PUNCH -> {
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .size(11.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF000000))
                                .align(Alignment.TopCenter)
                        )
                    }
                    NotchStyle.LEFT_HOLE_PUNCH -> {
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp, start = 22.dp)
                                .size(11.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF000000))
                                .align(Alignment.TopStart)
                        )
                    }
                    NotchStyle.PILL_ISLAND -> {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(width = 46.dp, height = 15.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF000000))
                                .align(Alignment.TopCenter)
                        )
                    }
                    NotchStyle.WATERDROP -> {
                        Box(
                            modifier = Modifier
                                .size(width = 24.dp, height = 14.dp)
                                .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                                .background(Color(0xFF000000))
                                .align(Alignment.TopCenter)
                        )
                    }
                    NotchStyle.NONE -> { /* No cutout */ }
                }
            }

            // Interactive simulation controls if enabled
            if (showControls) {
                Spacer(modifier = Modifier.height(14.dp))

                // Battery Slider row
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (simulation.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Battery Level: ${simulation.batteryLevel}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Charging switch
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Charging",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Switch(
                                checked = simulation.isCharging,
                                onCheckedChange = { onSimulationChange(simulation.copy(isCharging = it)) },
                                modifier = Modifier.testTag("charging_toggle")
                            )
                        }
                    }

                    Slider(
                        value = simulation.batteryLevel.toFloat(),
                        onValueChange = { onSimulationChange(simulation.copy(batteryLevel = it.toInt())) },
                        valueRange = 0f..100f,
                        steps = 19,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("battery_slider")
                    )
                }

                // Quick Toggles: Wi-Fi, Bluetooth, Notifications
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = simulation.isWifiOn,
                        onClick = { onSimulationChange(simulation.copy(isWifiOn = !simulation.isWifiOn)) },
                        label = { Text("Wi-Fi", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (simulation.isWifiOn) Icons.Default.Wifi else Icons.Default.WifiOff,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        modifier = Modifier.testTag("wifi_toggle")
                    )

                    FilterChip(
                        selected = simulation.isBluetoothOn,
                        onClick = { onSimulationChange(simulation.copy(isBluetoothOn = !simulation.isBluetoothOn)) },
                        label = { Text("BT", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (simulation.isBluetoothOn) Icons.Default.Bluetooth else Icons.Default.BluetoothDisabled,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        modifier = Modifier.testTag("bluetooth_toggle")
                    )

                    FilterChip(
                        selected = simulation.notificationsCount > 0,
                        onClick = {
                            val newCount = if (simulation.notificationsCount > 0) 0 else 3
                            onSimulationChange(simulation.copy(notificationsCount = newCount))
                        },
                        label = { Text("Alerts (${simulation.notificationsCount})", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        modifier = Modifier.testTag("notifications_toggle")
                    )
                }
            }
        }
    }
}
