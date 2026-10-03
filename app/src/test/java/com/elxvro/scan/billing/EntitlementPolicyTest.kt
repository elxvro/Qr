package com.elxvro.scan.billing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EntitlementPolicyTest {
    @Test
    fun activeAcknowledgedPurchaseGrantsPro() {
        assertTrue(EntitlementPolicy.resolve(BillingObservation.ACTIVE_ACKNOWLEDGED, false) is ProEntitlement.Pro)
    }

    @Test
    fun activeButUnacknowledgedPurchaseDoesNotGrantPro() {
        assertTrue(EntitlementPolicy.resolve(BillingObservation.ACTIVE_UNACKNOWLEDGED, false) is ProEntitlement.Free)
    }

    @Test
    fun pendingPurchaseStaysPending() {
        assertTrue(EntitlementPolicy.resolve(BillingObservation.PENDING, false) is ProEntitlement.Pending)
    }

    @Test
    fun missingPurchaseMeansFreeEvenWhenOldCacheExists() {
        assertTrue(EntitlementPolicy.resolve(BillingObservation.NONE, true) is ProEntitlement.Free)
    }

    @Test
    fun offlineCanUsePreviouslyVerifiedProCache() {
        val value = EntitlementPolicy.resolve(BillingObservation.OFFLINE, true)
        assertEquals(ProEntitlement.Pro("cache"), value)
    }

    @Test
    fun offlineWithoutVerifiedCacheFallsBackToFree() {
        assertTrue(EntitlementPolicy.resolve(BillingObservation.OFFLINE, false) is ProEntitlement.Free)
    }

    @Test
    fun billingErrorDoesNotUnlockPremium() {
        assertTrue(EntitlementPolicy.resolve(BillingObservation.ERROR, true) is ProEntitlement.Error)
    }
}
