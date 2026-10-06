package com.elxvro.scan.billing

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EntitlementFreshnessTest {
    private val now = 1_000_000_000L

    @Test
    fun verifiedLifetimeProRemainsFreshOffline() {
        val verifiedLongAgo = 1L

        assertTrue(EntitlementFreshness.isFresh(true, verifiedLongAgo, now))
    }

    @Test
    fun unverifiedEntitlementIsNeverFresh() {
        assertFalse(EntitlementFreshness.isFresh(false, now, now))
    }

    @Test
    fun missingVerificationTimestampIsRejected() {
        assertFalse(EntitlementFreshness.isFresh(true, 0L, now))
    }

    @Test
    fun futureVerificationTimestampIsRejected() {
        assertFalse(EntitlementFreshness.isFresh(true, now + 1L, now))
    }
}
