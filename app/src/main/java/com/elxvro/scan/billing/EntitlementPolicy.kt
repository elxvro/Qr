package com.elxvro.scan.billing

enum class BillingObservation {
    ACTIVE_ACKNOWLEDGED,
    ACTIVE_UNACKNOWLEDGED,
    PENDING,
    NONE,
    OFFLINE,
    ERROR
}

object EntitlementPolicy {
    fun resolve(observation: BillingObservation, cachedVerifiedPro: Boolean): ProEntitlement = when (observation) {
        BillingObservation.ACTIVE_ACKNOWLEDGED -> ProEntitlement.Pro()
        BillingObservation.ACTIVE_UNACKNOWLEDGED -> ProEntitlement.Free
        BillingObservation.PENDING -> ProEntitlement.Pending
        BillingObservation.NONE -> ProEntitlement.Free
        BillingObservation.OFFLINE -> if (cachedVerifiedPro) ProEntitlement.Pro("cache") else ProEntitlement.Free
        BillingObservation.ERROR -> ProEntitlement.Error("Google Play Billing kullanılamıyor")
    }
}
