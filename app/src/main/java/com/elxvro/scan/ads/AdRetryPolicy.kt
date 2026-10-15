package com.elxvro.scan.ads

object AdRetryPolicy {
    private val delaysMillis = longArrayOf(
        15_000L,
        30_000L,
        60_000L,
        120_000L
    )

    fun delayForAttempt(attempt: Int): Long? {
        if (attempt < 0 || attempt >= delaysMillis.size) return null
        return delaysMillis[attempt]
    }
}
