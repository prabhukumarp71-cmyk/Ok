package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun BatteryIconView(
    config: StatusBarConfig,
    batteryLevel: Int,
    isCharging: Boolean,
    modifier: Modifier = Modifier
) {
    val levelClamped = batteryLevel.coerceIn(0, 100)

    // Determine current active color
    val activeColor = when {
        isCharging -> Color(config.colorCharging)
        config.batteryDesign == BatteryIconDesign.DYNAMIC_LEVEL -> {
            when {
                levelClamped <= 20 -> Color(config.colorLow)
                levelClamped <= 70 -> Color(config.colorNormal)
                else -> Color(config.colorFull)
            }
        }
        levelClamped <= 20 -> Color(config.colorLow)
        levelClamped >= 90 -> Color(config.colorFull)
        else -> Color(config.colorNormal)
    }

    // Determine percentage visibility
    val shouldShowPercentage = when (config.percentageVisibility) {
        BatteryPercentageVisibility.ALWAYS -> true
        BatteryPercentageVisibility.HIDE -> false
        BatteryPercentageVisibility.LOW_ONLY -> levelClamped <= 20
        BatteryPercentageVisibility.CHARGING_ONLY -> isCharging
    }

    // Animation values
    val infiniteTransition = rememberInfiniteTransition(label = "battery_anim")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    val sweepProgress by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_progress"
    )

    val effectiveColor = if (isCharging && config.chargingAnimation == ChargingAnimationStyle.PULSE) {
        activeColor.copy(alpha = pulseAlpha)
    } else {
        activeColor
    }

    if (config.isEmojiBattery) {
        EmojiBatteryIndicator(
            mascotEmoji = config.emojiMascot,
            secondaryEmoji = config.emojiSecondary,
            level = levelClamped,
            isCharging = isCharging,
            fillColor = Color(config.emojiBatteryFillColor),
            textColor = Color(config.textColor),
            sizeDp = config.iconSize,
            thicknessDp = config.iconThickness,
            showPercentage = shouldShowPercentage,
            modifier = modifier
        )
        return
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        // Percentage Outside (Left of icon option)
        if (shouldShowPercentage && config.batteryDesign == BatteryIconDesign.PERCENT_OUTSIDE) {
            Text(
                text = "$levelClamped%",
                color = Color(config.textColor),
                fontSize = (config.iconSize * 0.55f).sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Render appropriate battery graphic based on design
        when (config.batteryDesign) {
            BatteryIconDesign.CIRCLE -> {
                CircleBatteryGauge(
                    level = levelClamped,
                    isCharging = isCharging,
                    activeColor = effectiveColor,
                    textColor = Color(config.textColor),
                    showPercentage = shouldShowPercentage,
                    sizeDp = config.iconSize,
                    thicknessDp = config.iconThickness,
                    fillStyle = config.batteryFillStyle,
                    sweepProgress = sweepProgress
                )
            }
            BatteryIconDesign.MINIMAL -> {
                MinimalBatteryBar(
                    level = levelClamped,
                    isCharging = isCharging,
                    activeColor = effectiveColor,
                    sizeDp = config.iconSize,
                    thicknessDp = config.iconThickness,
                    sweepProgress = sweepProgress
                )
            }
            BatteryIconDesign.PERCENT_INSIDE -> {
                InsidePercentBattery(
                    level = levelClamped,
                    isCharging = isCharging,
                    activeColor = effectiveColor,
                    textColor = Color(config.textColor),
                    sizeDp = config.iconSize,
                    thicknessDp = config.iconThickness,
                    fillStyle = config.batteryFillStyle,
                    sweepProgress = sweepProgress
                )
            }
            BatteryIconDesign.VERTICAL_CAPSULE -> {
                VerticalBatteryIcon(
                    level = levelClamped,
                    isCharging = isCharging,
                    activeColor = effectiveColor,
                    textColor = Color(config.textColor),
                    heightDp = config.iconSize.dp,
                    strokeDp = config.iconThickness.dp
                )
            }
            else -> {
                // Classic, Rounded, Dynamic Level, Charging Animation
                HorizontalBatteryIcon(
                    design = config.batteryDesign,
                    level = levelClamped,
                    isCharging = isCharging,
                    activeColor = effectiveColor,
                    textColor = Color(config.textColor),
                    sizeDp = config.iconSize,
                    thicknessDp = config.iconThickness,
                    fillStyle = config.batteryFillStyle,
                    chargingAnimation = config.chargingAnimation,
                    sweepProgress = sweepProgress,
                    showPercentageText = shouldShowPercentage && config.batteryDesign != BatteryIconDesign.PERCENT_OUTSIDE
                )
            }
        }

        // Percentage Outside default if not percent_inside or circle
        if (shouldShowPercentage &&
            config.batteryDesign != BatteryIconDesign.PERCENT_OUTSIDE &&
            config.batteryDesign != BatteryIconDesign.PERCENT_INSIDE &&
            config.batteryDesign != BatteryIconDesign.CIRCLE
        ) {
            Text(
                text = "$levelClamped%",
                color = Color(config.textColor),
                fontSize = (config.iconSize * 0.55f).sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun HorizontalBatteryIcon(
    design: BatteryIconDesign,
    level: Int,
    isCharging: Boolean,
    activeColor: Color,
    textColor: Color,
    sizeDp: Float,
    thicknessDp: Float,
    fillStyle: BatteryFillStyle,
    chargingAnimation: ChargingAnimationStyle,
    sweepProgress: Float,
    showPercentageText: Boolean
) {
    val isRounded = design == BatteryIconDesign.ROUNDED || design == BatteryIconDesign.CHARGING_ANIMATION
    val widthDp = (sizeDp * 1.55f).dp
    val heightDp = sizeDp.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(width = widthDp, height = heightDp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = thicknessDp.dp.toPx()
            val totalW = size.width
            val totalH = size.height

            val terminalW = totalW * 0.08f
            val bodyW = totalW - terminalW - strokePx
            val bodyH = totalH
            val cornerRadius = if (isRounded) CornerRadius(bodyH * 0.35f, bodyH * 0.35f) else CornerRadius(bodyH * 0.12f, bodyH * 0.12f)

            // Draw Body Outline
            drawRoundRect(
                color = Color.White.copy(alpha = 0.85f),
                topLeft = Offset(strokePx / 2, strokePx / 2),
                size = Size(bodyW, bodyH - strokePx),
                cornerRadius = cornerRadius,
                style = Stroke(width = strokePx)
            )

            // Draw Terminal Nipple on right
            val terminalH = bodyH * 0.38f
            val terminalY = (bodyH - terminalH) / 2
            drawRoundRect(
                color = Color.White.copy(alpha = 0.85f),
                topLeft = Offset(bodyW + strokePx, terminalY),
                size = Size(terminalW, terminalH),
                cornerRadius = CornerRadius(terminalW * 0.4f, terminalW * 0.4f)
            )

            // Calculate Fill Rect
            val innerPad = strokePx + 1.5f
            val maxFillW = bodyW - (innerPad * 2)
            val fillH = (bodyH - strokePx) - (innerPad * 2)
            val fillW = (maxFillW * (level / 100f)).coerceAtLeast(0f)

            if (fillStyle != BatteryFillStyle.OUTLINE && fillW > 0f) {
                val fillCorner = if (isRounded) CornerRadius(fillH * 0.3f, fillH * 0.3f) else CornerRadius(fillH * 0.1f, fillH * 0.1f)

                when (fillStyle) {
                    BatteryFillStyle.SEGMENTED -> {
                        val numBlocks = 4
                        val blockGap = 2f.dp.toPx()
                        val singleBlockW = (maxFillW - (blockGap * (numBlocks - 1))) / numBlocks
                        val filledBlocks = ((level / 100f) * numBlocks).toInt().coerceIn(0, numBlocks)

                        for (i in 0 until filledBlocks) {
                            val blockX = innerPad + (i * (singleBlockW + blockGap))
                            drawRoundRect(
                                color = activeColor,
                                topLeft = Offset(blockX, innerPad),
                                size = Size(singleBlockW, fillH),
                                cornerRadius = CornerRadius(2f, 2f)
                            )
                        }
                    }
                    BatteryFillStyle.GRADIENT -> {
                        val brush = Brush.horizontalGradient(
                            colors = listOf(activeColor.copy(alpha = 0.7f), activeColor),
                            startX = innerPad,
                            endX = innerPad + fillW
                        )
                        drawRoundRect(
                            brush = brush,
                            topLeft = Offset(innerPad, innerPad),
                            size = Size(fillW, fillH),
                            cornerRadius = fillCorner
                        )
                    }
                    else -> {
                        // SOLID or default
                        drawRoundRect(
                            color = activeColor,
                            topLeft = Offset(innerPad, innerPad),
                            size = Size(fillW, fillH),
                            cornerRadius = fillCorner
                        )
                    }
                }
            }
        }

        // Overlay charging lightning bolt if charging
        if (isCharging) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Charging",
                tint = if (chargingAnimation == ChargingAnimationStyle.LIGHTNING_GLOW) Color(0xFFFDE047) else Color.White,
                modifier = Modifier
                    .size((sizeDp * 0.7f).dp)
                    .padding(end = (sizeDp * 0.12f).dp)
            )
        }
    }
}

@Composable
private fun CircleBatteryGauge(
    level: Int,
    isCharging: Boolean,
    activeColor: Color,
    textColor: Color,
    showPercentage: Boolean,
    sizeDp: Float,
    thicknessDp: Float,
    fillStyle: BatteryFillStyle,
    sweepProgress: Float
) {
    val totalSize = (sizeDp * 1.15f).dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(totalSize)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = (thicknessDp * 1.2f).dp.toPx()
            val diameter = min(size.width, size.height) - strokePx
            val centerOffset = Offset(size.width / 2, size.height / 2)
            val sweepAngle = 360f * (level / 100f)

            // Background Ring Track
            drawCircle(
                color = Color.White.copy(alpha = 0.2f),
                radius = diameter / 2,
                center = centerOffset,
                style = Stroke(width = strokePx)
            )

            // Progress Arc
            if (fillStyle == BatteryFillStyle.GRADIENT) {
                val brush = Brush.sweepGradient(
                    listOf(activeColor.copy(alpha = 0.5f), activeColor),
                    center = centerOffset
                )
                drawArc(
                    brush = brush,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(strokePx / 2, strokePx / 2),
                    size = Size(diameter, diameter),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            } else {
                drawArc(
                    color = activeColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(strokePx / 2, strokePx / 2),
                    size = Size(diameter, diameter),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        if (isCharging) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Charging",
                tint = Color(0xFFFDE047),
                modifier = Modifier.size((sizeDp * 0.65f).dp)
            )
        } else if (showPercentage) {
            Text(
                text = "$level",
                color = textColor,
                fontSize = (sizeDp * 0.42f).sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MinimalBatteryBar(
    level: Int,
    isCharging: Boolean,
    activeColor: Color,
    sizeDp: Float,
    thicknessDp: Float,
    sweepProgress: Float
) {
    val barWidth = (sizeDp * 1.8f).dp
    val barHeight = (thicknessDp * 2.2f).dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        if (isCharging) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Charging",
                tint = Color(0xFFFDE047),
                modifier = Modifier.size((sizeDp * 0.7f).dp)
            )
        }
        Canvas(modifier = Modifier.size(width = barWidth, height = barHeight)) {
            val totalW = size.width
            val totalH = size.height
            val corner = CornerRadius(totalH / 2, totalH / 2)

            // Track background
            drawRoundRect(
                color = Color.White.copy(alpha = 0.25f),
                size = Size(totalW, totalH),
                cornerRadius = corner
            )

            // Active bar
            val activeW = totalW * (level / 100f)
            drawRoundRect(
                color = activeColor,
                size = Size(activeW, totalH),
                cornerRadius = corner
            )
        }
    }
}

@Composable
private fun InsidePercentBattery(
    level: Int,
    isCharging: Boolean,
    activeColor: Color,
    textColor: Color,
    sizeDp: Float,
    thicknessDp: Float,
    fillStyle: BatteryFillStyle,
    sweepProgress: Float
) {
    val widthDp = (sizeDp * 1.75f).dp
    val heightDp = sizeDp.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(width = widthDp, height = heightDp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = thicknessDp.dp.toPx()
            val totalW = size.width
            val totalH = size.height

            val terminalW = totalW * 0.08f
            val bodyW = totalW - terminalW - strokePx
            val bodyH = totalH
            val cornerRadius = CornerRadius(bodyH * 0.38f, bodyH * 0.38f)

            // Outline body
            drawRoundRect(
                color = Color.White.copy(alpha = 0.85f),
                topLeft = Offset(strokePx / 2, strokePx / 2),
                size = Size(bodyW, bodyH - strokePx),
                cornerRadius = cornerRadius,
                style = Stroke(width = strokePx)
            )

            // Terminal
            val terminalH = bodyH * 0.36f
            val terminalY = (bodyH - terminalH) / 2
            drawRoundRect(
                color = Color.White.copy(alpha = 0.85f),
                topLeft = Offset(bodyW + strokePx, terminalY),
                size = Size(terminalW, terminalH),
                cornerRadius = CornerRadius(terminalW * 0.4f, terminalW * 0.4f)
            )

            // Fill Level
            val innerPad = strokePx + 1.2f
            val maxFillW = bodyW - (innerPad * 2)
            val fillH = (bodyH - strokePx) - (innerPad * 2)
            val fillW = maxFillW * (level / 100f)

            if (fillStyle != BatteryFillStyle.OUTLINE && fillW > 0f) {
                drawRoundRect(
                    color = activeColor.copy(alpha = 0.5f),
                    topLeft = Offset(innerPad, innerPad),
                    size = Size(fillW, fillH),
                    cornerRadius = CornerRadius(fillH * 0.3f, fillH * 0.3f)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(end = (sizeDp * 0.1f).dp)
        ) {
            if (isCharging) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Charging",
                    tint = Color(0xFFFDE047),
                    modifier = Modifier.size((sizeDp * 0.5f).dp)
                )
            }
            Text(
                text = "$level",
                color = textColor,
                fontSize = (sizeDp * 0.42f).sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmojiBatteryIndicator(
    mascotEmoji: String,
    secondaryEmoji: String = "",
    level: Int,
    isCharging: Boolean,
    fillColor: Color,
    textColor: Color,
    sizeDp: Float,
    thicknessDp: Float,
    showPercentage: Boolean = false,
    modifier: Modifier = Modifier
) {
    val levelClamped = level.coerceIn(0, 100)
    val widthDp = (sizeDp * 1.45f).dp
    val heightDp = (sizeDp * 0.85f).dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = modifier
    ) {
        // Numeric percentage if requested
        if (showPercentage) {
            Text(
                text = "$levelClamped%",
                color = textColor,
                fontSize = (sizeDp * 0.6f).sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Cute mascot emoji (e.g. 🐹 or 🐰)
        Text(
            text = mascotEmoji,
            fontSize = (sizeDp * 0.95f).sp,
            modifier = Modifier.padding(end = 1.dp)
        )

        // Battery Capsule
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(width = widthDp, height = heightDp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokePx = 1.5f.dp.toPx()
                val totalW = size.width
                val totalH = size.height

                val terminalW = totalW * 0.08f
                val bodyW = totalW - terminalW - strokePx
                val bodyH = totalH
                val cornerRadius = CornerRadius(bodyH * 0.35f, bodyH * 0.35f)

                // Battery Outer Shell
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.9f),
                    topLeft = Offset(strokePx / 2, strokePx / 2),
                    size = Size(bodyW, bodyH - strokePx),
                    cornerRadius = cornerRadius,
                    style = Stroke(width = strokePx)
                )

                // Positive Terminal Cap
                val terminalH = bodyH * 0.4f
                val terminalY = (bodyH - terminalH) / 2
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.9f),
                    topLeft = Offset(bodyW + strokePx, terminalY),
                    size = Size(terminalW, terminalH),
                    cornerRadius = CornerRadius(terminalW * 0.4f, terminalW * 0.4f)
                )

                // Fluid Liquid Fill inside
                val innerPad = strokePx + 1.5f
                val maxFillW = bodyW - (innerPad * 2)
                val fillH = (bodyH - strokePx) - (innerPad * 2)
                val fillW = (maxFillW * (levelClamped / 100f)).coerceAtLeast(0f)

                if (fillW > 0f) {
                    drawRoundRect(
                        color = fillColor,
                        topLeft = Offset(innerPad, innerPad),
                        size = Size(fillW, fillH),
                        cornerRadius = CornerRadius(fillH * 0.28f, fillH * 0.28f)
                    )
                }
            }

            // Charging lightning bolt overlay
            if (isCharging) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Charging",
                    tint = Color(0xFFFDE047),
                    modifier = Modifier.size((sizeDp * 0.65f).dp)
                )
            }
        }
    }
}

@Composable
fun EmojiThemeCardPreview(
    theme: EmojiTheme,
    batteryLevel: Int = 75,
    isCharging: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(8.dp)
    ) {
        // Battery capsule (large preview)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(width = 62.dp, height = 36.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokePx = 2.5f.dp.toPx()
                val totalW = size.width
                val totalH = size.height

                val terminalW = 6f.dp.toPx()
                val bodyW = totalW - terminalW - strokePx
                val bodyH = totalH
                val cornerRadius = CornerRadius(bodyH * 0.36f, bodyH * 0.36f)

                // Outer Shell
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.85f),
                    topLeft = Offset(strokePx / 2, strokePx / 2),
                    size = Size(bodyW, bodyH - strokePx),
                    cornerRadius = cornerRadius,
                    style = Stroke(width = strokePx)
                )

                // Terminal Cap
                val terminalH = bodyH * 0.38f
                val terminalY = (bodyH - terminalH) / 2
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.85f),
                    topLeft = Offset(bodyW + strokePx, terminalY),
                    size = Size(terminalW, terminalH),
                    cornerRadius = CornerRadius(terminalW * 0.4f, terminalW * 0.4f)
                )

                // Liquid fill
                val innerPad = strokePx + 2f
                val maxFillW = bodyW - (innerPad * 2)
                val fillH = (bodyH - strokePx) - (innerPad * 2)
                val fillW = maxFillW * (batteryLevel / 100f)

                if (fillW > 0f) {
                    drawRoundRect(
                        color = Color(theme.fillColor),
                        topLeft = Offset(innerPad, innerPad),
                        size = Size(fillW, fillH),
                        cornerRadius = CornerRadius(fillH * 0.25f, fillH * 0.25f)
                    )
                }
            }
        }

        // Mascot sitting/leaning on the battery
        Text(
            text = theme.mascotEmoji,
            fontSize = 32.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-10).dp, y = (-2).dp)
        )
    }
}
