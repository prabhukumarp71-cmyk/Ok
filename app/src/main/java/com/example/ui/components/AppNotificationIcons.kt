package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
 * Renders status bar notification icons exactly matching modern Android & social apps
 * (Facebook, WhatsApp, Snapchat, Instagram, Messenger, Gmail, Dot separator, etc.)
 */
@Composable
fun AppNotificationRow(
    notificationIcons: List<String>,
    tintColor: Color = Color.White,
    iconSizeDp: Dp = 15.dp,
    spacingDp: Dp = 6.dp,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacingDp),
        modifier = modifier
    ) {
        notificationIcons.forEach { iconKey ->
            AppNotificationIconItem(
                iconKey = iconKey,
                tintColor = tintColor,
                size = iconSizeDp
            )
        }
    }
}

@Composable
fun AppNotificationIconItem(
    iconKey: String,
    tintColor: Color,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        when (iconKey.lowercase()) {
            "facebook", "fb" -> FacebookNotificationIcon(tintColor = tintColor)
            "whatsapp", "wa" -> WhatsAppNotificationIcon(tintColor = tintColor)
            "snapchat", "snap" -> SnapchatNotificationIcon(tintColor = tintColor)
            "instagram", "insta" -> InstagramNotificationIcon(tintColor = tintColor)
            "messenger" -> MessengerNotificationIcon(tintColor = tintColor)
            "gmail" -> GmailNotificationIcon(tintColor = tintColor)
            "telegram" -> TelegramNotificationIcon(tintColor = tintColor)
            "twitter", "x" -> TwitterXNotificationIcon(tintColor = tintColor)
            "youtube", "yt" -> YouTubeNotificationIcon(tintColor = tintColor)
            "dot", "bullet" -> DotNotificationIcon(tintColor = tintColor)
            else -> DotNotificationIcon(tintColor = tintColor)
        }
    }
}

/**
 * Facebook notification icon: solid circle with 'f' cut out
 */
@Composable
fun FacebookNotificationIcon(tintColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val r = w / 2f
        val center = Offset(r, r)

        // Draw outer filled circle
        drawCircle(
            color = tintColor,
            radius = r,
            center = center
        )

        // Draw the distinctive Facebook 'f' inside using transparent/background cutout
        val fPath = Path().apply {
            val startX = w * 0.48f
            val topY = h * 0.22f
            val botY = h * 0.82f

            moveTo(startX, botY)
            lineTo(startX + w * 0.14f, botY)
            lineTo(startX + w * 0.14f, h * 0.52f)
            lineTo(startX + w * 0.26f, h * 0.52f)
            lineTo(startX + w * 0.28f, h * 0.40f)
            lineTo(startX + w * 0.14f, h * 0.40f)
            lineTo(startX + w * 0.14f, h * 0.32f)
            cubicTo(
                startX + w * 0.14f, h * 0.25f,
                startX + w * 0.20f, topY,
                startX + w * 0.30f, topY
            )
            lineTo(startX + w * 0.32f, topY)
            lineTo(startX + w * 0.32f, topY - h * 0.02f)
            lineTo(startX + w * 0.20f, topY - h * 0.02f)
            cubicTo(
                startX + w * 0.06f, topY - h * 0.02f,
                startX, h * 0.25f,
                startX, h * 0.35f
            )
            lineTo(startX, h * 0.40f)
            lineTo(startX - w * 0.10f, h * 0.40f)
            lineTo(startX - w * 0.10f, h * 0.52f)
            lineTo(startX, h * 0.52f)
            close()
        }

        // Draw 'f' in translucent background color or cut out
        drawPath(fPath, color = Color(0xDD000000), style = Fill)
    }
}

/**
 * WhatsApp notification icon: Speech bubble with phone handset inside
 */
@Composable
fun WhatsAppNotificationIcon(tintColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Outer speech bubble path with tail at bottom-left
        val bubblePath = Path().apply {
            val cx = w * 0.5f
            val cy = h * 0.46f
            val r = w * 0.42f

            // Circle bubble with a small speech pointer pointing southwest
            moveTo(cx + r, cy)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(cx - r, cy - r, cx + r, cy + r),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 220f,
                forceMoveTo = false
            )
            // Tail
            lineTo(w * 0.10f, h * 0.90f)
            lineTo(w * 0.28f, h * 0.76f)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(cx - r, cy - r, cx + r, cy + r),
                startAngleDegrees = 120f,
                sweepAngleDegrees = 120f,
                forceMoveTo = false
            )
            close()
        }

        drawPath(bubblePath, color = tintColor, style = Fill)

        // Handset cutout inside
        val handsetPath = Path().apply {
            moveTo(w * 0.36f, h * 0.34f)
            cubicTo(w * 0.34f, h * 0.44f, w * 0.44f, h * 0.56f, w * 0.56f, h * 0.58f)
            lineTo(w * 0.62f, h * 0.52f)
            lineTo(w * 0.68f, h * 0.54f)
            lineTo(w * 0.66f, h * 0.66f)
            cubicTo(w * 0.48f, h * 0.66f, w * 0.28f, h * 0.46f, w * 0.28f, h * 0.28f)
            lineTo(w * 0.40f, h * 0.26f)
            lineTo(w * 0.42f, h * 0.32f)
            close()
        }
        drawPath(handsetPath, color = Color(0xEE000000), style = Fill)
    }
}

/**
 * Snapchat notification icon: Iconic ghost silhouette
 */
@Composable
fun SnapchatNotificationIcon(tintColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val ghostPath = Path().apply {
            // Head (rounded top dome)
            moveTo(w * 0.28f, h * 0.42f)
            cubicTo(w * 0.28f, h * 0.15f, w * 0.72f, h * 0.15f, w * 0.72f, h * 0.42f)

            // Right arm
            cubicTo(w * 0.75f, h * 0.44f, w * 0.88f, h * 0.46f, w * 0.88f, h * 0.54f)
            cubicTo(w * 0.88f, h * 0.60f, w * 0.78f, h * 0.62f, w * 0.72f, h * 0.56f)

            // Body right side down to ripples
            cubicTo(w * 0.72f, h * 0.68f, w * 0.84f, h * 0.76f, w * 0.86f, h * 0.84f)
            cubicTo(w * 0.80f, h * 0.88f, w * 0.70f, h * 0.82f, w * 0.62f, h * 0.86f)
            cubicTo(w * 0.54f, h * 0.88f, w * 0.46f, h * 0.88f, w * 0.38f, h * 0.86f)
            cubicTo(w * 0.30f, h * 0.82f, w * 0.20f, h * 0.88f, w * 0.14f, h * 0.84f)

            // Body left side up to left arm
            cubicTo(w * 0.16f, h * 0.76f, w * 0.28f, h * 0.68f, w * 0.28f, h * 0.56f)
            cubicTo(w * 0.22f, h * 0.62f, w * 0.12f, h * 0.60f, w * 0.12f, h * 0.54f)
            cubicTo(w * 0.12f, h * 0.46f, w * 0.25f, h * 0.44f, w * 0.28f, h * 0.42f)
            close()
        }

        drawPath(ghostPath, color = tintColor, style = Fill)
    }
}

/**
 * Instagram notification icon: Rounded camera rectangle with center lens & flash
 */
@Composable
fun InstagramNotificationIcon(tintColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val strokeW = w * 0.12f

        // Outer rounded box
        drawRoundRect(
            color = tintColor,
            topLeft = Offset(strokeW / 2, strokeW / 2),
            size = Size(w - strokeW, h - strokeW),
            cornerRadius = CornerRadius(w * 0.26f, h * 0.26f),
            style = Stroke(width = strokeW)
        )

        // Center lens circle
        drawCircle(
            color = tintColor,
            radius = w * 0.20f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = Stroke(width = strokeW)
        )

        // Top right flash dot
        drawCircle(
            color = tintColor,
            radius = w * 0.05f,
            center = Offset(w * 0.74f, h * 0.26f),
            style = Fill
        )
    }
}

/**
 * Messenger notification icon: Bubble with lightning bolt
 */
@Composable
fun MessengerNotificationIcon(tintColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val cy = h * 0.46f
        val r = w * 0.42f

        // Bubble
        val bubblePath = Path().apply {
            moveTo(cx + r, cy)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(cx - r, cy - r, cx + r, cy + r),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 220f,
                forceMoveTo = false
            )
            lineTo(w * 0.12f, h * 0.88f)
            lineTo(w * 0.28f, h * 0.74f)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(cx - r, cy - r, cx + r, cy + r),
                startAngleDegrees = 110f,
                sweepAngleDegrees = 130f,
                forceMoveTo = false
            )
            close()
        }
        drawPath(bubblePath, color = tintColor, style = Fill)

        // Lightning bolt cutout
        val boltPath = Path().apply {
            moveTo(w * 0.60f, h * 0.24f)
            lineTo(w * 0.36f, h * 0.50f)
            lineTo(w * 0.48f, h * 0.50f)
            lineTo(w * 0.40f, h * 0.68f)
            lineTo(w * 0.64f, h * 0.42f)
            lineTo(w * 0.52f, h * 0.42f)
            close()
        }
        drawPath(boltPath, color = Color(0xEE000000), style = Fill)
    }
}

/**
 * Gmail notification icon: Envelope with distinctive M shape
 */
@Composable
fun GmailNotificationIcon(tintColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val strokeW = w * 0.12f

        // Envelope outline
        drawRoundRect(
            color = tintColor,
            topLeft = Offset(strokeW / 2, h * 0.22f),
            size = Size(w - strokeW, h * 0.56f),
            cornerRadius = CornerRadius(w * 0.10f, h * 0.10f),
            style = Stroke(width = strokeW)
        )

        // 'M' fold lines
        val mPath = Path().apply {
            moveTo(strokeW / 2, h * 0.24f)
            lineTo(w * 0.5f, h * 0.54f)
            lineTo(w - strokeW / 2, h * 0.24f)
        }
        drawPath(mPath, color = tintColor, style = Stroke(width = strokeW))
    }
}

/**
 * Telegram notification icon: Circle with paper plane
 */
@Composable
fun TelegramNotificationIcon(tintColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawCircle(color = tintColor, radius = w * 0.48f, center = Offset(w * 0.5f, h * 0.5f))

        // Paper plane cutout
        val planePath = Path().apply {
            moveTo(w * 0.24f, h * 0.48f)
            lineTo(w * 0.74f, h * 0.26f)
            lineTo(w * 0.62f, h * 0.72f)
            lineTo(w * 0.48f, h * 0.58f)
            lineTo(w * 0.40f, h * 0.66f)
            lineTo(w * 0.42f, h * 0.54f)
            lineTo(w * 0.66f, h * 0.36f)
            lineTo(w * 0.34f, h * 0.48f)
            close()
        }
        drawPath(planePath, color = Color(0xEE000000), style = Fill)
    }
}

/**
 * Twitter / X notification icon
 */
@Composable
fun TwitterXNotificationIcon(tintColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val strokeW = w * 0.15f

        drawLine(
            color = tintColor,
            start = Offset(w * 0.18f, h * 0.18f),
            end = Offset(w * 0.82f, h * 0.82f),
            strokeWidth = strokeW
        )
        drawLine(
            color = tintColor,
            start = Offset(w * 0.82f, h * 0.18f),
            end = Offset(w * 0.18f, h * 0.82f),
            strokeWidth = strokeW
        )
    }
}

/**
 * YouTube notification icon
 */
@Composable
fun YouTubeNotificationIcon(tintColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRoundRect(
            color = tintColor,
            topLeft = Offset(0f, h * 0.22f),
            size = Size(w, h * 0.56f),
            cornerRadius = CornerRadius(w * 0.20f, h * 0.20f),
            style = Fill
        )

        val playPath = Path().apply {
            moveTo(w * 0.42f, h * 0.36f)
            lineTo(w * 0.64f, h * 0.50f)
            lineTo(w * 0.42f, h * 0.64f)
            close()
        }
        drawPath(playPath, color = Color(0xEE000000), style = Fill)
    }
}

/**
 * Dot / overflow indicator icon (e.g. • in the screenshot)
 */
@Composable
fun DotNotificationIcon(tintColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val r = size.width * 0.18f
        drawCircle(
            color = tintColor,
            radius = r,
            center = Offset(size.width * 0.5f, size.height * 0.5f)
        )
    }
}
