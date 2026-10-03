package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCardAspectPresetTest {
    @Test
    fun exposesRequestedPremiumAspectRatios() {
        assertEquals(1.0f, QrCardAspectPreset.SQUARE.resolve(QrCardTemplate.MINIMAL))
        assertEquals(16f / 9f, QrCardAspectPreset.WIDE.resolve(QrCardTemplate.MINIMAL))
        assertEquals(1.586f, QrCardAspectPreset.CARD.resolve(QrCardTemplate.MINIMAL))
        assertEquals(0.8f, QrCardAspectPreset.PORTRAIT.resolve(QrCardTemplate.MINIMAL))
    }

    @Test
    fun defaultPresetTracksTemplateDefault() {
        assertEquals(
            QrCardTemplate.EVENT.defaults().cardAspectRatio,
            QrCardAspectPreset.DEFAULT.resolve(QrCardTemplate.EVENT)
        )
    }

    @Test
    fun everyTemplateAndPresetKeepsQrAndTextSeparated() {
        val width = 1600
        QrCardTemplate.entries.forEach { template ->
            QrCardAspectPreset.entries.forEach { preset ->
                val ratio = preset.resolve(template)
                val height = (width / ratio).toInt()
                val position = template.defaults().qrPosition
                val layout = QrCardLayoutPolicy.resolve(width, height, position)

                assertFalse(
                    "${template.name}/${preset.name} overlap",
                    layout.textRect.overlaps(layout.qrRect)
                )
                assertTrue(layout.textRect.width > 0f)
                assertTrue(layout.textRect.height > 0f)
            }
        }
    }
}
