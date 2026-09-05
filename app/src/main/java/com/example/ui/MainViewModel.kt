package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PresetRepository
import com.example.model.*
import com.example.service.ActiveNotificationTracker
import com.example.service.StatusBarOverlayService
import com.example.util.BatteryMonitor
import com.example.util.SystemStatusMonitor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PresetRepository(application)

    val activeConfig: StateFlow<StatusBarConfig> = repository.activeConfig
    val presets: StateFlow<List<Preset>> = repository.presets

    private val _simulation = MutableStateFlow(SimulationState())
    val simulation: StateFlow<SimulationState> = _simulation.asStateFlow()

    private val _isOverlayGranted = MutableStateFlow(false)
    val isOverlayGranted: StateFlow<Boolean> = _isOverlayGranted.asStateFlow()

    private val _isOverlayActive = MutableStateFlow(repository.isOverlayEnabled() && Settings.canDrawOverlays(application))
    val isOverlayActive: StateFlow<Boolean> = _isOverlayActive.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        checkOverlayPermission()
        observeBatteryIfDesired()
        observeLiveTimeTicker()
        observeLiveNetworkStatus()
        observeLiveTrafficSpeed()
        observeLiveNotifications()
    }

    fun checkOverlayPermission() {
        val granted = Settings.canDrawOverlays(getApplication())
        _isOverlayGranted.value = granted
        if (granted && repository.isOverlayEnabled() && !_isOverlayActive.value) {
            StatusBarOverlayService.start(getApplication())
            _isOverlayActive.value = true
        } else if (!granted && _isOverlayActive.value) {
            _isOverlayActive.value = false
            repository.setOverlayEnabled(false)
        }
    }

    private fun observeBatteryIfDesired() {
        viewModelScope.launch {
            BatteryMonitor.observeBattery(getApplication()).collectLatest { batInfo ->
                if (_simulation.value.useRealBattery) {
                    _simulation.value = _simulation.value.copy(
                        batteryLevel = batInfo.level,
                        isCharging = batInfo.isCharging
                    )
                }
            }
        }
    }

    private fun observeLiveTimeTicker() {
        viewModelScope.launch {
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
                    "10:03"
                }
                _simulation.value = _simulation.value.copy(simulatedTime = formatted)
                delay(if (showSec) 1000L else 10000L)
            }
        }
    }

    private fun observeLiveNetworkStatus() {
        viewModelScope.launch {
            SystemStatusMonitor.observeNetworkStatus(getApplication()).collectLatest { netStatus ->
                if (_simulation.value.useRealNetworkData) {
                    _simulation.value = _simulation.value.copy(
                        isWifiOn = netStatus.isWifiConnected,
                        wifiStrength = netStatus.wifiStrength,
                        signalStrength = netStatus.cellularStrength,
                        liveNetworkBadge = netStatus.networkTypeBadge,
                        liveVoBadge = netStatus.voBadge
                    )
                }
            }
        }
    }

    private fun observeLiveTrafficSpeed() {
        viewModelScope.launch {
            SystemStatusMonitor.observeNetworkSpeed().collectLatest { traffic ->
                if (_simulation.value.useRealNetworkData) {
                    _simulation.value = _simulation.value.copy(
                        liveNetworkSpeed = traffic.speedValue,
                        liveNetworkUnit = traffic.speedUnit,
                        liveIsDataTransferring = traffic.isTransferringUp || traffic.isTransferringDown
                    )
                }
            }
        }
    }

    private fun observeLiveNotifications() {
        viewModelScope.launch {
            ActiveNotificationTracker.activeIcons.collectLatest { notifIcons ->
                if (_simulation.value.useRealNetworkData && notifIcons.isNotEmpty()) {
                    _simulation.value = _simulation.value.copy(
                        liveNotificationIcons = notifIcons,
                        notificationsCount = notifIcons.size
                    )
                }
            }
        }
    }

    fun updateConfig(newConfig: StatusBarConfig) {
        repository.updateConfig(newConfig)
    }

    fun updateSimulation(newSimulation: SimulationState) {
        _simulation.value = newSimulation
    }

    fun selectPreset(preset: Preset) {
        repository.updateConfig(preset.config)
        _statusMessage.value = "Applied preset: ${preset.name}"
    }

    fun saveCurrentAsPreset(name: String, description: String) {
        val created = repository.savePreset(name, description, activeConfig.value)
        _statusMessage.value = "Saved preset: ${created.name}"
    }

    fun renamePreset(id: String, newName: String) {
        repository.renamePreset(id, newName)
        _statusMessage.value = "Preset renamed to: $newName"
    }

    fun duplicatePreset(id: String) {
        repository.duplicatePreset(id)
        _statusMessage.value = "Preset duplicated"
    }

    fun deletePreset(id: String) {
        val success = repository.deletePreset(id)
        if (success) {
            _statusMessage.value = "Preset deleted"
        }
    }

    fun selectDeviceProfile(profile: DeviceProfile) {
        val current = activeConfig.value
        val updated = when (profile) {
            DeviceProfile.MOTOROLA -> current.copy(
                deviceProfile = profile,
                batteryDesign = BatteryIconDesign.CIRCLE,
                notchStyle = NotchStyle.CENTER_HOLE_PUNCH,
                iconSpacing = 8f,
                leftPadding = 18f,
                rightPadding = 18f
            )
            DeviceProfile.SAMSUNG -> current.copy(
                deviceProfile = profile,
                batteryDesign = BatteryIconDesign.ROUNDED,
                notchStyle = NotchStyle.CENTER_HOLE_PUNCH,
                iconSpacing = 5f,
                leftPadding = 14f,
                rightPadding = 14f
            )
            DeviceProfile.XIAOMI -> current.copy(
                deviceProfile = profile,
                batteryDesign = BatteryIconDesign.PERCENT_INSIDE,
                notchStyle = NotchStyle.LEFT_HOLE_PUNCH,
                iconSpacing = 6f
            )
            DeviceProfile.ONEPLUS -> current.copy(
                deviceProfile = profile,
                batteryDesign = BatteryIconDesign.CIRCLE,
                notchStyle = NotchStyle.LEFT_HOLE_PUNCH,
                iconSpacing = 7f
            )
            DeviceProfile.PIXEL -> current.copy(
                deviceProfile = profile,
                batteryDesign = BatteryIconDesign.ROUNDED,
                notchStyle = NotchStyle.CENTER_HOLE_PUNCH,
                iconSpacing = 6f,
                leftPadding = 16f,
                rightPadding = 16f
            )
            DeviceProfile.GENERIC -> current.copy(
                deviceProfile = profile,
                batteryDesign = BatteryIconDesign.CLASSIC,
                notchStyle = NotchStyle.NONE,
                iconSpacing = 6f
            )
        }
        updateConfig(updated)
        _statusMessage.value = "Switched profile to ${profile.displayName}"
    }

    fun toggleOverlay(context: Context) {
        if (!_isOverlayGranted.value) {
            _statusMessage.value = "Overlay permission required. Tap below to grant."
            return
        }
        val shouldStart = !_isOverlayActive.value
        if (shouldStart) {
            StatusBarOverlayService.start(context)
            _isOverlayActive.value = true
            repository.setOverlayEnabled(true)
            _statusMessage.value = "Custom status bar applied to your main screen!"
        } else {
            StatusBarOverlayService.stop(context)
            _isOverlayActive.value = false
            repository.setOverlayEnabled(false)
            _statusMessage.value = "Status bar overlay disabled"
        }
    }

    fun resetToDefaults() {
        repository.resetToDefaults()
        _statusMessage.value = "Reset configuration to defaults"
    }

    fun exportConfiguration(): String {
        return repository.exportConfigurationJson()
    }

    fun importConfiguration(json: String): Boolean {
        val success = repository.importConfigurationJson(json)
        if (success) {
            _statusMessage.value = "Configuration imported successfully"
        } else {
            _statusMessage.value = "Failed to parse configuration JSON"
        }
        return success
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
