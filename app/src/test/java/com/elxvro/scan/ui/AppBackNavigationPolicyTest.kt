package com.elxvro.scan.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class AppBackNavigationPolicyTest {
    @Test
    fun closesTopmostChildBeforeChangingRootTab() {
        val state = AppBackState(
            tab = AppTab.CREATE,
            showPaywall = true,
            showQrCard = true,
            showBarcodeCreate = true,
            showBatchScan = true
        )

        assertEquals(AppBackAction.CLOSE_PAYWALL, AppBackNavigationPolicy.resolve(state))
    }

    @Test
    fun closesEachChildScreenInsteadOfExitingApplication() {
        assertEquals(
            AppBackAction.CLOSE_QR_CARD,
            AppBackNavigationPolicy.resolve(AppBackState(tab = AppTab.SCAN, showQrCard = true))
        )
        assertEquals(
            AppBackAction.CLOSE_BARCODE_CREATE,
            AppBackNavigationPolicy.resolve(AppBackState(tab = AppTab.SCAN, showBarcodeCreate = true))
        )
        assertEquals(
            AppBackAction.CLOSE_BATCH_SCAN,
            AppBackNavigationPolicy.resolve(AppBackState(tab = AppTab.SCAN, showBatchScan = true))
        )
    }

    @Test
    fun nonScanRootTabsReturnToScannerBeforeExit() {
        listOf(AppTab.CREATE, AppTab.HISTORY, AppTab.SETTINGS).forEach { tab ->
            assertEquals(
                AppBackAction.GO_TO_SCAN,
                AppBackNavigationPolicy.resolve(AppBackState(tab = tab))
            )
        }
    }

    @Test
    fun scannerRootAllowsNormalSystemExit() {
        assertEquals(
            AppBackAction.ALLOW_SYSTEM_EXIT,
            AppBackNavigationPolicy.resolve(AppBackState(tab = AppTab.SCAN))
        )
    }
}
