package com.elxvro.scan.billing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BillingOfferMapperTest {
    @Test
    fun mapsConfiguredMonthlyAndYearlyPlansInStableOrder() {
        val mapped = BillingOfferMapper.mapOffers(
            listOf(
                RawSubscriptionOffer("yearly", "year-token", "₺999,99", 999_990_000L, "TRY"),
                RawSubscriptionOffer("legacy", "legacy-token", "₺1,00", 1_000_000L, "TRY"),
                RawSubscriptionOffer("monthly", "month-token", "₺99,99", 99_990_000L, "TRY")
            )
        )

        assertEquals(listOf("monthly", "yearly"), mapped.map { it.basePlanId })
        assertEquals("₺99,99", mapped.first().formattedPrice)
        assertEquals("year-token", mapped.last().offerToken)
    }

    @Test
    fun missingBasePlanIsAllowedWithoutInventingPrice() {
        val mapped = BillingOfferMapper.mapOffers(
            listOf(RawSubscriptionOffer("monthly", "month-token", "€4.99", 4_990_000L, "EUR"))
        )

        assertEquals(1, mapped.size)
        assertEquals(BillingProducts.MONTHLY_BASE_PLAN_ID, mapped.single().basePlanId)
        assertEquals("€4.99", mapped.single().formattedPrice)
    }

    @Test
    fun basePlanOfferWinsOverPromotionalOfferForSamePlan() {
        val mapped = BillingOfferMapper.mapOffers(
            listOf(
                RawSubscriptionOffer(
                    basePlanId = "monthly",
                    offerToken = "trial-token",
                    formattedPrice = "₺0,00",
                    priceAmountMicros = 0L,
                    priceCurrencyCode = "TRY",
                    offerId = "trial"
                ),
                RawSubscriptionOffer(
                    basePlanId = "monthly",
                    offerToken = "base-token",
                    formattedPrice = "₺99,99",
                    priceAmountMicros = 99_990_000L,
                    priceCurrencyCode = "TRY",
                    offerId = null
                )
            )
        )

        assertEquals(1, mapped.size)
        assertEquals("base-token", mapped.single().offerToken)
        assertEquals("₺99,99", mapped.single().formattedPrice)
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
    fun purchasedSubscriptionRequiresAcknowledgementBeforePro() {
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
