package com.elxvro.scan.scanner

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusAssistPolicyTest {
    @Test
    fun refocusesAfterLongPeriodWithoutDetection() {
        assertTrue(
            FocusAssistPolicy.shouldRefocus(
                nowMs = 5_000L,
                lastDetectionAtMs = 0L,
                lastFocusAtMs = 0L
            )
        )
    }

    @Test
    fun doesNotRefocusImmediatelyAfterSuccessfulDetection() {
        assertFalse(
            FocusAssistPolicy.shouldRefocus(
                nowMs = 5_000L,
                lastDetectionAtMs = 4_200L,
                lastFocusAtMs = 0L
            )
        )
    }

    @Test
    fun throttlesRepeatedRefocusAttempts() {
        assertFalse(
            FocusAssistPolicy.shouldRefocus(
                nowMs = 8_000L,
                lastDetectionAtMs = 0L,
                lastFocusAtMs = 6_500L
            )
        )
    }
}
