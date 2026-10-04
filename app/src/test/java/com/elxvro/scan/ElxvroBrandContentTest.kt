package com.elxvro.scan

import org.junit.Assert.assertTrue
import org.junit.Test

class ElxvroBrandContentTest {
    @Test
    fun aboutCopyPromotesBrandWithoutLosingProductMeaning() {
        assertTrue(ElxvroBrandContent.aboutText.contains("ELXVRO"))
        assertTrue(ElxvroBrandContent.aboutText.contains("QR", ignoreCase = true))
        assertTrue(ElxvroBrandContent.aboutText.contains("barkod", ignoreCase = true))
    }

    @Test
    fun privacyCopyKeepsLocalProcessingStatement() {
        assertTrue(ElxvroBrandContent.privacyText.contains("cihaz", ignoreCase = true))
        assertTrue(ElxvroBrandContent.privacyText.contains("sunucu", ignoreCase = true))
    }

    @Test
    fun termsCopyExplainsResponsibleUseAndBrand() {
        assertTrue(ElxvroBrandContent.termsText.contains("ELXVRO"))
        assertTrue(ElxvroBrandContent.termsText.contains("soruml", ignoreCase = true))
    }

    @Test
    fun supportedContentCopyIsExpandedByCategory() {
        val text = ElxvroBrandContent.supportedContentText
        assertTrue(text.contains("QR Code"))
        assertTrue(text.contains("Data Matrix"))
        assertTrue(text.contains("vCard"))
        assertTrue(text.contains("Wi-Fi"))
        assertTrue(text.contains("Takvim"))
        assertTrue(text.contains("ISBN"))
    }
}
