package com.example.ui.screens

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.CustomStatusBarView

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BatteryPlusScreen(
    config: StatusBarConfig,
    simulation: SimulationState,
    onConfigChange: (StatusBarConfig) -> Unit,
    onSimulationChange: (SimulationState) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusEmojiPresets = listOf(
        "Default (Screenshot 2)" to listOf("🔇", "😎", "🐱"),
        "Romantic Hearts" to listOf("💌", "💖", "🌸"),
        "Gamer Ace" to listOf("🎮", "🔥", "⚡"),
        "World Champion" to listOf("⚽", "🏆", "🥇"),
        "Kawaii Pets" to listOf("🐰", "🐹", "🐾"),
        "Night Owl" to listOf("🌙", "✨", "☕")
    )

    val availableEmojis = listOf(
        "🔇", "😎", "🐱", "🐰", "🐹", "🐶", "🐼", "🦊",
        "💖", "💕", "💌", "🌹", "🌸", "🍓", "🧸", "🎀",
        "⚽", "🏆", "⚡", "🔥", "🚀", "👟", "🎮", "🥇",
        "🔔", "🌙", "✈️", "🔋", "📶", "⭐", "🎉", "☃️"
    )

    val signalColors = listOf(
        "Coral Pink" to 0xFFFF4B6E,
        "Cyan Blue" to 0xFF38BDF8,
        "Purple Glow" to 0xFFA855F7,
        "Emerald Green" to 0xFF10B981,
        "Amber Gold" to 0xFFF59E0B,
        "Pure White" to 0xFFFFFFFF
    )

    val networkTypes = listOf("3G", "4G", "5G", "LTE", "Wi-Fi", "")

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Title
        item {
            Column {
                Text(
                    text = "Battery Plus",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Custom status emojis, charging effects & signal accents",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Live preview bar
        item {
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
        }

        // Section 1: Status Emojis Next to Clock (09:51 🔇 😎 🐱)
        item {
            Card(
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
                        Column {
                            Text(
                                text = "Clock Status Emojis",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Displays next to time (e.g. 09:51 🔇 😎 🐱)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = config.showStatusEmojis,
                            onCheckedChange = { onConfigChange(config.copy(showStatusEmojis = it)) }
                        )
                    }

                    if (config.showStatusEmojis) {
                        // Current active emojis
                        Text(
                            text = "Current Emojis Sequence:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            config.statusEmojis.forEachIndexed { index, emoji ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF3E8FF),
                                    modifier = Modifier.clickable {
                                        val updated = config.statusEmojis.toMutableList()
                                        updated.removeAt(index)
                                        onConfigChange(config.copy(statusEmojis = updated))
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = emoji, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = Color(0xFF6B21A8),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            if (config.statusEmojis.size < 5) {
                                Text(
                                    text = "(Tap below to add)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Presets row
                        Text(
                            text = "Quick Presets:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(statusEmojiPresets) { (name, emojis) ->
                                FilterChip(
                                    selected = config.statusEmojis == emojis,
                                    onClick = { onConfigChange(config.copy(statusEmojis = emojis)) },
                                    label = { Text(name, fontSize = 12.sp) }
                                )
                            }
                        }

                        // Available emoji palette
                        Text(
                            text = "Pick Emojis:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableEmojis.forEach { emoji ->
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable {
                                            if (config.statusEmojis.size < 5) {
                                                onConfigChange(
                                                    config.copy(statusEmojis = config.statusEmojis + emoji)
                                                )
                                            }
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = emoji, fontSize = 20.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Cellular Signal & Network Label (Screenshot 2: 📶 3G in coral pink)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Network & Signal Customization",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Network Type Label (3G, 4G, 5G, LTE)
                    Text(
                        text = "Network Indicator Badge:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        networkTypes.forEach { netType ->
                            val label = if (netType.isEmpty()) "None" else netType
                            FilterChip(
                                selected = config.customNetworkType == netType,
                                onClick = { onConfigChange(config.copy(customNetworkType = netType)) },
                                label = { Text(label, fontSize = 12.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Signal Bar Color
                    Text(
                        text = "Signal Bar Color:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        signalColors.forEach { (name, colorVal) ->
                            val isSelected = config.customSignalColor == colorVal
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(colorVal))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onConfigChange(config.copy(customSignalColor = colorVal))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (colorVal == 0xFFFFFFFF) Color.Black else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Live Battery Simulation Controls
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Battery Testing Controls",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Battery Level Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Battery Level", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${simulation.batteryLevel}%",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = simulation.batteryLevel.toFloat(),
                        onValueChange = { onSimulationChange(simulation.copy(batteryLevel = it.toInt())) },
                        valueRange = 0f..100f
                    )

                    // Charging Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Simulate Charging (Bolt & Sparkles)", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = simulation.isCharging,
                            onCheckedChange = { onSimulationChange(simulation.copy(isCharging = it)) }
                        )
                    }
                }
            }
        }
    }
}
