package com.example.model

enum class BatteryIconDesign(val displayName: String, val description: String) {
    CLASSIC("Classic Battery", "Standard rectangular battery with terminal cap"),
    ROUNDED("Rounded Pill", "Smooth curved pill with fluid fill level"),
    CIRCLE("Circle Gauge", "Circular progress ring with center readout"),
    MINIMAL("Minimal Bar", "Sleek horizontal line indicator"),
    PERCENT_INSIDE("Percentage Inside", "Numeric percentage centered inside battery body"),
    PERCENT_OUTSIDE("Percentage Outside", "Percentage badge placed adjacent to battery"),
    VERTICAL_CAPSULE("Vertical Capsule", "Vertical battery with top terminal cap & percentage"),
    CHARGING_ANIMATION("Animated Energy", "Dynamic energy flow with charging effects"),
    DYNAMIC_LEVEL("Dynamic Level", "Adaptive colors shifting with battery percentage")
}

enum class BatteryFillStyle(val displayName: String) {
    SOLID("Solid Fill"),
    GRADIENT("Gradient Fill"),
    SEGMENTED("Segmented Blocks"),
    OUTLINE("Outline Only")
}

enum class BatteryPercentageVisibility(val displayName: String) {
    ALWAYS("Always Show"),
    HIDE("Hide"),
    LOW_ONLY("Low Battery (<20%)"),
    CHARGING_ONLY("While Charging Only")
}

enum class ChargingAnimationStyle(val displayName: String) {
    PULSE("Pulse Glow"),
    LIGHTNING_GLOW("Lightning Glow"),
    SWEEP("Energy Sweep"),
    NONE("No Animation")
}

enum class StatusIconStyle(val displayName: String) {
    FILLED("Filled Modern"),
    OUTLINED("Outlined Minimal"),
    NEON("Neon Glow"),
    COMPACT("Compact")
}

enum class DeviceProfile(val displayName: String, val brandSubtitle: String) {
    GENERIC("Generic Android", "Standard AOSP layout and spacing"),
    PIXEL("Google Pixel", "Material You styling with center hole-punch clearance"),
    SAMSUNG("Samsung OneUI", "Compact right-aligned battery & OneUI spacing"),
    XIAOMI("Xiaomi HyperOS", "Pill battery with percentage inside & bold indicators"),
    ONEPLUS("OnePlus OxygenOS", "Iconic red '1' clock accent & circular battery"),
    MOTOROLA("Motorola", "Circle battery ring & signature moto spacing")
}

enum class NotchStyle(val displayName: String) {
    CENTER_HOLE_PUNCH("Center Punch Hole"),
    LEFT_HOLE_PUNCH("Left Punch Hole"),
    PILL_ISLAND("Pill Island"),
    WATERDROP("Waterdrop Notch"),
    NONE("No Notch / Full Width")
}

data class StatusBarConfig(
    // Battery Customization
    val batteryDesign: BatteryIconDesign = BatteryIconDesign.ROUNDED,
    val batteryFillStyle: BatteryFillStyle = BatteryFillStyle.SOLID,
    val percentageVisibility: BatteryPercentageVisibility = BatteryPercentageVisibility.ALWAYS,
    val iconSize: Float = 20f, // in dp
    val iconThickness: Float = 2.0f, // in dp
    val colorLow: Long = 0xFFEF4444, // Red
    val colorNormal: Long = 0xFF38BDF8, // Cyan Blue
    val colorFull: Long = 0xFF10B981, // Emerald Green
    val colorCharging: Long = 0xFFF59E0B, // Amber
    val chargingAnimation: ChargingAnimationStyle = ChargingAnimationStyle.LIGHTNING_GLOW,

    // Status Bar Elements Visibility
    val showBattery: Boolean = true,
    val showBatteryPercentage: Boolean = true,
    val showTime: Boolean = true,
    val showWifi: Boolean = true,
    val showMobileSignal: Boolean = true,
    val showBluetooth: Boolean = true,
    val showAlarm: Boolean = false,
    val showNotificationDots: Boolean = true,
    val showDnd: Boolean = false,
    val showLocation: Boolean = false,
    val showVpn: Boolean = false,

    // Time & Layout Customization
    val timeFormat24Hour: Boolean = false,
    val showTimeSeconds: Boolean = false,
    val isClockOnLeft: Boolean = true,
    val iconStyle: StatusIconStyle = StatusIconStyle.FILLED,
    val statusIconSize: Float = 16f, // in dp
    val iconSpacing: Float = 6f, // in dp
    val statusBarHeight: Float = 32f, // in dp
    val leftPadding: Float = 16f, // in dp
    val rightPadding: Float = 16f, // in dp

    // Colors & Appearance
    val barBackgroundColor: Long = 0x00000000, // Transparent by default
    val barAlpha: Float = 0.0f, // 0.0f to 1.0f
    val textColor: Long = 0xFFFFFFFF, // Pure White

    // Emoji Battery Themes & Mascot Customization
    val isEmojiBattery: Boolean = true,
    val emojiThemeId: String = "cute_bunny_love",
    val emojiMascot: String = "🐰",
    val emojiSecondary: String = "🥕",
    val emojiBatteryFillColor: Long = 0xFFFF6584,
    val showStatusEmojis: Boolean = true,
    val statusEmojis: List<String> = listOf("🔇", "😎", "🐱"),
    val customSignalColor: Long = 0xFFFF4B6E,
    val customNetworkType: String = "3G",

    // App Notifications (Facebook, WhatsApp, Snapchat, Dot • from Screenshot)
    val showAppNotifications: Boolean = true,
    val notificationIcons: List<String> = listOf("facebook", "whatsapp", "snapchat", "snapchat", "dot"),

    // Network Speed Meter (e.g. 23.9 KB/s stacked)
    val showNetworkSpeed: Boolean = true,
    val networkSpeedValue: String = "23.9",
    val networkSpeedUnit: String = "KB/s",

    // VoNR / VoLTE indicator (Vo with waves on top, NR on bottom)
    val showVoNr: Boolean = true,
    val voNrText: String = "NR",

    // 5G++ Network Badge with Traffic Activity Arrows (5G++ with ⇅)
    val showNetworkBadge: Boolean = true,
    val networkBadgeText: String = "5G++",
    val showTrafficArrows: Boolean = true,

    // Cellular Icon Style (Triangle signal slope from Motorola/AOSP vs standard bars)
    val useTriangleSignal: Boolean = true,

    // Device Profile & Hardware Simulation
    val deviceProfile: DeviceProfile = DeviceProfile.PIXEL,
    val notchStyle: NotchStyle = NotchStyle.CENTER_HOLE_PUNCH
)

data class Preset(
    val id: String,
    val name: String,
    val description: String,
    val isBuiltIn: Boolean = false,
    val config: StatusBarConfig
)
