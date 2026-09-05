package com.example.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class RealBatteryInfo(
    val level: Int,
    val isCharging: Boolean
)

object BatteryMonitor {
    fun observeBattery(context: Context): Flow<RealBatteryInfo> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (intent == null) return
                val rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

                val percentage = if (rawLevel >= 0 && scale > 0) {
                    ((rawLevel.toFloat() / scale.toFloat()) * 100).toInt()
                } else 100

                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL

                trySend(RealBatteryInfo(percentage, isCharging))
            }
        }

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val initialIntent = context.registerReceiver(receiver, filter)
        if (initialIntent != null) {
            val rawLevel = initialIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = initialIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val status = initialIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val percentage = if (rawLevel >= 0 && scale > 0) {
                ((rawLevel.toFloat() / scale.toFloat()) * 100).toInt()
            } else 100
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL
            trySend(RealBatteryInfo(percentage, isCharging))
        }

        awaitClose {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {
                // Ignore receiver not registered
            }
        }
    }
}
