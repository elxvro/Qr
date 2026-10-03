package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProPaywallPolicyTest {
    @Test
    fun freeUserCanStartPurchase() {
        assertTrue(ProPaywallPolicy.canStartPurchase(ProEntitlement.Free))
    }

    @Test
    fun activeProCannotStartAnotherPurchase() {
        assertFalse(ProPaywallPolicy.canStartPurchase(ProEntitlement.Pro))
    }

    @Test
    fun pendingPurchaseCannotStartAnotherPurchase() {
        assertFalse(ProPaywallPolicy.canStartPurchase(ProEntitlement.Pending))
    }

    @Test
    fun unknownOrErrorStateCannotStartPurchase() {
        assertFalse(ProPaywallPolicy.canStartPurchase(ProEntitlement.Unknown))
        assertFalse(ProPaywallPolicy.canStartPurchase(ProEntitlement.Error("offline")))
    }
}
