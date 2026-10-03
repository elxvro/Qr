package com.elxvro.scan.scanner

object LumaSamplingPolicy {
    const val SAMPLE_INTERVAL_MS = 450L

    fun shouldSample(nowMs: Long, lastSampleAtMs: Long): Boolean {
        if (lastSampleAtMs == Long.MIN_VALUE) return true
        return nowMs - lastSampleAtMs >= SAMPLE_INTERVAL_MS
    }
}
