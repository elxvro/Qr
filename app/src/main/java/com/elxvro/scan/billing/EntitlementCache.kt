package com.elxvro.scan.billing

import android.content.Context

class EntitlementCache(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(
        "elxvro_scan_billing",
        Context.MODE_PRIVATE
    )

    fun hasFreshVerifiedPro(nowEpochMillis: Long = System.currentTimeMillis()): Boolean {
        return EntitlementFreshness.isFresh(
            verified = prefs.getBoolean(KEY_VERIFIED_PRO, false),
            verifiedAtEpochMillis = prefs.getLong(KEY_VERIFIED_AT, 0L),
            nowEpochMillis = nowEpochMillis
        )
    }

    fun setVerifiedPro(value: Boolean, nowEpochMillis: Long = System.currentTimeMillis()) {
        prefs.edit().apply {
            putBoolean(KEY_VERIFIED_PRO, value)
            if (value) putLong(KEY_VERIFIED_AT, nowEpochMillis)
            else remove(KEY_VERIFIED_AT)
        }.apply()
    }

    private companion object {
        const val KEY_VERIFIED_PRO = "verified_pro"
        const val KEY_VERIFIED_AT = "verified_at_epoch_ms"
    }
}
