package com.elxvro.scan.billing

object BillingOfferMapper {
    fun selectOneTimeOffer(rawOffers: List<RawOneTimeOffer>): OneTimePurchaseOffer? {
        return rawOffers
            .minWithOrNull(
                compareBy<RawOneTimeOffer> { it.priceAmountMicros }
                    .thenBy { it.offerToken }
            )
            ?.let {
                OneTimePurchaseOffer(
                    offerToken = it.offerToken,
                    formattedPrice = it.formattedPrice,
                    priceAmountMicros = it.priceAmountMicros,
                    priceCurrencyCode = it.priceCurrencyCode
                )
            }
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
