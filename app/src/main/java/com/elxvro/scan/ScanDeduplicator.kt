package com.elxvro.scan

class ScanDeduplicator(private val cooldownMs: Long = 1500L) {
    private var lastValue: String? = null
    private var lastAcceptedAt: Long = Long.MIN_VALUE

    @Synchronized
    fun shouldAccept(value: String, nowMs: Long): Boolean {
        val normalized = value.trim()
        if (normalized.isEmpty()) return false
        val sameValue = normalized == lastValue
        val insideCooldown = sameValue && nowMs - lastAcceptedAt < cooldownMs
        if (insideCooldown) return false
        lastValue = normalized
        lastAcceptedAt = nowMs
        return true
    }

    @Synchronized
    fun reset() {
        lastValue = null
        lastAcceptedAt = Long.MIN_VALUE
    }
}
