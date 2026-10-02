package com.elxvro.scan

import android.content.Context
import android.content.SharedPreferences

data class ScanSettings(
    val sound: Boolean = true,
    val vibrate: Boolean = true,
    val defaultTorch: Boolean = false,
    val quickStart: Boolean = true,
    val autoCopy: Boolean = false,
    val safeAutoOpen: Boolean = false,
    val duplicateDelayMs: Long = 1500L
)

class AppPrefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun snapshot(): ScanSettings = ScanSettings(
        sound = prefs.getBoolean(KEY_SOUND, true),
        vibrate = prefs.getBoolean(KEY_VIBRATE, true),
        defaultTorch = prefs.getBoolean(KEY_DEFAULT_TORCH, false),
        quickStart = prefs.getBoolean(KEY_QUICK_START, true),
        autoCopy = prefs.getBoolean(KEY_AUTO_COPY, false),
        safeAutoOpen = prefs.getBoolean(KEY_SAFE_AUTO_OPEN, false),
        duplicateDelayMs = prefs.getLong(KEY_DUPLICATE_DELAY, 1500L).coerceIn(500L, 5000L)
    )

    fun setSound(value: Boolean) = edit(KEY_SOUND, value)
    fun setVibrate(value: Boolean) = edit(KEY_VIBRATE, value)
    fun setDefaultTorch(value: Boolean) = edit(KEY_DEFAULT_TORCH, value)
    fun setQuickStart(value: Boolean) = edit(KEY_QUICK_START, value)
    fun setAutoCopy(value: Boolean) = edit(KEY_AUTO_COPY, value)
    fun setSafeAutoOpen(value: Boolean) = edit(KEY_SAFE_AUTO_OPEN, value)
    fun setDuplicateDelay(value: Long) {
        prefs.edit().putLong(KEY_DUPLICATE_DELAY, value.coerceIn(500L, 5000L)).apply()
    }

    private fun edit(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    companion object {
        private const val FILE = "elxvro_scan"
        private const val KEY_SOUND = "sound"
        private const val KEY_VIBRATE = "vibrate"
        private const val KEY_DEFAULT_TORCH = "default_torch"
        private const val KEY_QUICK_START = "quick_start"
        private const val KEY_AUTO_COPY = "auto_copy"
        private const val KEY_SAFE_AUTO_OPEN = "safe_auto_open"
        private const val KEY_DUPLICATE_DELAY = "duplicate_delay_ms"
    }
}
