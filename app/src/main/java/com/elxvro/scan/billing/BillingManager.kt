package com.elxvro.scan.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BillingManager(
    context: Context,
    private val cache: EntitlementCache
) : PurchasesUpdatedListener {
    private val appContext = context.applicationContext

    private val _entitlement = MutableStateFlow<ProEntitlement>(ProEntitlement.Unknown)
    val entitlement: StateFlow<ProEntitlement> = _entitlement.asStateFlow()

    private val _offers = MutableStateFlow<List<SubscriptionOffer>>(emptyList())
    val offers: StateFlow<List<SubscriptionOffer>> = _offers.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val productsByOfferToken = mutableMapOf<String, ProductDetails>()

    private val billingClient: BillingClient = BillingClient.newBuilder(appContext)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .enableAutoServiceReconnection()
        .build()

    fun start() {
        if (billingClient.isReady) {
            _isReady.value = true
            queryProducts()
            refreshPurchases()
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    _isReady.value = true
                    queryProducts()
                    refreshPurchases()
                } else {
                    _isReady.value = false
                    _entitlement.value = EntitlementPolicy.resolve(
                        BillingObservation.OFFLINE,
                        cache.hasVerifiedPro()
                    )
                }
            }

            override fun onBillingServiceDisconnected() {
                _isReady.value = false
                _entitlement.value = EntitlementPolicy.resolve(
                    BillingObservation.OFFLINE,
                    cache.hasVerifiedPro()
                )
            }
        })
    }

    fun refreshPurchases() {
        if (!billingClient.isReady) {
            _entitlement.value = EntitlementPolicy.resolve(
                BillingObservation.OFFLINE,
                cache.hasVerifiedPro()
            )
            return
        }
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        billingClient.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                processPurchases(purchases)
            } else {
                _entitlement.value = EntitlementPolicy.resolve(
                    BillingObservation.ERROR,
                    cache.hasVerifiedPro()
                )
            }
        }
    }

    fun restorePurchases() = refreshPurchases()

    fun launchPurchase(activity: Activity, offer: SubscriptionOffer): BillingResult {
        val details = productsByOfferToken[offer.offerToken]
            ?: return BillingResult.newBuilder()
                .setResponseCode(BillingClient.BillingResponseCode.ITEM_UNAVAILABLE)
                .setDebugMessage("Subscription offer is not currently available")
                .build()

        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .setOfferToken(offer.offerToken)
            .build()
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
            .build()
        return billingClient.launchBillingFlow(activity, params)
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> processPurchases(purchases.orEmpty())
            BillingClient.BillingResponseCode.USER_CANCELED -> refreshPurchases()
            else -> _entitlement.value = EntitlementPolicy.resolve(
                BillingObservation.ERROR,
                cache.hasVerifiedPro()
            )
        }
    }

    private fun queryProducts() {
        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(BillingProducts.PRODUCT_ID)
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(product))
            .build()

        billingClient.queryProductDetailsAsync(params) { result, queryResult ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                _offers.value = emptyList()
                return@queryProductDetailsAsync
            }

            productsByOfferToken.clear()
            val raw = mutableListOf<RawSubscriptionOffer>()
            queryResult.productDetailsList.forEach { details ->
                if (details.productId != BillingProducts.PRODUCT_ID) return@forEach
                details.subscriptionOfferDetails
                    .orEmpty()
                    .sortedBy { if (it.offerId == null) 0 else 1 }
                    .forEach { offer ->
                        val phase = offer.pricingPhases.pricingPhaseList.lastOrNull() ?: return@forEach
                        productsByOfferToken[offer.offerToken] = details
                        raw += RawSubscriptionOffer(
                            basePlanId = offer.basePlanId,
                            offerToken = offer.offerToken,
                            formattedPrice = phase.formattedPrice,
                            priceAmountMicros = phase.priceAmountMicros,
                            priceCurrencyCode = phase.priceCurrencyCode
                        )
                    }
            }
            _offers.value = BillingOfferMapper.mapOffers(raw).all()
        }
    }

    private fun processPurchases(allPurchases: List<Purchase>) {
        val purchases = allPurchases.filter { BillingProducts.PRODUCT_ID in it.products }
        val acknowledged = purchases.firstOrNull {
            it.purchaseState == Purchase.PurchaseState.PURCHASED && it.isAcknowledged
        }
        if (acknowledged != null) {
            cache.storeVerifiedPro()
            _entitlement.value = EntitlementPolicy.resolve(BillingObservation.ACTIVE_ACKNOWLEDGED, true)
            return
        }

        val purchased = purchases.firstOrNull { it.purchaseState == Purchase.PurchaseState.PURCHASED }
        if (purchased != null) {
            _entitlement.value = EntitlementPolicy.resolve(BillingObservation.ACTIVE_UNACKNOWLEDGED, cache.hasVerifiedPro())
            acknowledge(purchased)
            return
        }

        if (purchases.any { it.purchaseState == Purchase.PurchaseState.PENDING }) {
            _entitlement.value = EntitlementPolicy.resolve(BillingObservation.PENDING, cache.hasVerifiedPro())
            return
        }

        cache.clearVerifiedPro()
        _entitlement.value = EntitlementPolicy.resolve(BillingObservation.NONE, false)
    }

    private fun acknowledge(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { result ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                cache.storeVerifiedPro()
                _entitlement.value = ProEntitlement.Pro()
            } else {
                _entitlement.value = ProEntitlement.Error("Satın alma doğrulanamadı")
            }
        }
    }

    fun close() {
        if (billingClient.isReady) billingClient.endConnection()
    }
}
