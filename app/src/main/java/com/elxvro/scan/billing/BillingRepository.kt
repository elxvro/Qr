package com.elxvro.scan.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingResult
import kotlinx.coroutines.flow.StateFlow

class BillingRepository(context: Context) : AutoCloseable {
    private val cache = EntitlementCache(context.applicationContext)
    private val manager = BillingManager(context.applicationContext, cache)

    val entitlement: StateFlow<ProEntitlement> = manager.entitlement
    val offers: StateFlow<List<SubscriptionOffer>> = manager.offers
    val isReady: StateFlow<Boolean> = manager.isReady

    init {
        manager.start()
    }

    fun refreshPurchases() = manager.refreshPurchases()

    fun restorePurchases() = manager.restorePurchases()

    fun launchPurchase(activity: Activity, offer: SubscriptionOffer): BillingResult =
        manager.launchPurchase(activity, offer)

    override fun close() = manager.close()
}
