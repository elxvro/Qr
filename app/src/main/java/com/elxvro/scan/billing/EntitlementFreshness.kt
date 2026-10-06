package com.elxvro.scan.billing

object EntitlementFreshness {
    fun isFresh(
        verified: Boolean,
        verifiedAtEpochMillis: Long,
        nowEpochMillis: Long
    ): Boolean {
        if (!verified) return false
        if (verifiedAtEpochMillis <= 0L) return false
        if (nowEpochMillis < verifiedAtEpochMillis) return false
        return true
    }
}
