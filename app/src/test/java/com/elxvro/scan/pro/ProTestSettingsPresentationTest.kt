package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ProTestSettingsPresentationTest {
    @Test
    fun testModeClearlyLabelsPlanAndHidesBillingActions() {
        val state = ProSettingsPresentation.from(
            entitlement = ProEntitlement.Pro,
            testMode = true
        )

        assertEquals("PRO TEST aktif", state.statusLabel)
        assertFalse(state.showUpgrade)
        assertFalse(state.showManage)
        assertFalse(state.showRestore)
        assertFalse(state.showRetry)
    }
}
