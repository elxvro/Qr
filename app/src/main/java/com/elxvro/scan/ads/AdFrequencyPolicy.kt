package com.elxvro.scan.ads

object AdFrequencyPolicy {
    const val ACTIONS_PER_INTERSTITIAL = 4
    const val MIN_INTERVAL_MILLIS = 8L * 60L * 1000L

    fun shouldShow(
        eligibleActionCount: Int,
        lastShownAtMillis: Long,
        nowMillis: Long
    ): Boolean {
        if (eligibleActionCount < ACTIONS_PER_INTERSTITIAL) return false
        if (lastShownAtMillis <= 0L) return true
        if (nowMillis < lastShownAtMillis) return false
        return nowMillis - lastShownAtMillis >= MIN_INTERVAL_MILLIS
    }
}
