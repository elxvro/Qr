package com.elxvro.scan

import org.junit.Assert.assertEquals
import org.junit.Test

class UrlSafetyPresentationTest {
    @Test
    fun riskLabelsAreStableForTheResultSheet() {
        assertEquals("Düşük risk", UrlSafetyPresentation.label(UrlRiskLevel.LOW))
        assertEquals("Orta risk", UrlSafetyPresentation.label(UrlRiskLevel.MEDIUM))
        assertEquals("Yüksek risk", UrlSafetyPresentation.label(UrlRiskLevel.HIGH))
    }

    @Test
    fun reasonsHaveUserFacingTurkishMessages() {
        assertEquals(
            "Bağlantı şifreli HTTPS kullanmıyor.",
            UrlSafetyPresentation.reasonText(UrlRiskReason.INSECURE_HTTP)
        )
        assertEquals(
            "Bağlantı gerçek hedefi gizleyen bir URL kısaltıcı kullanıyor.",
            UrlSafetyPresentation.reasonText(UrlRiskReason.URL_SHORTENER)
        )
    }
}
