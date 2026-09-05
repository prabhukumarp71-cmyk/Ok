package com.example.model

data class SimulationState(
    val batteryLevel: Int = 85,
    val isCharging: Boolean = false,
    val isWifiOn: Boolean = true,
    val wifiStrength: Int = 4, // 1 to 4
    val isBluetoothOn: Boolean = true,
    val signalStrength: Int = 4, // 1 to 4
    val notificationsCount: Int = 3,
    val isDndOn: Boolean = false,
    val isLocationOn: Boolean = false,
    val isVpnOn: Boolean = false,
    val isDarkModePreview: Boolean = true,
    val simulatedTime: String = "09:41",
    val useRealBattery: Boolean = true,
    val useRealNetworkData: Boolean = true,
    val liveNetworkSpeed: String = "23.9",
    val liveNetworkUnit: String = "KB/s",
    val liveNetworkBadge: String = "5G++",
    val liveVoBadge: String = "NR",
    val liveIsDataTransferring: Boolean = true,
    val liveNotificationIcons: List<String> = listOf("facebook", "whatsapp", "snapchat", "snapchat", "dot")
)
