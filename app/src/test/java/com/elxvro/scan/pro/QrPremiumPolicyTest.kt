package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement
import com.elxvro.scan.qrcard.LogoMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QrPremiumPolicyTest {
    @Test
    fun freeQrUsesOnlyElxvroLogo() {
        assertEquals(listOf(LogoMode.ELXVRO), QrPremiumPolicy.allowedLogoModes(ProEntitlement.Free))
        assertEquals(LogoMode.ELXVRO, QrPremiumPolicy.defaultLogoMode(ProEntitlement.Free))
    }

    @Test
    fun proCanUseElxvroCustomOrNoLogo() {
        assertEquals(
            listOf(LogoMode.ELXVRO, LogoMode.CUSTOM, LogoMode.NONE),
            QrPremiumPolicy.allowedLogoModes(ProEntitlement.Pro)
        )
    }

    @Test
    fun freeCanBrowseCardTemplatesButCannotEditOrExportCards() {
        assertTrue(QrPremiumPolicy.canBrowseQrCards(ProEntitlement.Free))
        assertFalse(QrPremiumPolicy.canEditQrCards(ProEntitlement.Free))
        assertFalse(QrPremiumPolicy.canExportQrCards(ProEntitlement.Free))
    }

    @Test
    fun exportSizeLimitTracksEntitlement() {
        assertEquals(900, QrPremiumPolicy.maxQrExportSize(ProEntitlement.Free))
        assertEquals(2048, QrPremiumPolicy.maxQrExportSize(ProEntitlement.Pro))
    }
}
