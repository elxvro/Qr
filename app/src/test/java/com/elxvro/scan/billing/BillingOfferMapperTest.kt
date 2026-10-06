package com.elxvro.scan.billing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BillingOfferMapperTest {
    @Test
    fun selectsLowestEligibleOneTimeOffer() {
        val selected = BillingOfferMapper.selectOneTimeOffer(
            listOf(
                RawOneTimeOffer("standard-token", "₺199,99", 199_990_000L, "TRY"),
                RawOneTimeOffer("discount-token", "₺149,99", 149_990_000L, "TRY")
            )
        )

        assertEquals("discount-token", selected?.offerToken)
        assertEquals("₺149,99", selected?.formattedPrice)
        assertEquals("TRY", selected?.priceCurrencyCode)
    }

    @Test
    fun emptyOneTimeOfferListReturnsNull() {
        assertNull(BillingOfferMapper.selectOneTimeOffer(emptyList()))
    }

    @Test
    fun pendingPurchaseNeverGrantsPro() {
        val entitlement = BillingOfferMapper.mapEntitlement(
            hasConfiguredProduct = true,
            purchaseStatus = PurchaseStatus.PENDING,
            acknowledged = false
        )

        assertTrue(entitlement is ProEntitlement.Pending)
    }

    @Test
    fun purchasedLifetimeProductRequiresAcknowledgementBeforePro() {
        val unacknowledged = BillingOfferMapper.mapEntitlement(
            hasConfiguredProduct = true,
            purchaseStatus = PurchaseStatus.PURCHASED,
            acknowledged = false
        )
        val acknowledged = BillingOfferMapper.mapEntitlement(
            hasConfiguredProduct = true,
            purchaseStatus = PurchaseStatus.PURCHASED,
            acknowledged = true
        )

        assertTrue(unacknowledged is ProEntitlement.Unknown)
        assertTrue(acknowledged is ProEntitlement.Pro)
    }
}
