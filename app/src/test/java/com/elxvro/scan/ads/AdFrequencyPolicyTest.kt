package com.elxvro.scan.ads

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdFrequencyPolicyTest {
    @Test
    fun doesNotShowBeforeFourthEligibleAction() {
        assertFalse(AdFrequencyPolicy.shouldShow(3, 0L, 10_000L))
    }

    @Test
    fun fourthEligibleActionCanShowFirstInterstitial() {
        assertTrue(AdFrequencyPolicy.shouldShow(4, 0L, 10_000L))
    }

    @Test
    fun cooldownPreventsFrequentInterstitials() {
        val lastShown = 1_000L
        val tooSoon = lastShown + AdFrequencyPolicy.MIN_INTERVAL_MILLIS - 1L
        assertFalse(AdFrequencyPolicy.shouldShow(8, lastShown, tooSoon))
    }

    @Test
    fun interstitialCanShowAfterCooldown() {
        val lastShown = 1_000L
        val ready = lastShown + AdFrequencyPolicy.MIN_INTERVAL_MILLIS
        assertTrue(AdFrequencyPolicy.shouldShow(4, lastShown, ready))
    }
}
