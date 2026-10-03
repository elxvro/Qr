package com.elxvro.scan.billing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BillingOfferMapperTest {
    @Test
    fun selectsMonthlyAndYearlyOffersUsingBasePlanIds() {
        val catalog = BillingOfferMapper.mapOffers(
            listOf(
                RawSubscriptionOffer("monthly", "monthly-token", "₺49,99", 49_990_000L, "TRY"),
                RawSubscriptionOffer("yearly", "yearly-token", "₺399,99", 399_990_000L, "TRY"),
                RawSubscriptionOffer("legacy", "legacy-token", "₺1", 1_000_000L, "TRY")
            )
        )

        assertEquals("₺49,99", catalog.monthly?.formattedPrice)
        assertEquals("monthly-token", catalog.monthly?.offerToken)
        assertEquals("₺399,99", catalog.yearly?.formattedPrice)
        assertEquals("yearly-token", catalog.yearly?.offerToken)
    }

    @Test
    fun missingBasePlanIsLeftAbsentWithoutInventingPrice() {
        val catalog = BillingOfferMapper.mapOffers(
            listOf(RawSubscriptionOffer("monthly", "token", "€2.99", 2_990_000L, "EUR"))
        )

        assertEquals("€2.99", catalog.monthly?.formattedPrice)
        assertNull(catalog.yearly)
    }

    @Test
    fun pendingPurchaseNeverGrantsPro() {
        val entitlement = BillingOfferMapper.mapEntitlement(
            PurchaseSnapshot(purchased = false, pending = true, acknowledged = false)
        )

        assertTrue(entitlement is ProEntitlement.Pending)
    }

    @Test
    fun acknowledgedPurchasedSubscriptionGrantsPro() {
        val entitlement = BillingOfferMapper.mapEntitlement(
            PurchaseSnapshot(purchased = true, pending = false, acknowledged = true)
        )

        assertTrue(entitlement is ProEntitlement.Pro)
    }

    @Test
    fun unacknowledgedPurchaseDoesNotGrantProYet() {
        val entitlement = BillingOfferMapper.mapEntitlement(
            PurchaseSnapshot(purchased = true, pending = false, acknowledged = false)
        )

        assertTrue(entitlement is ProEntitlement.Free)
    }
}
