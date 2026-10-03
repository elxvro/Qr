package com.elxvro.scan.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BillingRepository(context: Context) {
    private val cache = EntitlementCache(context)
    private val _entitlement = MutableStateFlow<ProEntitlement>(ProEntitlement.Unknown)
    private val _offers = MutableStateFlow<List<SubscriptionOffer>>(emptyList())

    val entitlement: StateFlow<ProEntitlement> = _entitlement.asStateFlow()
    val offers: StateFlow<List<SubscriptionOffer>> = _offers.asStateFlow()

    private val manager = BillingManager(
        context = context.applicationContext,
        onOffers = { _offers.value = it },
        onPurchaseSnapshot = ::applySnapshot
    )

    fun start() = manager.start()

    fun refreshPurchases() = manager.refreshPurchases()

    fun restorePurchases() {
        _entitlement.value = ProEntitlement.Unknown
        manager.refreshPurchases()
    }

    fun launchPurchase(activity: Activity, offer: SubscriptionOffer) {
        val result = manager.launchPurchase(activity, offer)
        if (result == null || result.responseCode != BillingClient.BillingResponseCode.OK) {
            _entitlement.value = EntitlementPolicy.resolve(
                billingOk = false,
                purchase = null,
                cachedVerifiedPro = cache.hasFreshVerifiedPro()
            )
        }
    }

    fun close() = manager.close()

    private fun applySnapshot(billingOk: Boolean, snapshot: PurchaseSnapshot?) {
        val resolved = EntitlementPolicy.resolve(
            billingOk = billingOk,
            purchase = snapshot,
            cachedVerifiedPro = cache.hasFreshVerifiedPro()
        )
        if (billingOk) {
            val verified = resolved is ProEntitlement.Pro &&
                snapshot?.purchaseStatus == PurchaseStatus.PURCHASED &&
                snapshot.acknowledged
            cache.setVerifiedPro(verified)
        }
        _entitlement.value = resolved
    }
}
