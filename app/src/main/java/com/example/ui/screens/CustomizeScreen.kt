package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.ColorPickerRow
import com.example.ui.components.PhonePreviewCard

@Composable
fun CustomizeScreen(
    config: StatusBarConfig,
    simulation: SimulationState,
    onConfigChange: (StatusBarConfig) -> Unit,
    onSimulationChange: (SimulationState) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Battery", "Icons & Clock", "Appearance")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("customize_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sticky Live Preview Header
        item {
            PhonePreviewCard(
                config = config,
                simulation = simulation,
                onSimulationChange = onSimulationChange,
                showControls = true
            )
        }

        // Customization Category Tab Selector
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("customize_tab_row")
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.BatteryChargingFull
                                    1 -> Icons.Default.Widgets
                                    else -> Icons.Default.Palette
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
        }

        // Content based on selected tab
        when (selectedTab) {
            0 -> {
                // BATTERY CUSTOMIZER TAB
                item {
                    BatteryCustomizerSection(
                        config = config,
                        onConfigChange = onConfigChange
                    )
                }
            }
            1 -> {
                // STATUS BAR & ICONS TAB
                item {
                    StatusBarIconsSection(
                        config = config,
                        onConfigChange = onConfigChange
                    )
                }
            }
            2 -> {
                // APPEARANCE & LAYOUT TAB
                item {
                    AppearanceSection(
                        config = config,
                        onConfigChange = onConfigChange
                    )
                }
            }
        }
    }
}

@Composable
private fun BatteryCustomizerSection(
    config: StatusBarConfig,
    onConfigChange: (StatusBarConfig) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Battery Icon Design
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Battery Icon Design",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BatteryIconDesign.values().forEach { design ->
                        val isSelected = config.batteryDesign == design
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onConfigChange(config.copy(batteryDesign = design)) }
                                .testTag("design_item_${design.name}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = design.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = design.description,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onConfigChange(config.copy(batteryDesign = design)) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Fill Style & Percentage Visibility
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Battery Fill Style",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BatteryFillStyle.values().forEach { style ->
                        val isSelected = config.batteryFillStyle == style
                        FilterChip(
                            selected = isSelected,
                            onClick = { onConfigChange(config.copy(batteryFillStyle = style)) },
                            label = { Text(style.displayName, fontSize = 12.sp) },
                            modifier = Modifier.testTag("fill_chip_${style.name}")
                        )
                    }
                }

                HorizontalDivider()

                Text(
                    text = "Percentage Visibility",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BatteryPercentageVisibility.values().forEach { visibility ->
                        val isSelected = config.percentageVisibility == visibility
                        FilterChip(
                            selected = isSelected,
                            onClick = { onConfigChange(config.copy(percentageVisibility = visibility)) },
                            label = { Text(visibility.displayName, fontSize = 11.sp) },
                            modifier = Modifier.testTag("visibility_chip_${visibility.name}")
                        )
                    }
                }

                HorizontalDivider()

                Text(
                    text = "Charging Animation Effect",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ChargingAnimationStyle.values().forEach { anim ->
                        val isSelected = config.chargingAnimation == anim
                        FilterChip(
                            selected = isSelected,
                            onClick = { onConfigChange(config.copy(chargingAnimation = anim)) },
                            label = { Text(anim.displayName, fontSize = 12.sp) },
                            modifier = Modifier.testTag("anim_chip_${anim.name}")
                        )
                    }
                }
            }
        }

        // Geometry: Icon Size & Thickness
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Dimensions & Stroke",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Icon Size", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "${config.iconSize.toInt()} dp",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = config.iconSize,
                        onValueChange = { onConfigChange(config.copy(iconSize = it)) },
                        valueRange = 14f..32f,
                        modifier = Modifier.testTag("battery_size_slider")
                    )
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Stroke / Thickness", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = String.format("%.1f dp", config.iconThickness),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = config.iconThickness,
                        onValueChange = { onConfigChange(config.copy(iconThickness = it)) },
                        valueRange = 1.0f..4.0f,
                        modifier = Modifier.testTag("battery_thickness_slider")
                    )
                }
            }
        }

        // Battery Colors by Charge Level
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Battery Colors by Charge Level",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                ColorPickerRow(
                    title = "Normal Level (20% - 80%)",
                    selectedColor = config.colorNormal,
                    onColorSelected = { onConfigChange(config.copy(colorNormal = it)) }
                )

                ColorPickerRow(
                    title = "Low Battery (<20%)",
                    selectedColor = config.colorLow,
                    onColorSelected = { onConfigChange(config.copy(colorLow = it)) }
                )

                ColorPickerRow(
                    title = "Full Battery (>80%)",
                    selectedColor = config.colorFull,
                    onColorSelected = { onConfigChange(config.copy(colorFull = it)) }
                )

                ColorPickerRow(
                    title = "While Charging",
                    selectedColor = config.colorCharging,
                    onColorSelected = { onConfigChange(config.copy(colorCharging = it)) }
                )
            }
        }
    }
}

@Composable
private fun StatusBarIconsSection(
    config: StatusBarConfig,
    onConfigChange: (StatusBarConfig) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Icon Visibility Toggles
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Indicators & Elements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IndicatorToggleItem(
                    title = "Battery Icon",
                    subtitle = "Main battery meter",
                    icon = Icons.Default.BatteryFull,
                    checked = config.showBattery,
                    onCheckedChange = { onConfigChange(config.copy(showBattery = it)) }
                )

                IndicatorToggleItem(
                    title = "Clock / Time",
                    subtitle = "Status bar digital clock",
                    icon = Icons.Default.Schedule,
                    checked = config.showTime,
                    onCheckedChange = { onConfigChange(config.copy(showTime = it)) }
                )

                IndicatorToggleItem(
                    title = "Wi-Fi Signal",
                    subtitle = "Wireless connection meter",
                    icon = Icons.Default.Wifi,
                    checked = config.showWifi,
                    onCheckedChange = { onConfigChange(config.copy(showWifi = it)) }
                )

                IndicatorToggleItem(
                    title = "Mobile Signal",
                    subtitle = "Cellular signal & 5G/4G",
                    icon = Icons.Default.SignalCellular4Bar,
                    checked = config.showMobileSignal,
                    onCheckedChange = { onConfigChange(config.copy(showMobileSignal = it)) }
                )

                IndicatorToggleItem(
                    title = "Bluetooth",
                    subtitle = "Bluetooth connection status",
                    icon = Icons.Default.Bluetooth,
                    checked = config.showBluetooth,
                    onCheckedChange = { onConfigChange(config.copy(showBluetooth = it)) }
                )

                IndicatorToggleItem(
                    title = "Alarm Icon",
                    subtitle = "Scheduled active alarms",
                    icon = Icons.Default.Alarm,
                    checked = config.showAlarm,
                    onCheckedChange = { onConfigChange(config.copy(showAlarm = it)) }
                )

                IndicatorToggleItem(
                    title = "Notification Indicators",
                    subtitle = "Message and email dots",
                    icon = Icons.Default.Notifications,
                    checked = config.showNotificationDots,
                    onCheckedChange = { onConfigChange(config.copy(showNotificationDots = it)) }
                )

                IndicatorToggleItem(
                    title = "Do Not Disturb",
                    subtitle = "Silent / DND indicator",
                    icon = Icons.Default.DoNotDisturbOn,
                    checked = config.showDnd,
                    onCheckedChange = { onConfigChange(config.copy(showDnd = it)) }
                )

                IndicatorToggleItem(
                    title = "Location",
                    subtitle = "GPS active indicator",
                    icon = Icons.Default.LocationOn,
                    checked = config.showLocation,
                    onCheckedChange = { onConfigChange(config.copy(showLocation = it)) }
                )

                IndicatorToggleItem(
                    title = "VPN",
                    subtitle = "Active VPN tunnel key",
                    icon = Icons.Default.VpnKey,
                    checked = config.showVpn,
                    onCheckedChange = { onConfigChange(config.copy(showVpn = it)) }
                )
            }
        }

        // App Notifications Customization (Facebook, WhatsApp, Snapchat, Dot •)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "App Notification Icons",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Icons next to clock: Facebook, WhatsApp, Snapchat, etc.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = config.showAppNotifications,
                        onCheckedChange = { onConfigChange(config.copy(showAppNotifications = it)) }
                    )
                }

                if (config.showAppNotifications) {
                    // Preview of current notification row
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "10:03",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            com.example.ui.components.AppNotificationRow(
                                notificationIcons = config.notificationIcons,
                                tintColor = Color.White,
                                iconSizeDp = 15.dp,
                                spacingDp = 5.dp
                            )
                        }
                    }

                    // Chips to remove currently active icons
                    Text(
                        text = "Active Notification Queue (Tap ✕ to remove):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        config.notificationIcons.forEachIndexed { index, iconId ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.clickable {
                                    val updated = config.notificationIcons.toMutableList()
                                    if (index in updated.indices) {
                                        updated.removeAt(index)
                                        onConfigChange(config.copy(notificationIcons = updated))
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = iconId.capitalize(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = "✕", fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    // Quick Add buttons
                    Text(
                        text = "Add Notification Icon:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    val availableApps = listOf(
                        "facebook" to "Facebook",
                        "whatsapp" to "WhatsApp",
                        "snapchat" to "Snapchat",
                        "dot" to "Dot •",
                        "instagram" to "Instagram",
                        "messenger" to "Messenger",
                        "gmail" to "Gmail",
                        "telegram" to "Telegram",
                        "twitter" to "X / Twitter",
                        "youtube" to "YouTube"
                    )
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(availableApps.size) { i ->
                            val (appId, label) = availableApps[i]
                            OutlinedButton(
                                onClick = {
                                    val updated = config.notificationIcons.toMutableList()
                                    updated.add(appId)
                                    onConfigChange(config.copy(notificationIcons = updated))
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = "+ $label", fontSize = 11.sp)
                            }
                        }
                    }

                    // Reset button
                    Button(
                        onClick = {
                            onConfigChange(
                                config.copy(
                                    notificationIcons = listOf("facebook", "whatsapp", "snapchat", "snapchat", "dot")
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Text(
                            text = "Reset to Screenshot (FB, WA, 2x Snapchat, Dot)",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Network Speed, VoNR & 5G++ Indicators
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "5G++, VoNR & Network Speed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IndicatorToggleItem(
                    title = "Network Speed Meter",
                    subtitle = "Stacked speed meter e.g. ${config.networkSpeedValue} ${config.networkSpeedUnit}",
                    icon = Icons.Default.Speed,
                    checked = config.showNetworkSpeed,
                    onCheckedChange = { onConfigChange(config.copy(showNetworkSpeed = it)) }
                )

                if (config.showNetworkSpeed) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = config.networkSpeedValue,
                            onValueChange = { onConfigChange(config.copy(networkSpeedValue = it)) },
                            label = { Text("Speed Value") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = config.networkSpeedUnit,
                            onValueChange = { onConfigChange(config.copy(networkSpeedUnit = it)) },
                            label = { Text("Unit") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                IndicatorToggleItem(
                    title = "VoNR / VoLTE",
                    subtitle = "Voice over NR / LTE indicator with radio waves",
                    icon = Icons.Default.Call,
                    checked = config.showVoNr,
                    onCheckedChange = { onConfigChange(config.copy(showVoNr = it)) }
                )

                IndicatorToggleItem(
                    title = "5G++ Network Badge",
                    subtitle = "High-speed 5G++ / 5G+ network badge",
                    icon = Icons.Default.NetworkCheck,
                    checked = config.showNetworkBadge,
                    onCheckedChange = { onConfigChange(config.copy(showNetworkBadge = it)) }
                )

                if (config.showNetworkBadge) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Data Activity Arrows (↑↓ / ⇅)", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = config.showTrafficArrows,
                            onCheckedChange = { onConfigChange(config.copy(showTrafficArrows = it)) }
                        )
                    }
                }

                IndicatorToggleItem(
                    title = "Solid Triangle Cellular Signal",
                    subtitle = "Motorola / modern Android right triangle wedge",
                    icon = Icons.Default.SignalCellular4Bar,
                    checked = config.useTriangleSignal,
                    onCheckedChange = { onConfigChange(config.copy(useTriangleSignal = it)) }
                )
            }
        }

        // Clock & Alignment Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Clock & Alignment",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "24-Hour Format", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "e.g. 14:30 instead of 2:30", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = config.timeFormat24Hour,
                        onCheckedChange = { onConfigChange(config.copy(timeFormat24Hour = it)) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Show Seconds", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Adds seconds counter (:42)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = config.showTimeSeconds,
                        onCheckedChange = { onConfigChange(config.copy(showTimeSeconds = it)) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Clock on Left", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Standard Android left-hand clock", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = config.isClockOnLeft,
                        onCheckedChange = { onConfigChange(config.copy(isClockOnLeft = it)) }
                    )
                }
            }
        }

        // Icon Style & Spacing
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Icon Style & Density",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusIconStyle.values().forEach { style ->
                        val isSelected = config.iconStyle == style
                        FilterChip(
                            selected = isSelected,
                            onClick = { onConfigChange(config.copy(iconStyle = style)) },
                            label = { Text(style.displayName, fontSize = 12.sp) }
                        )
                    }
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Status Icon Size", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${config.statusIconSize.toInt()} dp", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = config.statusIconSize,
                        onValueChange = { onConfigChange(config.copy(statusIconSize = it)) },
                        valueRange = 12f..24f
                    )
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Icon Spacing", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${config.iconSpacing.toInt()} dp", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = config.iconSpacing,
                        onValueChange = { onConfigChange(config.copy(iconSpacing = it)) },
                        valueRange = 2f..16f
                    )
                }
            }
        }
    }
}

@Composable
private fun AppearanceSection(
    config: StatusBarConfig,
    onConfigChange: (StatusBarConfig) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Height & Padding
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Geometry & Dimensions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Status Bar Height", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${config.statusBarHeight.toInt()} dp", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = config.statusBarHeight,
                        onValueChange = { onConfigChange(config.copy(statusBarHeight = it)) },
                        valueRange = 24f..48f
                    )
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Left Margin / Inset", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${config.leftPadding.toInt()} dp", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = config.leftPadding,
                        onValueChange = { onConfigChange(config.copy(leftPadding = it)) },
                        valueRange = 8f..36f
                    )
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Right Margin / Inset", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${config.rightPadding.toInt()} dp", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = config.rightPadding,
                        onValueChange = { onConfigChange(config.copy(rightPadding = it)) },
                        valueRange = 8f..36f
                    )
                }
            }
        }

        // Colors & Background Alpha
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Colors & Transparency",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                ColorPickerRow(
                    title = "Text & Icon Color",
                    selectedColor = config.textColor,
                    onColorSelected = { onConfigChange(config.copy(textColor = it)) }
                )

                ColorPickerRow(
                    title = "Bar Background Color",
                    selectedColor = config.barBackgroundColor,
                    onColorSelected = { onConfigChange(config.copy(barBackgroundColor = it)) }
                )

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Background Opacity / Alpha", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "${(config.barAlpha * 100).toInt()}%",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = config.barAlpha,
                        onValueChange = { onConfigChange(config.copy(barAlpha = it)) },
                        valueRange = 0f..1f
                    )
                }
            }
        }

        // Notch & Hardware Cutout Simulation
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Notch & Screen Cutout",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    NotchStyle.values().forEach { notch ->
                        val isSelected = config.notchStyle == notch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onConfigChange(config.copy(notchStyle = notch)) }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = notch.displayName, style = MaterialTheme.typography.bodyMedium)
                            RadioButton(
                                selected = isSelected,
                                onClick = { onConfigChange(config.copy(notchStyle = notch)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IndicatorToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
