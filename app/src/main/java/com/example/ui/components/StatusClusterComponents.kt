package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Renders the two-line stacked Network Speed Meter (e.g. 23.9 on top, KB/s on bottom)
 * Exactly as seen in Motorola / Android status bar.
 */
@Composable
fun NetworkSpeedMeter(
    speedValue: String = "23.9",
    speedUnit: String = "KB/s",
    textColor: Color = Color.White,
    fontSizeSp: Float = 14f,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = speedValue,
            color = textColor,
            fontSize = (fontSizeSp * 0.68f).sp,
            fontWeight = FontWeight.Bold,
            lineHeight = (fontSizeSp * 0.72f).sp
        )
        Text(
            text = speedUnit,
            color = textColor.copy(alpha = 0.95f),
            fontSize = (fontSizeSp * 0.46f).sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = (fontSizeSp * 0.50f).sp
        )
    }
}

/**
 * VoNR / VoLTE Indicator
 * Stacked: "Vo" with subtle radio signal waves on top, and "NR" or "LTE" on the bottom.
 */
@Composable
fun VoNrIndicator(
    text: String = "NR",
    textColor: Color = Color.White,
    fontSizeSp: Float = 14f,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = "Vo",
                color = textColor,
                fontSize = (fontSizeSp * 0.54f).sp,
                fontWeight = FontWeight.Bold,
                lineHeight = (fontSizeSp * 0.56f).sp
            )
            // Small radio wifi/NR arc
            Canvas(modifier = Modifier.size((fontSizeSp * 0.44f).dp)) {
                val w = size.width
                val h = size.height
                val strokeW = w * 0.22f

                // Outer arc
                drawArc(
                    color = textColor,
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(0f, 0f),
                    size = Size(w, h),
                    style = Stroke(width = strokeW)
                )
                // Inner arc
                drawArc(
                    color = textColor,
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(w * 0.22f, h * 0.22f),
                    size = Size(w * 0.56f, h * 0.56f),
                    style = Stroke(width = strokeW)
                )
            }
        }
        Text(
            text = text,
            color = textColor,
            fontSize = (fontSizeSp * 0.48f).sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = (fontSizeSp * 0.50f).sp
        )
    }
}

/**
 * 5G++ Network Indicator with data transfer arrows (⇅ or ↑↓)
 * Matches the exact badge shown in the user's screenshot.
 */
@Composable
fun NetworkTrafficBadge(
    badgeText: String = "5G++",
    showArrows: Boolean = true,
    textColor: Color = Color.White,
    fontSizeSp: Float = 14f,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = badgeText,
            color = textColor,
            fontSize = (fontSizeSp * 0.72f).sp,
            fontWeight = FontWeight.Black,
            lineHeight = (fontSizeSp * 0.76f).sp
        )
        if (showArrows) {
            Canvas(
                modifier = Modifier
                    .width((fontSizeSp * 0.65f).dp)
                    .height((fontSizeSp * 0.35f).dp)
            ) {
                val w = size.width
                val h = size.height

                // Left Arrow (Up)
                val leftArrowPath = Path().apply {
                    val cx = w * 0.30f
                    moveTo(cx, 0f)
                    lineTo(cx - w * 0.16f, h * 0.45f)
                    lineTo(cx + w * 0.16f, h * 0.45f)
                    close()
                    // Stem
                    addRect(
                        androidx.compose.ui.geometry.Rect(
                            left = cx - w * 0.06f,
                            top = h * 0.40f,
                            right = cx + w * 0.06f,
                            bottom = h
                        )
                    )
                }
                drawPath(leftArrowPath, color = textColor, style = Fill)

                // Right Arrow (Down)
                val rightArrowPath = Path().apply {
                    val cx = w * 0.70f
                    moveTo(cx, h)
                    lineTo(cx - w * 0.16f, h * 0.55f)
                    lineTo(cx + w * 0.16f, h * 0.55f)
                    close()
                    // Stem
                    addRect(
                        androidx.compose.ui.geometry.Rect(
                            left = cx - w * 0.06f,
                            top = 0f,
                            right = cx + w * 0.06f,
                            bottom = h * 0.60f
                        )
                    )
                }
                drawPath(rightArrowPath, color = textColor, style = Fill)
            }
        }
    }
}

/**
 * Solid Right-Triangle Cellular Signal
 * The clean right-angled triangle signal wedge seen on Motorola, Pixel & modern Android devices.
 */
@Composable
fun TriangleCellularSignal(
    strength: Int = 4, // 1 to 4
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.White.copy(alpha = 0.3f),
    sizeDp: Dp = 15.dp,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .width(sizeDp)
            .height(sizeDp * 0.9f)
    ) {
        val w = size.width
        val h = size.height

        // Full triangle background path (slope from bottom-left to top-right)
        val fullTrianglePath = Path().apply {
            moveTo(0f, h)
            lineTo(w, h)
            lineTo(w, 0f)
            close()
        }

        // Draw inactive base
        drawPath(fullTrianglePath, color = inactiveColor, style = Fill)

        // Draw active filled portion based on strength
        val activeFraction = (strength.coerceIn(1, 4)) / 4f
        val activeW = w * activeFraction
        val activeH = h * activeFraction

        if (activeFraction >= 0.95f) {
            // Full solid fill
            drawPath(fullTrianglePath, color = activeColor, style = Fill)
        } else {
            // Partial wedge
            val partialPath = Path().apply {
                moveTo(0f, h)
                lineTo(activeW, h)
                lineTo(activeW, h - activeH)
                close()
            }
            drawPath(partialPath, color = activeColor, style = Fill)
        }
    }
}

/**
 * Vertical Battery Capsule (with top positive cap and fluid vertical fill)
 * Exactly as displayed in the screenshot: vertical capsule with 64% battery level.
 */
@Composable
fun VerticalBatteryIcon(
    level: Int,
    isCharging: Boolean,
    activeColor: Color,
    textColor: Color,
    heightDp: Dp = 17.dp,
    strokeDp: Dp = 1.6.dp,
    modifier: Modifier = Modifier
) {
    val widthDp = heightDp * 0.52f

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(width = widthDp, height = heightDp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val strokePx = strokeDp.toPx()

            // Terminal cap at top
            val capW = w * 0.44f
            val capH = h * 0.10f
            val capLeft = (w - capW) / 2f

            drawRoundRect(
                color = activeColor,
                topLeft = Offset(capLeft, 0f),
                size = Size(capW, capH),
                cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx()),
                style = Fill
            )

            // Main battery body
            val bodyTop = capH + 0.5.dp.toPx()
            val bodyH = h - bodyTop
            val cornerRadius = CornerRadius(2.2.dp.toPx(), 2.2.dp.toPx())

            // Outline of battery body
            drawRoundRect(
                color = activeColor,
                topLeft = Offset(strokePx / 2, bodyTop + strokePx / 2),
                size = Size(w - strokePx, bodyH - strokePx),
                cornerRadius = cornerRadius,
                style = Stroke(width = strokePx)
            )

            // Fill from bottom up
            val fillPadding = strokePx + 1.2.dp.toPx()
            val maxFillH = bodyH - (fillPadding * 2)
            val fillH = (maxFillH * (level.coerceIn(0, 100) / 100f)).coerceAtLeast(0f)
            val fillW = w - (fillPadding * 2)

            if (fillH > 0 && fillW > 0) {
                drawRoundRect(
                    color = activeColor,
                    topLeft = Offset(fillPadding, bodyTop + bodyH - fillPadding - fillH),
                    size = Size(fillW, fillH),
                    cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx()),
                    style = Fill
                )
            }

            // Charging lightning bolt if charging
            if (isCharging) {
                val boltPath = Path().apply {
                    val cx = w * 0.5f
                    val cy = bodyTop + bodyH * 0.5f
                    val bw = w * 0.35f
                    val bh = bodyH * 0.50f

                    moveTo(cx + bw * 0.1f, cy - bh * 0.5f)
                    lineTo(cx - bw * 0.5f, cy + bh * 0.05f)
                    lineTo(cx, cy + bh * 0.05f)
                    lineTo(cx - bw * 0.1f, cy + bh * 0.5f)
                    lineTo(cx + bw * 0.5f, cy - bh * 0.05f)
                    lineTo(cx, cy - bh * 0.05f)
                    close()
                }
                drawPath(boltPath, color = textColor, style = Fill)
            }
        }
    }
}
