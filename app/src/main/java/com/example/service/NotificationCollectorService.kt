package com.example.service

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ActiveNotificationTracker {
    private val _activeIcons = MutableStateFlow<List<String>>(listOf("facebook", "whatsapp", "snapchat", "snapchat", "dot"))
    val activeIcons: StateFlow<List<String>> = _activeIcons.asStateFlow()

    private val _activeCount = MutableStateFlow(4)
    val activeCount: StateFlow<Int> = _activeCount.asStateFlow()

    fun updateNotifications(sbnList: Array<StatusBarNotification>?) {
        if (sbnList == null || sbnList.isEmpty()) {
            return
        }

        val detectedIcons = mutableListOf<String>()

        for (sbn in sbnList) {
            // Ignore ongoing or foreground service notifications like our own
            if ((sbn.notification.flags and Notification.FLAG_ONGOING_EVENT) != 0) {
                continue
            }

            val pkg = sbn.packageName.lowercase()
            when {
                pkg.contains("facebook") || pkg.contains("katana") -> detectedIcons.add("facebook")
                pkg.contains("whatsapp") -> detectedIcons.add("whatsapp")
                pkg.contains("snapchat") -> detectedIcons.add("snapchat")
                pkg.contains("instagram") -> detectedIcons.add("instagram")
                pkg.contains("orca") || pkg.contains("messenger") -> detectedIcons.add("messenger")
                pkg.contains("telegram") -> detectedIcons.add("telegram")
                pkg.contains("twitter") || pkg.contains("x.corp") -> detectedIcons.add("twitter")
                pkg.contains("youtube") -> detectedIcons.add("youtube")
                pkg.contains("gmail") || pkg.contains("email") -> detectedIcons.add("gmail")
                else -> {
                    // Other general app notification
                    if (detectedIcons.size < 5) {
                        detectedIcons.add("dot")
                    }
                }
            }
        }

        if (detectedIcons.isNotEmpty()) {
            // Include dot separator if multiple notifications
            if (detectedIcons.size >= 2 && !detectedIcons.contains("dot")) {
                detectedIcons.add("dot")
            }
            _activeIcons.value = detectedIcons.take(6)
            _activeCount.value = detectedIcons.size
        }
    }

    fun isNotificationAccessGranted(context: Context): Boolean {
        val packageName = context.packageName
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        return flat != null && flat.contains(packageName)
    }
}

class NotificationCollectorService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        try {
            ActiveNotificationTracker.updateNotifications(activeNotifications)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        try {
            ActiveNotificationTracker.updateNotifications(activeNotifications)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        try {
            ActiveNotificationTracker.updateNotifications(activeNotifications)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
