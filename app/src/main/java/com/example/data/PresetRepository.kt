package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class PresetRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("statusbar_studio_prefs", Context.MODE_PRIVATE)

    private val _activeConfig = MutableStateFlow(loadSavedConfig())
    val activeConfig: StateFlow<StatusBarConfig> = _activeConfig.asStateFlow()

    private val _presets = MutableStateFlow<List<Preset>>(emptyList())
    val presets: StateFlow<List<Preset>> = _presets.asStateFlow()

    init {
        loadAllPresets()
    }

    fun updateConfig(newConfig: StatusBarConfig) {
        _activeConfig.value = newConfig
        saveConfigToPrefs(newConfig)
    }

    fun isOverlayEnabled(): Boolean = prefs.getBoolean("is_overlay_enabled", false)

    fun setOverlayEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("is_overlay_enabled", enabled).apply()
    }

    fun resetToDefaults() {
        val defaultConfig = StatusBarConfig()
        updateConfig(defaultConfig)
    }

    fun savePreset(name: String, description: String, config: StatusBarConfig): Preset {
        val newPreset = Preset(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            isBuiltIn = false,
            config = config
        )
        val current = _presets.value.toMutableList()
        current.add(newPreset)
        _presets.value = current
        saveCustomPresetsToPrefs()
        return newPreset
    }

    fun renamePreset(id: String, newName: String) {
        val current = _presets.value.map { preset ->
            if (preset.id == id && !preset.isBuiltIn) {
                preset.copy(name = newName)
            } else preset
        }
        _presets.value = current
        saveCustomPresetsToPrefs()
    }

    fun duplicatePreset(id: String) {
        val original = _presets.value.find { it.id == id } ?: return
        val duplicate = Preset(
            id = UUID.randomUUID().toString(),
            name = "${original.name} (Copy)",
            description = original.description,
            isBuiltIn = false,
            config = original.config
        )
        val current = _presets.value.toMutableList()
        current.add(duplicate)
        _presets.value = current
        saveCustomPresetsToPrefs()
    }

    fun deletePreset(id: String): Boolean {
        val preset = _presets.value.find { it.id == id }
        if (preset == null || preset.isBuiltIn) return false
        val current = _presets.value.filter { it.id != id }
        _presets.value = current
        saveCustomPresetsToPrefs()
        return true
    }

    fun exportConfigurationJson(): String {
        val root = JSONObject()
        root.put("activeConfig", configToJson(_activeConfig.value))
        val customPresets = _presets.value.filter { !it.isBuiltIn }
        val presetsArray = JSONArray()
        for (p in customPresets) {
            val pObj = JSONObject()
            pObj.put("id", p.id)
            pObj.put("name", p.name)
            pObj.put("description", p.description)
            pObj.put("config", configToJson(p.config))
            presetsArray.put(pObj)
        }
        root.put("customPresets", presetsArray)
        return root.toString(2)
    }

    fun importConfigurationJson(jsonStr: String): Boolean {
        return try {
            val root = JSONObject(jsonStr)
            if (root.has("activeConfig")) {
                val configObj = root.getJSONObject("activeConfig")
                val parsedConfig = jsonToConfig(configObj)
                updateConfig(parsedConfig)
            }
            if (root.has("customPresets")) {
                val presetsArray = root.getJSONArray("customPresets")
                val current = _presets.value.toMutableList()
                for (i in 0 until presetsArray.length()) {
                    val pObj = presetsArray.getJSONObject(i)
                    val p = Preset(
                        id = UUID.randomUUID().toString(),
                        name = pObj.optString("name", "Imported Preset"),
                        description = pObj.optString("description", "Imported configuration"),
                        isBuiltIn = false,
                        config = jsonToConfig(pObj.getJSONObject("config"))
                    )
                    current.add(p)
                }
                _presets.value = current
                saveCustomPresetsToPrefs()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun loadAllPresets() {
        val builtIn = createBuiltInPresets()
        val custom = loadCustomPresetsFromPrefs()
        _presets.value = builtIn + custom
    }

    private fun createBuiltInPresets(): List<Preset> {
        return listOf(
            Preset(
                id = "moto_5g_plus",
                name = "Moto 5G++ (Screenshot Style)",
                description = "Exact Moto status bar: 23.9 KB/s, VoNR, 5G++, Facebook, WhatsApp, Snapchat & vertical 64% battery",
                isBuiltIn = true,
                config = StatusBarConfig(
                    batteryDesign = BatteryIconDesign.VERTICAL_CAPSULE,
                    batteryFillStyle = BatteryFillStyle.SOLID,
                    percentageVisibility = BatteryPercentageVisibility.ALWAYS,
                    showBatteryPercentage = true,
                    showAppNotifications = true,
                    notificationIcons = listOf("facebook", "whatsapp", "snapchat", "snapchat", "dot"),
                    showNetworkSpeed = true,
                    networkSpeedValue = "23.9",
                    networkSpeedUnit = "KB/s",
                    showVoNr = true,
                    voNrText = "NR",
                    showNetworkBadge = true,
                    networkBadgeText = "5G++",
                    showTrafficArrows = true,
                    useTriangleSignal = true,
                    deviceProfile = DeviceProfile.MOTOROLA,
                    notchStyle = NotchStyle.CENTER_HOLE_PUNCH,
                    isEmojiBattery = false,
                    colorNormal = 0xFFFFFFFF,
                    textColor = 0xFFFFFFFF,
                    barBackgroundColor = 0x00000000,
                    barAlpha = 0f,
                    iconSize = 18f,
                    statusIconSize = 16f,
                    leftPadding = 16f,
                    rightPadding = 16f
                )
            ),
            Preset(
                id = "stock_android",
                name = "Stock Android",
                description = "Classic AOSP appearance with rectangular battery and clean layout",
                isBuiltIn = true,
                config = StatusBarConfig(
                    batteryDesign = BatteryIconDesign.CLASSIC,
                    batteryFillStyle = BatteryFillStyle.SOLID,
                    percentageVisibility = BatteryPercentageVisibility.ALWAYS,
                    deviceProfile = DeviceProfile.GENERIC,
                    notchStyle = NotchStyle.NONE,
                    iconSize = 19f,
                    iconThickness = 2.0f,
                    colorNormal = 0xFFFFFFFF,
                    barBackgroundColor = 0x00000000,
                    barAlpha = 0f
                )
            ),
            Preset(
                id = "pixel_style",
                name = "Pixel Style",
                description = "Google Pixel Material You aesthetic with center punch-hole clearance",
                isBuiltIn = true,
                config = StatusBarConfig(
                    batteryDesign = BatteryIconDesign.ROUNDED,
                    batteryFillStyle = BatteryFillStyle.SOLID,
                    percentageVisibility = BatteryPercentageVisibility.ALWAYS,
                    deviceProfile = DeviceProfile.PIXEL,
                    notchStyle = NotchStyle.CENTER_HOLE_PUNCH,
                    iconSize = 20f,
                    colorNormal = 0xFF38BDF8,
                    colorFull = 0xFF34D399,
                    colorLow = 0xFFF87171,
                    chargingAnimation = ChargingAnimationStyle.LIGHTNING_GLOW,
                    barBackgroundColor = 0x1A000000,
                    barAlpha = 0.1f
                )
            ),
            Preset(
                id = "motorola_inspired",
                name = "Motorola-inspired",
                description = "Moto signature circular ring battery with left clock and spaced icons",
                isBuiltIn = true,
                config = StatusBarConfig(
                    batteryDesign = BatteryIconDesign.CIRCLE,
                    batteryFillStyle = BatteryFillStyle.SOLID,
                    percentageVisibility = BatteryPercentageVisibility.ALWAYS,
                    deviceProfile = DeviceProfile.MOTOROLA,
                    notchStyle = NotchStyle.CENTER_HOLE_PUNCH,
                    iconSize = 22f,
                    colorNormal = 0xFF00E5FF,
                    iconSpacing = 8f,
                    leftPadding = 18f,
                    rightPadding = 18f,
                    barBackgroundColor = 0x00000000
                )
            ),
            Preset(
                id = "minimal",
                name = "Minimal",
                description = "Ultra clean single horizontal bar battery with outlined icons",
                isBuiltIn = true,
                config = StatusBarConfig(
                    batteryDesign = BatteryIconDesign.MINIMAL,
                    batteryFillStyle = BatteryFillStyle.SOLID,
                    percentageVisibility = BatteryPercentageVisibility.HIDE,
                    iconStyle = StatusIconStyle.OUTLINED,
                    deviceProfile = DeviceProfile.GENERIC,
                    notchStyle = NotchStyle.NONE,
                    iconSize = 16f,
                    iconSpacing = 5f,
                    colorNormal = 0xFFE2E8F0,
                    barBackgroundColor = 0x00000000,
                    barAlpha = 0f
                )
            ),
            Preset(
                id = "ios_inspired",
                name = "iOS-inspired",
                description = "Rounded battery with percentage inside and dynamic island notch layout",
                isBuiltIn = true,
                config = StatusBarConfig(
                    batteryDesign = BatteryIconDesign.PERCENT_INSIDE,
                    batteryFillStyle = BatteryFillStyle.SOLID,
                    percentageVisibility = BatteryPercentageVisibility.ALWAYS,
                    deviceProfile = DeviceProfile.GENERIC,
                    notchStyle = NotchStyle.PILL_ISLAND,
                    iconSize = 24f,
                    iconThickness = 2.4f,
                    colorNormal = 0xFF10B981,
                    colorCharging = 0xFF22C55E,
                    timeFormat24Hour = false,
                    isClockOnLeft = true,
                    barBackgroundColor = 0x22000000,
                    barAlpha = 0.15f
                )
            ),
            Preset(
                id = "gaming",
                name = "Gaming",
                description = "Cyberpunk neon aesthetic with lightning animation and electric accents",
                isBuiltIn = true,
                config = StatusBarConfig(
                    batteryDesign = BatteryIconDesign.CHARGING_ANIMATION,
                    batteryFillStyle = BatteryFillStyle.SEGMENTED,
                    percentageVisibility = BatteryPercentageVisibility.ALWAYS,
                    iconStyle = StatusIconStyle.NEON,
                    deviceProfile = DeviceProfile.ONEPLUS,
                    notchStyle = NotchStyle.LEFT_HOLE_PUNCH,
                    iconSize = 22f,
                    colorLow = 0xFFFF0055,
                    colorNormal = 0xFF00FFA3,
                    colorFull = 0xFF00E5FF,
                    colorCharging = 0xFFFFE600,
                    chargingAnimation = ChargingAnimationStyle.SWEEP,
                    barBackgroundColor = 0xE60A0A14,
                    barAlpha = 0.9f,
                    textColor = 0xFF00FFA3
                )
            ),
            Preset(
                id = "amoled",
                name = "AMOLED",
                description = "Deep pitch black background (#000000) for OLED battery savings",
                isBuiltIn = true,
                config = StatusBarConfig(
                    batteryDesign = BatteryIconDesign.ROUNDED,
                    batteryFillStyle = BatteryFillStyle.SOLID,
                    percentageVisibility = BatteryPercentageVisibility.ALWAYS,
                    deviceProfile = DeviceProfile.SAMSUNG,
                    notchStyle = NotchStyle.CENTER_HOLE_PUNCH,
                    iconSize = 20f,
                    colorNormal = 0xFF4ADE80,
                    barBackgroundColor = 0xFF000000,
                    barAlpha = 1.0f,
                    textColor = 0xFFFFFFFF
                )
            ),
            Preset(
                id = "transparent",
                name = "Transparent",
                description = "Crystal clear overlay that seamlessly blends with any wallpaper",
                isBuiltIn = true,
                config = StatusBarConfig(
                    batteryDesign = BatteryIconDesign.ROUNDED,
                    batteryFillStyle = BatteryFillStyle.SOLID,
                    percentageVisibility = BatteryPercentageVisibility.ALWAYS,
                    deviceProfile = DeviceProfile.PIXEL,
                    notchStyle = NotchStyle.CENTER_HOLE_PUNCH,
                    iconSize = 20f,
                    colorNormal = 0xFFFFFFFF,
                    barBackgroundColor = 0x00000000,
                    barAlpha = 0.0f,
                    textColor = 0xFFFFFFFF
                )
            )
        )
    }

    private fun saveConfigToPrefs(config: StatusBarConfig) {
        val json = configToJson(config).toString()
        prefs.edit().putString("active_config_json", json).apply()
    }

    private fun loadSavedConfig(): StatusBarConfig {
        val json = prefs.getString("active_config_json", null) ?: return StatusBarConfig()
        return try {
            jsonToConfig(JSONObject(json))
        } catch (e: Exception) {
            StatusBarConfig()
        }
    }

    private fun saveCustomPresetsToPrefs() {
        val customPresets = _presets.value.filter { !it.isBuiltIn }
        val array = JSONArray()
        for (p in customPresets) {
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("name", p.name)
            obj.put("description", p.description)
            obj.put("config", configToJson(p.config))
            array.put(obj)
        }
        prefs.edit().putString("custom_presets_json", array.toString()).apply()
    }

    private fun loadCustomPresetsFromPrefs(): List<Preset> {
        val json = prefs.getString("custom_presets_json", null) ?: return emptyList()
        val list = mutableListOf<Preset>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Preset(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        description = obj.optString("description", ""),
                        isBuiltIn = false,
                        config = jsonToConfig(obj.getJSONObject("config"))
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun configToJson(c: StatusBarConfig): JSONObject {
        val obj = JSONObject()
        obj.put("batteryDesign", c.batteryDesign.name)
        obj.put("batteryFillStyle", c.batteryFillStyle.name)
        obj.put("percentageVisibility", c.percentageVisibility.name)
        obj.put("iconSize", c.iconSize.toDouble())
        obj.put("iconThickness", c.iconThickness.toDouble())
        obj.put("colorLow", c.colorLow)
        obj.put("colorNormal", c.colorNormal)
        obj.put("colorFull", c.colorFull)
        obj.put("colorCharging", c.colorCharging)
        obj.put("chargingAnimation", c.chargingAnimation.name)

        obj.put("showBattery", c.showBattery)
        obj.put("showBatteryPercentage", c.showBatteryPercentage)
        obj.put("showTime", c.showTime)
        obj.put("showWifi", c.showWifi)
        obj.put("showMobileSignal", c.showMobileSignal)
        obj.put("showBluetooth", c.showBluetooth)
        obj.put("showAlarm", c.showAlarm)
        obj.put("showNotificationDots", c.showNotificationDots)
        obj.put("showDnd", c.showDnd)
        obj.put("showLocation", c.showLocation)
        obj.put("showVpn", c.showVpn)

        obj.put("timeFormat24Hour", c.timeFormat24Hour)
        obj.put("showTimeSeconds", c.showTimeSeconds)
        obj.put("isClockOnLeft", c.isClockOnLeft)
        obj.put("iconStyle", c.iconStyle.name)
        obj.put("statusIconSize", c.statusIconSize.toDouble())
        obj.put("iconSpacing", c.iconSpacing.toDouble())
        obj.put("statusBarHeight", c.statusBarHeight.toDouble())
        obj.put("leftPadding", c.leftPadding.toDouble())
        obj.put("rightPadding", c.rightPadding.toDouble())

        obj.put("barBackgroundColor", c.barBackgroundColor)
        obj.put("barAlpha", c.barAlpha.toDouble())
        obj.put("textColor", c.textColor)

        obj.put("deviceProfile", c.deviceProfile.name)
        obj.put("notchStyle", c.notchStyle.name)

        // Emoji Battery
        obj.put("isEmojiBattery", c.isEmojiBattery)
        obj.put("emojiThemeId", c.emojiThemeId)
        obj.put("emojiMascot", c.emojiMascot)
        obj.put("emojiSecondary", c.emojiSecondary)
        obj.put("emojiBatteryFillColor", c.emojiBatteryFillColor)
        obj.put("showStatusEmojis", c.showStatusEmojis)
        val statusEmojisArr = JSONArray()
        c.statusEmojis.forEach { statusEmojisArr.put(it) }
        obj.put("statusEmojis", statusEmojisArr)
        obj.put("customSignalColor", c.customSignalColor)
        obj.put("customNetworkType", c.customNetworkType)

        // Screenshot Elements: Notifications, Speed, VoNR, 5G++, Triangle signal
        obj.put("showAppNotifications", c.showAppNotifications)
        val notifArr = JSONArray()
        c.notificationIcons.forEach { notifArr.put(it) }
        obj.put("notificationIcons", notifArr)
        obj.put("showNetworkSpeed", c.showNetworkSpeed)
        obj.put("networkSpeedValue", c.networkSpeedValue)
        obj.put("networkSpeedUnit", c.networkSpeedUnit)
        obj.put("showVoNr", c.showVoNr)
        obj.put("voNrText", c.voNrText)
        obj.put("showNetworkBadge", c.showNetworkBadge)
        obj.put("networkBadgeText", c.networkBadgeText)
        obj.put("showTrafficArrows", c.showTrafficArrows)
        obj.put("useTriangleSignal", c.useTriangleSignal)

        return obj
    }

    private fun jsonToConfig(obj: JSONObject): StatusBarConfig {
        val statusEmojisList = mutableListOf<String>()
        val emojisArr = obj.optJSONArray("statusEmojis")
        if (emojisArr != null) {
            for (i in 0 until emojisArr.length()) {
                statusEmojisList.add(emojisArr.optString(i))
            }
        } else {
            statusEmojisList.addAll(listOf("🔇", "😎", "🐱"))
        }

        val notifList = mutableListOf<String>()
        val notifArr = obj.optJSONArray("notificationIcons")
        if (notifArr != null) {
            for (i in 0 until notifArr.length()) {
                notifList.add(notifArr.optString(i))
            }
        } else {
            notifList.addAll(listOf("facebook", "whatsapp", "snapchat", "snapchat", "dot"))
        }

        return StatusBarConfig(
            batteryDesign = safeValueOf(obj.optString("batteryDesign"), BatteryIconDesign.ROUNDED),
            batteryFillStyle = safeValueOf(obj.optString("batteryFillStyle"), BatteryFillStyle.SOLID),
            percentageVisibility = safeValueOf(obj.optString("percentageVisibility"), BatteryPercentageVisibility.ALWAYS),
            iconSize = obj.optDouble("iconSize", 20.0).toFloat(),
            iconThickness = obj.optDouble("iconThickness", 2.0).toFloat(),
            colorLow = obj.optLong("colorLow", 0xFFEF4444),
            colorNormal = obj.optLong("colorNormal", 0xFF38BDF8),
            colorFull = obj.optLong("colorFull", 0xFF10B981),
            colorCharging = obj.optLong("colorCharging", 0xFFF59E0B),
            chargingAnimation = safeValueOf(obj.optString("chargingAnimation"), ChargingAnimationStyle.LIGHTNING_GLOW),

            showBattery = obj.optBoolean("showBattery", true),
            showBatteryPercentage = obj.optBoolean("showBatteryPercentage", true),
            showTime = obj.optBoolean("showTime", true),
            showWifi = obj.optBoolean("showWifi", true),
            showMobileSignal = obj.optBoolean("showMobileSignal", true),
            showBluetooth = obj.optBoolean("showBluetooth", true),
            showAlarm = obj.optBoolean("showAlarm", false),
            showNotificationDots = obj.optBoolean("showNotificationDots", true),
            showDnd = obj.optBoolean("showDnd", false),
            showLocation = obj.optBoolean("showLocation", false),
            showVpn = obj.optBoolean("showVpn", false),

            timeFormat24Hour = obj.optBoolean("timeFormat24Hour", false),
            showTimeSeconds = obj.optBoolean("showTimeSeconds", false),
            isClockOnLeft = obj.optBoolean("isClockOnLeft", true),
            iconStyle = safeValueOf(obj.optString("iconStyle"), StatusIconStyle.FILLED),
            statusIconSize = obj.optDouble("statusIconSize", 16.0).toFloat(),
            iconSpacing = obj.optDouble("iconSpacing", 6.0).toFloat(),
            statusBarHeight = obj.optDouble("statusBarHeight", 32.0).toFloat(),
            leftPadding = obj.optDouble("leftPadding", 16.0).toFloat(),
            rightPadding = obj.optDouble("rightPadding", 16.0).toFloat(),

            barBackgroundColor = obj.optLong("barBackgroundColor", 0x00000000),
            barAlpha = obj.optDouble("barAlpha", 0.0).toFloat(),
            textColor = obj.optLong("textColor", 0xFFFFFFFF),

            deviceProfile = safeValueOf(obj.optString("deviceProfile"), DeviceProfile.PIXEL),
            notchStyle = safeValueOf(obj.optString("notchStyle"), NotchStyle.CENTER_HOLE_PUNCH),

            // Emoji Battery
            isEmojiBattery = obj.optBoolean("isEmojiBattery", true),
            emojiThemeId = obj.optString("emojiThemeId", "cute_bunny_love"),
            emojiMascot = obj.optString("emojiMascot", "🐰"),
            emojiSecondary = obj.optString("emojiSecondary", "🥕"),
            emojiBatteryFillColor = obj.optLong("emojiBatteryFillColor", 0xFFFF6584),
            showStatusEmojis = obj.optBoolean("showStatusEmojis", true),
            statusEmojis = statusEmojisList,
            customSignalColor = obj.optLong("customSignalColor", 0xFFFF4B6E),
            customNetworkType = obj.optString("customNetworkType", "3G"),

            // Screenshot Elements
            showAppNotifications = obj.optBoolean("showAppNotifications", true),
            notificationIcons = notifList,
            showNetworkSpeed = obj.optBoolean("showNetworkSpeed", true),
            networkSpeedValue = obj.optString("networkSpeedValue", "23.9"),
            networkSpeedUnit = obj.optString("networkSpeedUnit", "KB/s"),
            showVoNr = obj.optBoolean("showVoNr", true),
            voNrText = obj.optString("voNrText", "NR"),
            showNetworkBadge = obj.optBoolean("showNetworkBadge", true),
            networkBadgeText = obj.optString("networkBadgeText", "5G++"),
            showTrafficArrows = obj.optBoolean("showTrafficArrows", true),
            useTriangleSignal = obj.optBoolean("useTriangleSignal", true)
        )
    }

    private inline fun <reified T : Enum<T>> safeValueOf(value: String?, default: T): T {
        if (value.isNullOrBlank()) return default
        return try {
            java.lang.Enum.valueOf(T::class.java, value)
        } catch (e: Exception) {
            default
        }
    }
}
