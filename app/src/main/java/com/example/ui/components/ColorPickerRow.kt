package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val POPULAR_COLORS = listOf(
    0xFFEF4444, // Red 500
    0xFFF97316, // Orange 500
    0xFFF59E0B, // Amber 500
    0xFFEAB308, // Yellow 500
    0xFF10B981, // Emerald 500
    0xFF22C55E, // Green 500
    0xFF06B6D4, // Cyan 500
    0xFF38BDF8, // Light Sky 400
    0xFF3B82F6, // Blue 500
    0xFF6366F1, // Indigo 500
    0xFF8B5CF6, // Purple 500
    0xFFEC4899, // Pink 500
    0xFFFFFFFF, // Pure White
    0xFF94A3B8, // Slate Grey
    0xFF0F172A, // Midnight Slate
    0xFF000000  // AMOLED Black
)

@Composable
fun ColorPickerRow(
    title: String,
    selectedColor: Long,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Current selected color preview pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(selectedColor))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    )
                    Text(
                        text = "#${java.lang.Long.toHexString(selectedColor).takeLast(6).uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal scrolling color dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            POPULAR_COLORS.forEach { colorValue ->
                val isSelected = selectedColor == colorValue
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(colorValue))
                        .border(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.25f),
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(colorValue) }
                        .testTag("color_dot_${java.lang.Long.toHexString(colorValue)}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = if (colorValue == 0xFFFFFFFF || colorValue == 0xFF38BDF8) Color.Black else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
