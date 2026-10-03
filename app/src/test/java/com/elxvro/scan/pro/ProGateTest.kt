package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProGateTest {
    @Test
    fun freeUsersKeepCoreScannerAndQrCreation() {
        assertTrue(ProGate.canUse(ProFeature.SCANNER, ProEntitlement.Free))
        assertTrue(ProGate.canUse(ProFeature.CORE_QR, ProEntitlement.Free))
    }

    @Test
    fun freeUsersCannotUsePremiumFeatures() {
        val premium = listOf(
            ProFeature.OWN_LOGO,
            ProFeature.QR_CARD,
            ProFeature.HIGH_RES_EXPORT,
            ProFeature.PREMIUM_STYLE
        )

        premium.forEach { feature ->
            assertFalse(ProGate.canUse(feature, ProEntitlement.Free))
        }
    }

    @Test
    fun acknowledgedProEntitlementUnlocksEveryFeature() {
        ProFeature.entries.forEach { feature ->
            assertTrue(ProGate.canUse(feature, ProEntitlement.Pro))
        }
    }

    @Test
    fun pendingUnknownAndBillingErrorNeverUnlockPremiumFeatures() {
        val nonProStates = listOf(
            ProEntitlement.Pending,
            ProEntitlement.Unknown,
            ProEntitlement.Error("offline")
        )

        nonProStates.forEach { state ->
            assertFalse(ProGate.canUse(ProFeature.QR_CARD, state))
            assertTrue(ProGate.canUse(ProFeature.SCANNER, state))
        }
    }
}
