package com.elxvro.scan.ui

data class AppBackState(
    val tab: AppTab,
    val showPaywall: Boolean = false,
    val showQrCard: Boolean = false,
    val showBarcodeCreate: Boolean = false,
    val showBatchScan: Boolean = false
)

enum class AppBackAction {
    CLOSE_PAYWALL,
    CLOSE_QR_CARD,
    CLOSE_BARCODE_CREATE,
    CLOSE_BATCH_SCAN,
    GO_TO_SCAN,
    ALLOW_SYSTEM_EXIT
}

object AppBackNavigationPolicy {
    fun resolve(state: AppBackState): AppBackAction = when {
        state.showPaywall -> AppBackAction.CLOSE_PAYWALL
        state.showQrCard -> AppBackAction.CLOSE_QR_CARD
        state.showBarcodeCreate -> AppBackAction.CLOSE_BARCODE_CREATE
        state.showBatchScan -> AppBackAction.CLOSE_BATCH_SCAN
        state.tab != AppTab.SCAN -> AppBackAction.GO_TO_SCAN
        else -> AppBackAction.ALLOW_SYSTEM_EXIT
    }
}
