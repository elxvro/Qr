package com.elxvro.scan.billing

import android.content.Context

class EntitlementCache(context: Context) {
    private val prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun hasVerifiedPro(): Boolean = prefs.getBoolean(KEY_VERIFIED_PRO, false)

    fun storeVerifiedPro() {
        prefs.edit()
            .putBoolean(KEY_VERIFIED_PRO, true)
            .putLong(KEY_VERIFIED_AT, System.currentTimeMillis())
            .apply()
    }

    fun clearVerifiedPro() {
        prefs.edit().remove(KEY_VERIFIED_PRO).remove(KEY_VERIFIED_AT).apply()
    }

    companion object {
        private const val FILE = "elxvro_scan_billing"
        private const val KEY_VERIFIED_PRO = "verified_pro"
        private const val KEY_VERIFIED_AT = "verified_at"
    }
}
