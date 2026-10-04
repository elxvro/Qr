package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ProTestModeTest {
    @Test
    fun debugTestModeForcesProRegardlessOfBillingState() {
        assertSame(ProEntitlement.Pro, ProTestMode.resolve(ProEntitlement.Free, testMode = true))
        assertSame(ProEntitlement.Pro, ProTestMode.resolve(ProEntitlement.Unknown, testMode = true))
        assertSame(ProEntitlement.Pro, ProTestMode.resolve(ProEntitlement.Error("offline"), testMode = true))
    }

    @Test
    fun releaseModePreservesRealBillingEntitlement() {
        val free = ProTestMode.resolve(ProEntitlement.Free, testMode = false)
        val pending = ProTestMode.resolve(ProEntitlement.Pending, testMode = false)

        assertSame(ProEntitlement.Free, free)
        assertSame(ProEntitlement.Pending, pending)
    }
}
