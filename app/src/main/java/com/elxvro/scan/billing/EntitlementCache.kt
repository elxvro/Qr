package com.elxvro.scan.billing

import android.content.Context

class EntitlementCache(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(
        "elxvro_scan_billing",
        Context.MODE_PRIVATE
    )

    fun hasVerifiedPro(): Boolean = prefs.getBoolean(KEY_VERIFIED_PRO, false)

    fun setVerifiedPro(value: Boolean) {
        prefs.edit().putBoolean(KEY_VERIFIED_PRO, value).apply()
    }

    private companion object {
        const val KEY_VERIFIED_PRO = "verified_pro"
    }
}
