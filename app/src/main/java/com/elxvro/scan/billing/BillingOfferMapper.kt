package com.elxvro.scan.billing

data class RawSubscriptionOffer(
    val basePlanId: String,
    val offerToken: String,
    val formattedPrice: String,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String
)

data class SubscriptionOffer(
    val basePlanId: String,
    val offerToken: String,
    val formattedPrice: String,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String
)

data class SubscriptionCatalog(
    val monthly: SubscriptionOffer?,
    val yearly: SubscriptionOffer?
) {
    fun all(): List<SubscriptionOffer> = listOfNotNull(monthly, yearly)
}

data class PurchaseSnapshot(
    val purchased: Boolean,
    val pending: Boolean,
    val acknowledged: Boolean
)

object BillingOfferMapper {
    fun mapOffers(raw: List<RawSubscriptionOffer>): SubscriptionCatalog {
        fun find(basePlanId: String): SubscriptionOffer? = raw
            .firstOrNull { it.basePlanId == basePlanId }
            ?.let {
                SubscriptionOffer(
                    basePlanId = it.basePlanId,
                    offerToken = it.offerToken,
                    formattedPrice = it.formattedPrice,
                    priceAmountMicros = it.priceAmountMicros,
                    priceCurrencyCode = it.priceCurrencyCode
                )
            }

        return SubscriptionCatalog(
            monthly = find(BillingProducts.MONTHLY_BASE_PLAN_ID),
            yearly = find(BillingProducts.YEARLY_BASE_PLAN_ID)
        )
    }

    fun mapEntitlement(snapshot: PurchaseSnapshot): ProEntitlement = when {
        snapshot.pending -> ProEntitlement.Pending
        snapshot.purchased && snapshot.acknowledged -> ProEntitlement.Pro()
        else -> ProEntitlement.Free
    }
}
