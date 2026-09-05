package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeviceProfile

@Composable
fun SettingsScreen(
    isOverlayGranted: Boolean,
    isOverlayActive: Boolean,
    onToggleOverlay: () -> Unit,
    onCheckPermissions: () -> Unit,
    onResetDefaults: () -> Unit,
    onExportJson: () -> String,
    onImportJson: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var autoStartOnBoot by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var exportedJsonString by remember { mutableStateOf("") }
    var importJsonString by remember { mutableStateOf("") }
    var showTroubleshootingDialog by remember { mutableStateOf(false) }
    var showArchitectureDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "Settings & System",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "System permissions, overlay service & backups",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Overlay & Automation Section
        item {
            Text(
                text = "Service & Automation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Overlay Service Master Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Screen Status Bar Overlay", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = if (isOverlayActive) "Service running at top of display" else "Show custom status bar over apps",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isOverlayActive,
                            onCheckedChange = { onToggleOverlay() },
                            modifier = Modifier.testTag("overlay_toggle_settings")
                        )
                    }

                    HorizontalDivider()

                    // Start Automatically on Boot
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Start on Device Boot", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "Restore customization automatically when phone restarts",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = autoStartOnBoot,
                            onCheckedChange = { autoStartOnBoot = it }
                        )
                    }
                }
            }
        }

        // Permissions Manager Section
        item {
            Text(
                text = "Permission Manager",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Overlay Permission item
                    PermissionRowItem(
                        title = "Display Over Other Apps",
                        subtitle = if (isOverlayGranted) "Granted - Overlay can draw over screen" else "Required - Tap to open Android settings",
                        isGranted = isOverlayGranted,
                        onClick = {
                            try {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                )
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                                context.startActivity(intent)
                            }
                        }
                    )

                    HorizontalDivider()

                    // Battery Optimization item
                    PermissionRowItem(
                        title = "Ignore Battery Optimizations",
                        subtitle = "Prevents Android from killing the overlay in background",
                        isGranted = isBatteryOptimizationIgnored(context),
                        onClick = {
                            try {
                                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Open system battery settings to allow background activity", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    HorizontalDivider()

                    // Notification Permission (Android 13+)
                    PermissionRowItem(
                        title = "Foreground Notification",
                        subtitle = "Required for persistent overlay service on Android 13+",
                        isGranted = true,
                        onClick = {
                            try {
                                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    )

                    HorizontalDivider()

                    // Notification Listener Access for live notification icons
                    val hasNotifAccess = com.example.service.ActiveNotificationTracker.isNotificationAccessGranted(context)
                    PermissionRowItem(
                        title = "Notification Access (Live App Icons)",
                        subtitle = if (hasNotifAccess) "Granted - Overlay reads incoming app icons in real-time" else "Optional - Tap to allow overlay to show real app notification icons",
                        isGranted = hasNotifAccess,
                        onClick = {
                            try {
                                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Open system notification access settings", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }

        // Android Compatibility & No-Root Explanation
        item {
            Text(
                text = "Android Compatibility",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showArchitectureDialog = true },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "No-Root Architecture (Android 14, 15, 16)",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "How StatusBar Studio safely operates without root",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Troubleshooting & Device Guidelines
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTroubleshootingDialog = true },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(28.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Device-Specific Troubleshooting",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Guides for Xiaomi, Samsung, Motorola & OnePlus",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Backup, Restore & Reset Section
        item {
            Text(
                text = "Backup & Configuration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            exportedJsonString = onExportJson()
                            showExportDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("export_config_button")
                    ) {
                        Icon(imageVector = Icons.Default.FileUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Configuration JSON")
                    }

                    OutlinedButton(
                        onClick = {
                            importJsonString = ""
                            showImportDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("import_config_button")
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Import Configuration JSON")
                    }

                    TextButton(
                        onClick = { showResetConfirmDialog = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reset Customization to Defaults")
                    }
                }
            }
        }

        // Privacy & About Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PrivacyTip, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(text = "Privacy Guarantee", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "StatusBar Studio is 100% offline. Zero personal data, network telemetry, or analytics are ever gathered or transmitted.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Version 1.0 (Build 2026.09) • Designed for Android 14, 15, and 16",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset Customization?") },
            text = { Text("This will revert your status bar style, battery indicators, colors, and layout back to factory defaults.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetDefaults()
                        showResetConfirmDialog = false
                        Toast.makeText(context, "Settings reset to defaults", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Configuration") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Your complete status bar customization setup in JSON format:")
                    OutlinedTextField(
                        value = exportedJsonString,
                        onValueChange = {},
                        readOnly = true,
                        maxLines = 6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("StatusBar Studio Config", exportedJsonString)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                        showExportDialog = false
                    }
                ) {
                    Text("Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Configuration") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Paste your exported configuration JSON:")
                    OutlinedTextField(
                        value = importJsonString,
                        onValueChange = { importJsonString = it },
                        placeholder = { Text("Paste JSON here...") },
                        maxLines = 6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val ok = onImportJson(importJsonString)
                        if (ok) {
                            Toast.makeText(context, "Configuration restored successfully!", Toast.LENGTH_SHORT).show()
                            showImportDialog = false
                        } else {
                            Toast.makeText(context, "Invalid JSON format", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Architecture & Security Model Dialog
    if (showArchitectureDialog) {
        AlertDialog(
            onDismissRequest = { showArchitectureDialog = false },
            title = { Text("No-Root Architecture") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Android 14, 15, and 16 Security Model:",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Android strictly prevents third-party apps from modifying or hooking directly into the OS SystemUI without root privileges.\n\nStatusBar Studio uses official, non-root Android APIs: a high-priority Floating System Overlay (WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY) that renders your personalized battery meter, clock, and indicators seamlessly atop the screen.\n\nThis guarantees full safety, zero warranty void, and low battery consumption without compromising system integrity.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showArchitectureDialog = false }) {
                    Text("Understood")
                }
            }
        )
    }

    // Troubleshooting Dialog
    if (showTroubleshootingDialog) {
        AlertDialog(
            onDismissRequest = { showTroubleshootingDialog = false },
            title = { Text("Troubleshooting Guide") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Xiaomi / Redmi (MIUI & HyperOS):", fontWeight = FontWeight.Bold)
                    Text(
                        text = "Go to Settings > Apps > Permissions > Other permissions > StatusBar Studio. Enable 'Display pop-up windows while running in the background'.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    HorizontalDivider()
                    Text(text = "Samsung (OneUI):", fontWeight = FontWeight.Bold)
                    Text(
                        text = "Go to Settings > Apps > StatusBar Studio > Battery. Choose 'Unrestricted' so OneUI does not put the overlay to sleep.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    HorizontalDivider()
                    Text(text = "Notch Alignment:", fontWeight = FontWeight.Bold)
                    Text(
                        text = "If icons overlap with your camera hole punch, switch device profile or adjust 'Left Margin' and 'Right Margin' in the Customize tab.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showTroubleshootingDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun PermissionRowItem(
    title: String,
    subtitle: String,
    isGranted: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                if (isGranted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Granted",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isGranted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun isBatteryOptimizationIgnored(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: false
    } else {
        true
    }
}
