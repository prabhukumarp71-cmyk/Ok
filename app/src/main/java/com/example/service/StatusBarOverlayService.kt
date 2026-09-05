package com.example.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.NotificationCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.MainActivity
import com.example.R
import com.example.data.PresetRepository
import com.example.model.SimulationState
import com.example.model.StatusBarConfig
import com.example.ui.components.CustomStatusBarView
import com.example.util.BatteryMonitor
import com.example.util.RealBatteryInfo
import com.example.util.RealNetworkStatus
import com.example.util.RealTrafficSpeed
import com.example.util.SystemStatusMonitor
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StatusBarOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: ComposeView? = null
    private var lifecycleOwner: OverlayLifecycleOwner? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var repository: PresetRepository

    // Real-time reactive state streams for the main screen overlay
    private val currentBatteryState = MutableStateFlow(RealBatteryInfo(level = 85, isCharging = false))
    private val currentTimeState = MutableStateFlow("12:00")
    private val currentNetworkStatus = MutableStateFlow(RealNetworkStatus())
    private val currentTrafficSpeed = MutableStateFlow(RealTrafficSpeed(speedValue = "23.9", speedUnit = "KB/s"))

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        repository = PresetRepository(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            val notification = buildForegroundNotification()
            startForeground(NOTIFICATION_ID, notification)
        }

        if (Settings.canDrawOverlays(this)) {
            setupOverlayWindow()
        } else {
            stopSelf()
        }
    }

    private fun setupOverlayWindow() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 0

            // Ensure the overlay renders directly into notch and cutout areas on modern devices
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        // Initialize lifecycle and state registry owners for ComposeView
        val owner = OverlayLifecycleOwner()
        owner.onCreate()
        lifecycleOwner = owner

        overlayView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(owner)
            setViewTreeSavedStateRegistryOwner(owner)
            setViewTreeViewModelStoreOwner(owner)
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(owner)
            )

            setContent {
                val config by repository.activeConfig.collectAsState()
                val batteryInfo by currentBatteryState.collectAsState()
                val liveTime by currentTimeState.collectAsState()
                val netStatus by currentNetworkStatus.collectAsState()
                val trafficSpeed by currentTrafficSpeed.collectAsState()
                val liveNotifs by ActiveNotificationTracker.activeIcons.collectAsState()

                CustomStatusBarView(
                    config = config,
                    simulation = SimulationState(
                        batteryLevel = batteryInfo.level,
                        isCharging = batteryInfo.isCharging,
                        simulatedTime = liveTime,
                        useRealBattery = true,
                        useRealNetworkData = true,
                        isWifiOn = netStatus.isWifiConnected,
                        wifiStrength = netStatus.wifiStrength,
                        signalStrength = netStatus.cellularStrength,
                        liveNetworkSpeed = trafficSpeed.speedValue,
                        liveNetworkUnit = trafficSpeed.speedUnit,
                        liveNetworkBadge = netStatus.networkTypeBadge,
                        liveVoBadge = netStatus.voBadge,
                        liveIsDataTransferring = trafficSpeed.isTransferringUp || trafficSpeed.isTransferringDown,
                        liveNotificationIcons = liveNotifs
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        try {
            windowManager?.addView(overlayView, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Start live background observers for real battery status, live clock, network, and speed
        startBatteryMonitoring()
        startTimeTicker()
        startNetworkMonitoring()
        startTrafficSpeedMonitoring()
    }

    private fun startBatteryMonitoring() {
        serviceScope.launch {
            BatteryMonitor.observeBattery(applicationContext).collectLatest { batInfo ->
                currentBatteryState.value = batInfo
            }
        }
    }

    private fun startNetworkMonitoring() {
        serviceScope.launch {
            SystemStatusMonitor.observeNetworkStatus(applicationContext).collectLatest { status ->
                currentNetworkStatus.value = status
            }
        }
    }

    private fun startTrafficSpeedMonitoring() {
        serviceScope.launch {
            SystemStatusMonitor.observeNetworkSpeed().collectLatest { speed ->
                currentTrafficSpeed.value = speed
            }
        }
    }

    private fun startTimeTicker() {
        serviceScope.launch {
            while (isActive) {
                val config = repository.activeConfig.value
                val is24 = config.timeFormat24Hour
                val showSec = config.showTimeSeconds
                val pattern = when {
                    is24 && showSec -> "HH:mm:ss"
                    is24 -> "HH:mm"
                    showSec -> "hh:mm:ss"
                    else -> "hh:mm"
                }
                val formatted = try {
                    SimpleDateFormat(pattern, Locale.getDefault()).format(Date())
                } catch (e: Exception) {
                    "12:00"
                }
                currentTimeState.value = formatted

                delay(if (showSec) 1000L else 10000L)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Status Bar Overlay Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows custom status bar overlay on screen"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("StatusBar Studio")
            .setContentText("Custom status bar overlay active on main screen")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        lifecycleOwner?.onDestroy()
        lifecycleOwner = null
        if (overlayView != null && windowManager != null) {
            try {
                windowManager?.removeView(overlayView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            overlayView = null
        }
    }

    companion object {
        const val CHANNEL_ID = "statusbar_studio_overlay"
        const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, StatusBarOverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, StatusBarOverlayService::class.java)
            context.stopService(intent)
        }
    }
}

