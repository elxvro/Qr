package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfessionalEditableLayoutPolicyTest {
    @Test
    fun proFreeform_keepsQrInsideCardAndClampsScale() {
        val preset = ProfessionalCardCatalog.defaultPro
        val model = QrCardModel(
            payload = "https://elxvro.com",
            backgroundPresetId = preset.id,
            cardAspectRatio = 1.586f,
            freeformEnabled = true,
            qrAnchorX = 0.01f,
            qrAnchorY = 0.99f,
            qrScale = 4f
        )

        val layout = ProfessionalEditableLayoutPolicy.resolve(
            width = 1200,
            height = 760,
            model = model,
            preset = preset,
            usePhotoLayout = true
        )

        assertTrue(layout.qrRect.left >= 0f)
        assertTrue(layout.qrRect.top >= 0f)
        assertTrue(layout.qrRect.right <= 1200f)
        assertTrue(layout.qrRect.bottom <= 760f)
        assertTrue(layout.qrRect.width <= 760f * 0.62f + 1f)
    }

    @Test
    fun freeCard_ignoresFreeformCoordinates() {
        val preset = ProfessionalCardCatalog.defaultFree
        val model = QrCardModel(
            payload = "https://elxvro.com",
            backgroundPresetId = preset.id,
            cardAspectRatio = 1.586f,
            freeformEnabled = true,
            qrAnchorX = 0.08f,
            qrAnchorY = 0.90f,
            qrScale = 1.5f
        )
        val expected = ProfessionalPhotoLayoutEngine.resolve(1200, 760, preset.artVariant)
        val actual = ProfessionalEditableLayoutPolicy.resolve(
            width = 1200,
            height = 760,
            model = model,
            preset = preset,
            usePhotoLayout = true
        )

        assertEquals(expected.qrRect, actual.qrRect)
        assertEquals(expected.titleRect, actual.titleRect)
    }
}
