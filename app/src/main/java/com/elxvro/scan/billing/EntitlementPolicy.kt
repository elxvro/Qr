package com.elxvro.scan.billing

data class PurchaseSnapshot(
    val productIds: Set<String>,
    val purchaseStatus: PurchaseStatus,
    val acknowledged: Boolean
)

object EntitlementPolicy {
    fun resolve(
        billingOk: Boolean,
        purchase: PurchaseSnapshot?,
        cachedVerifiedPro: Boolean
    ): ProEntitlement {
        if (!billingOk) {
            return if (cachedVerifiedPro) ProEntitlement.Pro
            else ProEntitlement.Error("Google Play bağlantısı kullanılamıyor")
        }

        val current = purchase ?: return ProEntitlement.Free
        if (BillingProducts.PRODUCT_ID !in current.productIds) return ProEntitlement.Free
        return BillingOfferMapper.mapEntitlement(
            hasConfiguredProduct = true,
            purchaseStatus = current.purchaseStatus,
            acknowledged = current.acknowledged
        )
    }
}
