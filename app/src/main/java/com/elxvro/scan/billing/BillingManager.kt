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
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsResult
import com.android.billingclient.api.QueryPurchasesParams

class BillingManager(
    context: Context,
    private val onOffers: (List<SubscriptionOffer>) -> Unit,
    private val onPurchaseSnapshot: (Boolean, PurchaseSnapshot?) -> Unit
) {
    private var productDetails: ProductDetails? = null

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> processPurchases(purchases.orEmpty())
            BillingClient.BillingResponseCode.USER_CANCELED -> refreshPurchases()
            else -> onPurchaseSnapshot(false, null)
        }
    }

    private val billingClient = BillingClient.newBuilder(context.applicationContext)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .enableAutoServiceReconnection()
        .build()

    fun start() {
        if (billingClient.isReady) {
            queryOffers()
            refreshPurchases()
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryOffers()
                    refreshPurchases()
                } else {
                    onOffers(emptyList())
                    onPurchaseSnapshot(false, null)
                }
            }

            override fun onBillingServiceDisconnected() {
                onPurchaseSnapshot(false, null)
            }
        })
    }

    fun refreshPurchases() {
        if (!billingClient.isReady) {
            start()
            return
        }
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        billingClient.queryPurchasesAsync(
            params,
            object : PurchasesResponseListener {
                override fun onQueryPurchasesResponse(
                    billingResult: BillingResult,
                    purchases: List<Purchase>
                ) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        processPurchases(purchases)
                    } else {
                        onPurchaseSnapshot(false, null)
                    }
                }
            }
        )
    }

    fun launchPurchase(activity: Activity, offer: SubscriptionOffer): BillingResult? {
        val details = productDetails ?: return null
        if (!billingClient.isReady) return null
        val params = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .setOfferToken(offer.offerToken)
            .build()
        return billingClient.launchBillingFlow(
            activity,
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(params))
                .build()
        )
    }

    fun close() {
        billingClient.endConnection()
    }

    private fun queryOffers() {
        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(BillingProducts.PRODUCT_ID)
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(product))
            .build()

        billingClient.queryProductDetailsAsync(
            params,
            object : ProductDetailsResponseListener {
                override fun onProductDetailsResponse(
                    billingResult: BillingResult,
                    result: QueryProductDetailsResult
                ) {
                    if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                        productDetails = null
                        onOffers(emptyList())
                        return
                    }
                    val details = result.productDetailsList.firstOrNull {
                        it.productId == BillingProducts.PRODUCT_ID
                    }
                    productDetails = details
                    val rawOffers = details?.subscriptionOfferDetails.orEmpty().mapNotNull { offer ->
                        val phase = offer.pricingPhases.pricingPhaseList.lastOrNull()
                            ?: return@mapNotNull null
                        RawSubscriptionOffer(
                            basePlanId = offer.basePlanId,
                            offerToken = offer.offerToken,
                            formattedPrice = phase.formattedPrice,
                            priceAmountMicros = phase.priceAmountMicros,
                            priceCurrencyCode = phase.priceCurrencyCode,
                            offerId = offer.offerId
                        )
                    }
                    onOffers(BillingOfferMapper.mapOffers(rawOffers))
                }
            }
        )
    }

    private fun processPurchases(purchases: List<Purchase>) {
        val candidates = purchases.filter { BillingProducts.PRODUCT_ID in it.products }
        val purchased = candidates.firstOrNull { it.purchaseState == Purchase.PurchaseState.PURCHASED }
        if (purchased != null) {
            if (purchased.isAcknowledged) {
                onPurchaseSnapshot(true, purchased.toSnapshot(PurchaseStatus.PURCHASED, true))
            } else {
                acknowledge(purchased)
            }
            return
        }
        val pending = candidates.firstOrNull { it.purchaseState == Purchase.PurchaseState.PENDING }
        if (pending != null) {
            onPurchaseSnapshot(true, pending.toSnapshot(PurchaseStatus.PENDING, false))
            return
        }
        onPurchaseSnapshot(true, null)
    }

    private fun acknowledge(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { result ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                onPurchaseSnapshot(true, purchase.toSnapshot(PurchaseStatus.PURCHASED, true))
            } else {
                onPurchaseSnapshot(true, purchase.toSnapshot(PurchaseStatus.PURCHASED, false))
            }
        }
    }

    private fun Purchase.toSnapshot(status: PurchaseStatus, acknowledged: Boolean): PurchaseSnapshot {
        return PurchaseSnapshot(products.toSet(), status, acknowledged)
    }
}
