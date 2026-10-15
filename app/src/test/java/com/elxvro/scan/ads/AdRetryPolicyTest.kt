package com.elxvro.scan.ads

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AdRetryPolicyTest {
    @Test
    fun retryDelaysUseBoundedBackoff() {
        assertEquals(15_000L, AdRetryPolicy.delayForAttempt(0))
        assertEquals(30_000L, AdRetryPolicy.delayForAttempt(1))
        assertEquals(60_000L, AdRetryPolicy.delayForAttempt(2))
        assertEquals(120_000L, AdRetryPolicy.delayForAttempt(3))
        assertNull(AdRetryPolicy.delayForAttempt(4))
        assertNull(AdRetryPolicy.delayForAttempt(-1))
    }
}
