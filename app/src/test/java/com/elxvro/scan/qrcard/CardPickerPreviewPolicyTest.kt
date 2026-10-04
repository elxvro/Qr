package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CardPickerPreviewPolicyTest {
    @Test
    fun thumbnailUsesRealQrAndCompactLongEdge() {
        val spec = CardPickerPreviewPolicy.thumbnailSpec()
        assertEquals(320, spec.longEdge)
        assertTrue(spec.showQr)
        assertTrue(spec.showText)
        assertTrue(spec.showCta)
    }
}
