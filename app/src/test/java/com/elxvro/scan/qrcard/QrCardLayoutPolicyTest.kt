package com.elxvro.scan.qrcard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCardLayoutPolicyTest {
    @Test
    fun landscapeCardKeepsTextAndQrSeparated() {
        val layout = QrCardLayoutPolicy.resolve(
            width = 1600,
            height = 1009,
            qrPosition = QrPosition.CENTER
        )

        assertFalse(layout.textRect.overlaps(layout.qrRect))
        assertTrue(layout.textRect.right < layout.qrRect.left)
        assertInside(layout, 1600, 1009)
    }

    @Test
    fun squareCardPlacesTextAboveCenteredQrWithoutOverlap() {
        val layout = QrCardLayoutPolicy.resolve(
            width = 1200,
            height = 1200,
            qrPosition = QrPosition.CENTER
        )

        assertFalse(layout.textRect.overlaps(layout.qrRect))
        assertTrue(layout.textRect.bottom < layout.qrRect.top)
        assertInside(layout, 1200, 1200)
    }

    @Test
    fun portraitEventCardKeepsTextAboveBottomQr() {
        val layout = QrCardLayoutPolicy.resolve(
            width = 1200,
            height = 1600,
            qrPosition = QrPosition.BOTTOM
        )

        assertFalse(layout.textRect.overlaps(layout.qrRect))
        assertTrue(layout.textRect.bottom < layout.qrRect.top)
        assertInside(layout, 1200, 1600)
    }

    @Test
    fun topQrMovesTextBelowQrOnStackedCards() {
        val layout = QrCardLayoutPolicy.resolve(
            width = 1200,
            height = 1200,
            qrPosition = QrPosition.TOP
        )

        assertFalse(layout.textRect.overlaps(layout.qrRect))
        assertTrue(layout.textRect.top > layout.qrRect.bottom)
        assertInside(layout, 1200, 1200)
    }

    @Test
    fun everySupportedTemplateAspectRatioHasUsableTextArea() {
        QrCardTemplate.entries.forEach { template ->
            val width = 1200
            val height = (width / template.defaults().cardAspectRatio).toInt()
            val layout = QrCardLayoutPolicy.resolve(width, height, template.defaults().qrPosition)

            assertTrue("${template.name} text width", layout.textRect.width >= width * 0.35f)
            assertTrue("${template.name} text height", layout.textRect.height >= height * 0.16f)
            assertInside(layout, width, height)
        }
    }

    private fun assertInside(layout: QrCardLayout, width: Int, height: Int) {
        listOf(layout.qrRect, layout.textRect).forEach { rect ->
            assertTrue(rect.left >= 0f)
            assertTrue(rect.top >= 0f)
            assertTrue(rect.right <= width.toFloat())
            assertTrue(rect.bottom <= height.toFloat())
            assertTrue(rect.width > 0f)
            assertTrue(rect.height > 0f)
        }
    }
}
