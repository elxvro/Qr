package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCardTemplateTest {
    @Test
    fun templatesExposeStableDisplayOrder() {
        assertEquals(
            listOf(
                QrCardTemplate.MINIMAL,
                QrCardTemplate.CORPORATE,
                QrCardTemplate.WIFI,
                QrCardTemplate.SOCIAL,
                QrCardTemplate.EVENT,
                QrCardTemplate.BUSINESS,
                QrCardTemplate.PROMO,
                QrCardTemplate.TICKET
            ),
            QrCardTemplate.entries
        )
    }

    @Test
    fun minimalTemplateUsesLightProfessionalDefaults() {
        val defaults = QrCardTemplate.MINIMAL.defaults()

        assertEquals(0xFFFFFFFF.toInt(), defaults.cardBackgroundArgb)
        assertEquals(0xFF111820.toInt(), defaults.textArgb)
        assertEquals(QrPosition.CENTER, defaults.qrPosition)
        assertTrue(defaults.cardAspectRatio > 1f)
    }

    @Test
    fun corporateTemplateUsesDarkBrandSurface() {
        val defaults = QrCardTemplate.CORPORATE.defaults()

        assertEquals(0xFF090E15.toInt(), defaults.cardBackgroundArgb)
        assertEquals(0xFFF8FAFC.toInt(), defaults.textArgb)
        assertEquals(0xFF00A3FF.toInt(), defaults.accentArgb)
    }

    @Test
    fun everyTemplateKeepsSafeQrDefaults() {
        QrCardTemplate.entries.forEach { template ->
            val defaults = template.defaults()
            assertEquals(4, defaults.quietZoneModules)
            assertTrue(defaults.logoScaleFraction <= 0.20f)
        }
    }
}
