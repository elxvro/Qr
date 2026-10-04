package com.elxvro.scan.qrcard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCardV30LayoutPolicyTest {
    @Test
    fun everyProfessionalLayoutKeepsQrSeparateFromTextAndInsideCanvas() {
        QrCardDesignPreset.entries.forEach { preset ->
            val ratio = preset.aspectPreset.resolve(QrCardTemplate.MINIMAL)
            val width = if (ratio >= 1f) 1600 else (1600 * ratio).toInt()
            val height = if (ratio >= 1f) (1600 / ratio).toInt() else 1600
            val layout = QrCardV3LayoutPolicy.resolve(width, height, preset)

            assertFalse("${preset.name}: qr/text overlap", layout.qrRect.overlaps(layout.textRect))
            assertFalse("${preset.name}: qr/cta overlap", layout.qrRect.overlaps(layout.ctaRect))
            assertTrue("${preset.name}: qr invalid", layout.qrRect.width > 0 && layout.qrRect.height > 0)
            assertTrue("${preset.name}: text invalid", layout.textRect.width > 0 && layout.textRect.height > 0)
            assertTrue("${preset.name}: image invalid", layout.imageRect.width > 0 && layout.imageRect.height > 0)
            assertTrue("${preset.name}: cta invalid", layout.ctaRect.width > 0 && layout.ctaRect.height > 0)
            listOf(layout.qrRect, layout.textRect, layout.imageRect, layout.ctaRect).forEach {
                assertTrue(it.left >= 0f)
                assertTrue(it.top >= 0f)
                assertTrue(it.right <= width)
                assertTrue(it.bottom <= height)
            }
        }
    }

    @Test
    fun theTwoDesignsWithinEachRatioHaveDifferentGeometry() {
        listOf(
            QrCardAspectPreset.SQUARE,
            QrCardAspectPreset.WIDE,
            QrCardAspectPreset.CARD,
            QrCardAspectPreset.PORTRAIT,
            QrCardAspectPreset.STORY
        ).forEach { ratioPreset ->
            val pair = QrCardDesignCatalog.forAspect(ratioPreset)
            val ratio = ratioPreset.resolve(QrCardTemplate.MINIMAL)
            val width = if (ratio >= 1f) 1600 else (1600 * ratio).toInt()
            val height = if (ratio >= 1f) (1600 / ratio).toInt() else 1600
            val first = QrCardV3LayoutPolicy.resolve(width, height, pair[0])
            val second = QrCardV3LayoutPolicy.resolve(width, height, pair[1])

            assertNotEquals(ratioPreset.name, first.signature(), second.signature())
        }
    }

    private fun QrCardV3Layout.signature(): String =
        listOf(qrRect, textRect, imageRect, ctaRect)
            .joinToString("|") { "${it.left.toInt()},${it.top.toInt()},${it.right.toInt()},${it.bottom.toInt()}" }
}
