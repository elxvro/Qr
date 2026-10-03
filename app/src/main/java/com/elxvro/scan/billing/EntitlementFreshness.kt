package com.elxvro.scan.billing

object EntitlementFreshness {
    const val DEFAULT_GRACE_MILLIS: Long = 72L * 60L * 60L * 1000L

    fun isFresh(
        verified: Boolean,
        verifiedAtEpochMillis: Long,
        nowEpochMillis: Long,
        graceMillis: Long = DEFAULT_GRACE_MILLIS
    ): Boolean {
        if (!verified) return false
        if (verifiedAtEpochMillis <= 0L) return false
        if (graceMillis < 0L) return false
        if (nowEpochMillis < verifiedAtEpochMillis) return false
        return nowEpochMillis - verifiedAtEpochMillis <= graceMillis
    }
}
