package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement
import org.junit.Assert.assertEquals
import org.junit.Test

class QrBrandingPolicyTest {
    @Test
    fun freeUserAlwaysGetsElxvroLogo() {
        assertEquals(
            LogoMode.ELXVRO,
            QrBrandingPolicy.effectiveLogoMode(ProEntitlement.Free, LogoMode.CUSTOM)
        )
        assertEquals(
            LogoMode.ELXVRO,
            QrBrandingPolicy.effectiveLogoMode(ProEntitlement.Free, LogoMode.NONE)
        )
    }

    @Test
    fun proUserMayUseElxvroCustomOrNoLogo() {
        LogoMode.entries.forEach { requested ->
            assertEquals(
                requested,
                QrBrandingPolicy.effectiveLogoMode(ProEntitlement.Pro(), requested)
            )
        }
    }

    @Test
    fun highResolutionExportRequiresPro() {
        assertEquals(900, QrBrandingPolicy.maxExportSize(ProEntitlement.Free))
        assertEquals(2048, QrBrandingPolicy.maxExportSize(ProEntitlement.Pro()))
    }
}
