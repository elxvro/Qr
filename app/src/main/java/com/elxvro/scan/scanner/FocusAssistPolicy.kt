package com.elxvro.scan.scanner

object FocusAssistPolicy {
    const val NO_RESULT_BEFORE_REFOCUS_MS = 2_500L
    const val MIN_BETWEEN_REFOCUS_MS = 3_000L

    fun shouldRefocus(
        nowMs: Long,
        lastDetectionAtMs: Long,
        lastFocusAtMs: Long
    ): Boolean {
        if (nowMs < 0L) return false
        val sinceDetection = nowMs - lastDetectionAtMs
        val sinceFocus = nowMs - lastFocusAtMs
        return sinceDetection >= NO_RESULT_BEFORE_REFOCUS_MS &&
            sinceFocus >= MIN_BETWEEN_REFOCUS_MS
    }
}
