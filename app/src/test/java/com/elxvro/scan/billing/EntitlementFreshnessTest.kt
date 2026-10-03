package com.elxvro.scan.billing

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EntitlementFreshnessTest {
    private val now = 1_000_000_000L

    @Test
    fun verifiedProInsideOfflineGraceIsFresh() {
        val verifiedAt = now - EntitlementFreshness.DEFAULT_GRACE_MILLIS + 1L

        assertTrue(EntitlementFreshness.isFresh(true, verifiedAt, now))
    }

    @Test
    fun verifiedProPastOfflineGraceExpires() {
        val verifiedAt = now - EntitlementFreshness.DEFAULT_GRACE_MILLIS - 1L

        assertFalse(EntitlementFreshness.isFresh(true, verifiedAt, now))
    }

    @Test
    fun unverifiedEntitlementIsNeverFresh() {
        assertFalse(EntitlementFreshness.isFresh(false, now, now))
    }

    @Test
    fun futureVerificationTimestampIsRejected() {
        assertFalse(EntitlementFreshness.isFresh(true, now + 1L, now))
    }
}
