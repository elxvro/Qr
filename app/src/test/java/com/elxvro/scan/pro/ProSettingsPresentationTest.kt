package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProSettingsPresentationTest {
    @Test
    fun freeStateShowsUpgradeAndRestore() {
        val state = ProSettingsPresentation.from(ProEntitlement.Free)

        assertTrue(state.showUpgrade)
        assertTrue(state.showRestore)
        assertFalse(state.showManage)
        assertFalse(state.showRetry)
    }

    @Test
    fun proStateShowsManageAndRestore() {
        val state = ProSettingsPresentation.from(ProEntitlement.Pro)

        assertFalse(state.showUpgrade)
        assertTrue(state.showRestore)
        assertTrue(state.showManage)
        assertFalse(state.showRetry)
    }

    @Test
    fun billingErrorShowsRetryWithoutHidingRestore() {
        val state = ProSettingsPresentation.from(ProEntitlement.Error("offline"))

        assertTrue(state.showRetry)
        assertTrue(state.showRestore)
        assertFalse(state.showManage)
    }
}
