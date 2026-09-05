package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*

@Composable
fun CustomStatusBarView(
    config: StatusBarConfig,
    simulation: SimulationState,
    modifier: Modifier = Modifier
) {
    val barColor = Color(config.barBackgroundColor).copy(alpha = config.barAlpha)
    val textColor = Color(config.textColor)
    val iconColor = when (config.iconStyle) {
        StatusIconStyle.NEON -> Color(0xFF00FFA3)
        else -> textColor
    }

    val heightDp = config.statusBarHeight.dp
    val spacingDp = config.iconSpacing.dp
    val iconSizeDp = config.statusIconSize.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp)
            .background(barColor)
            .padding(horizontal = config.leftPadding.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Side: Typically Clock (or Clock + Notification badges)
            if (config.isClockOnLeft) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacingDp)
                ) {
                    if (config.showTime) {
                        FormattedClockText(
                            timeStr = simulation.simulatedTime,
                            is24Hour = config.timeFormat24Hour,
                            showSeconds = config.showTimeSeconds,
                            isOnePlus = config.deviceProfile == DeviceProfile.ONEPLUS,
                            textColor = textColor,
                            fontSizeSp = (config.statusIconSize * 0.9f)
                        )
                    }

                    // App Notification icons (Facebook, WhatsApp, Snapchat, Dot • from Screenshot or live notification collector)
                    val effectiveNotifications = if (simulation.useRealNetworkData && simulation.liveNotificationIcons.isNotEmpty()) {
                        simulation.liveNotificationIcons
                    } else {
                        config.notificationIcons
                    }

                    if (config.showAppNotifications && effectiveNotifications.isNotEmpty()) {
                        AppNotificationRow(
                            notificationIcons = effectiveNotifications,
                            tintColor = iconColor,
                            iconSizeDp = (iconSizeDp * 0.95f),
                            spacingDp = 5.dp
                        )
                    }

                    // Status emojis next to clock (e.g. 🔇 😎 🐱)
                    if (config.showStatusEmojis && config.statusEmojis.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            config.statusEmojis.forEach { emoji ->
                                Text(
                                    text = emoji,
                                    fontSize = (config.statusIconSize * 0.95f).sp
                                )
                            }
                        }
                    }

                    // Notification badges if enabled
                    if (config.showNotificationDots && simulation.notificationsCount > 0) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubble,
                                contentDescription = "Message",
                                tint = iconColor.copy(alpha = 0.85f),
                                modifier = Modifier.size((iconSizeDp * 0.75f))
                            )
                            if (simulation.notificationsCount > 1) {
                                Icon(
                                    imageVector = Icons.Default.Mail,
                                    contentDescription = "Mail",
                                    tint = iconColor.copy(alpha = 0.85f),
                                    modifier = Modifier.size((iconSizeDp * 0.75f))
                                )
                            }
                        }
                    }
                }
            } else {
                // Inverted layout: Left side has status indicators
                RightStatusCluster(
                    config = config,
                    simulation = simulation,
                    iconColor = iconColor,
                    textColor = textColor,
                    iconSizeDp = iconSizeDp,
                    spacingDp = spacingDp
                )
            }

            // Center clearance space for Center Notch if present in phone preview
            if (config.notchStyle == NotchStyle.CENTER_HOLE_PUNCH || config.notchStyle == NotchStyle.PILL_ISLAND) {
                Spacer(modifier = Modifier.width(36.dp))
            }

            // Right Side
            if (config.isClockOnLeft) {
                RightStatusCluster(
                    config = config,
                    simulation = simulation,
                    iconColor = iconColor,
                    textColor = textColor,
                    iconSizeDp = iconSizeDp,
                    spacingDp = spacingDp
                )
            } else {
                // Clock on right
                if (config.showTime) {
                    FormattedClockText(
                        timeStr = simulation.simulatedTime,
                        is24Hour = config.timeFormat24Hour,
                        showSeconds = config.showTimeSeconds,
                        isOnePlus = config.deviceProfile == DeviceProfile.ONEPLUS,
                        textColor = textColor,
                        fontSizeSp = (config.statusIconSize * 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RightStatusCluster(
    config: StatusBarConfig,
    simulation: SimulationState,
    iconColor: Color,
    textColor: Color,
    iconSizeDp: androidx.compose.ui.unit.Dp,
    spacingDp: androidx.compose.ui.unit.Dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacingDp)
    ) {
        // VPN
        if (config.showVpn && simulation.isVpnOn) {
            Icon(
                imageVector = Icons.Default.VpnKey,
                contentDescription = "VPN",
                tint = iconColor,
                modifier = Modifier.size((iconSizeDp * 0.8f))
            )
        }

        // Location
        if (config.showLocation && simulation.isLocationOn) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = iconColor,
                modifier = Modifier.size((iconSizeDp * 0.85f))
            )
        }

        // Do Not Disturb
        if (config.showDnd && simulation.isDndOn) {
            Icon(
                imageVector = Icons.Default.DoNotDisturbOn,
                contentDescription = "DND",
                tint = iconColor,
                modifier = Modifier.size((iconSizeDp * 0.85f))
            )
        }

        // Alarm
        if (config.showAlarm) {
            Icon(
                imageVector = Icons.Default.Alarm,
                contentDescription = "Alarm",
                tint = iconColor,
                modifier = Modifier.size((iconSizeDp * 0.85f))
            )
        }

        // Bluetooth
        if (config.showBluetooth && simulation.isBluetoothOn) {
            Icon(
                imageVector = Icons.Default.Bluetooth,
                contentDescription = "Bluetooth",
                tint = iconColor,
                modifier = Modifier.size((iconSizeDp * 0.85f))
            )
        }

        // Wi-Fi
        if (config.showWifi && simulation.isWifiOn) {
            val wifiIcon = when {
                simulation.wifiStrength >= 4 -> Icons.Default.Wifi
                simulation.wifiStrength == 3 -> Icons.Default.Wifi
                simulation.wifiStrength == 2 -> Icons.Default.Wifi2Bar
                else -> Icons.Default.Wifi1Bar
            }
            Icon(
                imageVector = wifiIcon,
                contentDescription = "Wi-Fi",
                tint = iconColor,
                modifier = Modifier.size(iconSizeDp)
            )
        }

        // Network Speed Meter (e.g. 23.9 KB/s stacked) from Screenshot or Real TrafficStats
        if (config.showNetworkSpeed) {
            val speedVal = if (simulation.useRealNetworkData) simulation.liveNetworkSpeed else config.networkSpeedValue
            val speedUnit = if (simulation.useRealNetworkData) simulation.liveNetworkUnit else config.networkSpeedUnit
            NetworkSpeedMeter(
                speedValue = speedVal,
                speedUnit = speedUnit,
                textColor = textColor,
                fontSizeSp = iconSizeDp.value
            )
        }

        // VoNR / VoLTE indicator (Vo with wave arcs, NR/LTE below) from Screenshot or Real Telephony
        if (config.showVoNr) {
            val voText = if (simulation.useRealNetworkData) simulation.liveVoBadge else config.voNrText
            VoNrIndicator(
                text = voText,
                textColor = textColor,
                fontSizeSp = iconSizeDp.value
            )
        }

        // 5G++ Network Badge with Traffic Activity Arrows from Screenshot or Real Telephony
        if (config.showNetworkBadge) {
            val badgeText = if (simulation.useRealNetworkData) simulation.liveNetworkBadge else config.networkBadgeText
            val hasArrows = if (simulation.useRealNetworkData) (config.showTrafficArrows && simulation.liveIsDataTransferring) else config.showTrafficArrows
            NetworkTrafficBadge(
                badgeText = badgeText,
                showArrows = hasArrows,
                textColor = textColor,
                fontSizeSp = iconSizeDp.value
            )
        }

        // Cellular / Mobile Signal
        if (config.showMobileSignal) {
            val signalTintColor = if (config.isEmojiBattery) Color(config.customSignalColor) else iconColor
            if (config.useTriangleSignal) {
                // Solid triangle cellular slope matching Motorola / modern Android from Screenshot
                TriangleCellularSignal(
                    strength = simulation.signalStrength,
                    activeColor = signalTintColor,
                    inactiveColor = signalTintColor.copy(alpha = 0.35f),
                    sizeDp = iconSizeDp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    val signalIcon = when {
                        simulation.signalStrength >= 4 -> Icons.Default.SignalCellular4Bar
                        simulation.signalStrength == 3 -> Icons.Default.NetworkCell
                        simulation.signalStrength == 2 -> Icons.Default.SignalCellularAlt
                        else -> Icons.Default.SignalCellularNull
                    }
                    Icon(
                        imageVector = signalIcon,
                        contentDescription = "Mobile Signal",
                        tint = signalTintColor,
                        modifier = Modifier.size(iconSizeDp)
                    )

                    if (config.customNetworkType.isNotEmpty()) {
                        Text(
                            text = config.customNetworkType,
                            color = signalTintColor,
                            fontSize = (iconSizeDp.value * 0.65f).sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Percentage text before battery in Emoji Battery mode (matching Screenshot 2: 📶 3G 66% 🐹🔋)
        if (config.isEmojiBattery && config.showBatteryPercentage) {
            Text(
                text = "${simulation.batteryLevel}%",
                color = textColor,
                fontSize = (iconSizeDp.value * 0.72f).sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Battery Icon & Mascot
        if (config.showBattery) {
            BatteryIconView(
                config = config,
                batteryLevel = simulation.batteryLevel,
                isCharging = simulation.isCharging
            )
        }
    }
}

@Composable
private fun FormattedClockText(
    timeStr: String,
    is24Hour: Boolean,
    showSeconds: Boolean,
    isOnePlus: Boolean,
    textColor: Color,
    fontSizeSp: Float
) {
    val displayTime = if (showSeconds) "$timeStr:42" else timeStr

    if (isOnePlus) {
        // OnePlus signature Red '1' accent
        val annotated = buildAnnotatedString {
            displayTime.forEach { char ->
                if (char == '1') {
                    withStyle(style = SpanStyle(color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)) {
                        append(char)
                    }
                } else {
                    withStyle(style = SpanStyle(color = textColor, fontWeight = FontWeight.Medium)) {
                        append(char)
                    }
                }
            }
        }
        Text(
            text = annotated,
            fontSize = fontSizeSp.sp,
            fontWeight = FontWeight.Medium
        )
    } else {
        Text(
            text = displayTime,
            color = textColor,
            fontSize = fontSizeSp.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
