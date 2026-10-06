package com.elxvro.scan.billing

sealed interface ProEntitlement {
    data object Unknown : ProEntitlement
    data object Free : ProEntitlement
    data object Pending : ProEntitlement
    data object Pro : ProEntitlement
    data class Error(val message: String) : ProEntitlement
}

enum class PurchaseStatus {
    PURCHASED,
    PENDING,
    NONE
}

data class RawOneTimeOffer(
    val offerToken: String?,
    val formattedPrice: String,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String
)

data class OneTimePurchaseOffer(
    val offerToken: String,
    val formattedPrice: String,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String
)
