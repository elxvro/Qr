package com.elxvro.scan.billing

object BillingOfferMapper {
    private val supportedPlanIds = setOf(
        BillingProducts.MONTHLY_BASE_PLAN_ID,
        BillingProducts.YEARLY_BASE_PLAN_ID
    )

    fun mapOffers(rawOffers: List<RawSubscriptionOffer>): List<SubscriptionOffer> {
        return rawOffers
            .asSequence()
            .filter { it.basePlanId in supportedPlanIds }
            .groupBy { it.basePlanId }
            .values
            .asSequence()
            .mapNotNull { offers ->
                offers.minByOrNull { if (it.offerId == null) 0 else 1 }
            }
            .map {
                SubscriptionOffer(
                    basePlanId = it.basePlanId,
                    offerToken = it.offerToken,
                    formattedPrice = it.formattedPrice,
                    priceAmountMicros = it.priceAmountMicros,
                    priceCurrencyCode = it.priceCurrencyCode
                )
            }
            .sortedBy { if (it.basePlanId == BillingProducts.MONTHLY_BASE_PLAN_ID) 0 else 1 }
            .toList()
    }

    fun mapEntitlement(
        hasConfiguredProduct: Boolean,
        purchaseStatus: PurchaseStatus,
        acknowledged: Boolean
    ): ProEntitlement {
        if (!hasConfiguredProduct) return ProEntitlement.Free
        return when (purchaseStatus) {
            PurchaseStatus.PENDING -> ProEntitlement.Pending
            PurchaseStatus.PURCHASED -> if (acknowledged) ProEntitlement.Pro else ProEntitlement.Unknown
            PurchaseStatus.NONE -> ProEntitlement.Free
        }
    }
}
