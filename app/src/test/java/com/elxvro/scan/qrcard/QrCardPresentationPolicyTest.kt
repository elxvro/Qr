package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class QrCardPresentationPolicyTest {
    @Test
    fun eachTemplateHasDistinctPresentationSignature() {
        val signatures = QrCardTemplate.entries.map { template ->
            val p = QrCardPresentationPolicy.forTemplate(template)
            "${p.accentStyle}:${p.titleAlignment}:${p.qrFrameStyle}"
        }

        assertEquals(QrCardTemplate.entries.size, signatures.toSet().size)
    }

    @Test
    fun socialUsesCenteredProfilePresentation() {
        val p = QrCardPresentationPolicy.forTemplate(QrCardTemplate.SOCIAL)

        assertEquals(QrCardAccentStyle.PROFILE_RING, p.accentStyle)
        assertEquals(QrCardTextAlignment.CENTER, p.titleAlignment)
    }

    @Test
    fun eventUsesDedicatedBandPresentation() {
        val p = QrCardPresentationPolicy.forTemplate(QrCardTemplate.EVENT)

        assertEquals(QrCardAccentStyle.EVENT_BAND, p.accentStyle)
        assertNotEquals(QrCardPresentationPolicy.forTemplate(QrCardTemplate.MINIMAL).accentStyle, p.accentStyle)
    }
}
