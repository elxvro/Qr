package com.elxvro.scan.billing

import org.junit.Assert.assertTrue
import org.junit.Test

class EntitlementPolicyTest {
    @Test
    fun acknowledgedConfiguredPurchaseGrantsPro() {
        val result = EntitlementPolicy.resolve(
            billingOk = true,
            purchase = PurchaseSnapshot(setOf(BillingProducts.PRODUCT_ID), PurchaseStatus.PURCHASED, acknowledged = true),
            cachedVerifiedPro = false
        )
        assertTrue(result is ProEntitlement.Pro)
    }

    @Test
    fun pendingPurchaseNeverGrantsPro() {
        val result = EntitlementPolicy.resolve(
            billingOk = true,
            purchase = PurchaseSnapshot(setOf(BillingProducts.PRODUCT_ID), PurchaseStatus.PENDING, acknowledged = false),
            cachedVerifiedPro = true
        )
        assertTrue(result is ProEntitlement.Pending)
    }

    @Test
    fun unacknowledgedPurchaseRemainsUnknownUntilAcknowledged() {
        val result = EntitlementPolicy.resolve(
            billingOk = true,
            purchase = PurchaseSnapshot(setOf(BillingProducts.PRODUCT_ID), PurchaseStatus.PURCHASED, acknowledged = false),
            cachedVerifiedPro = false
        )
        assertTrue(result is ProEntitlement.Unknown)
    }

    @Test
    fun missingOrExpiredPurchaseIsFree() {
        val result = EntitlementPolicy.resolve(
            billingOk = true,
            purchase = null,
            cachedVerifiedPro = true
        )
        assertTrue(result is ProEntitlement.Free)
    }

    @Test
    fun billingErrorUsesOnlyPreviouslyVerifiedProCache() {
        val withCache = EntitlementPolicy.resolve(
            billingOk = false,
            purchase = null,
            cachedVerifiedPro = true
        )
        val withoutCache = EntitlementPolicy.resolve(
            billingOk = false,
            purchase = null,
            cachedVerifiedPro = false
        )

        assertTrue(withCache is ProEntitlement.Pro)
        assertTrue(withoutCache is ProEntitlement.Error)
    }
}
