package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProAccessPolicyTest {
    @Test
    fun proEntitlementUnlocksPremiumFeatures() {
        ProFeature.entries.forEach { feature ->
            assertTrue(ProAccessPolicy.canUse(feature, ProEntitlement.Pro()))
        }
    }

    @Test
    fun freePendingUnknownAndErrorNeverUnlockPremium() {
        val states = listOf(
            ProEntitlement.Free,
            ProEntitlement.Pending,
            ProEntitlement.Unknown,
            ProEntitlement.Error("offline")
        )
        states.forEach { state ->
            ProFeature.entries.forEach { feature ->
                assertFalse(ProAccessPolicy.canUse(feature, state))
            }
        }
    }
}
