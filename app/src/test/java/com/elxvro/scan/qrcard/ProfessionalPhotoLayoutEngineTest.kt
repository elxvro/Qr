package com.elxvro.scan.qrcard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ProfessionalPhotoLayoutEngineTest {
    @Test
    fun photoLayoutsStayInsideCardAndKeepQrClearOfCopy() {
        val sizes = listOf(
            1600 to 900,
            1200 to 1200,
            960 to 1200,
            675 to 1200
        )

        sizes.forEach { (width, height) ->
            (0..4).forEach { variant ->
                val layout = ProfessionalPhotoLayoutEngine.resolve(width, height, variant)
                val rects = listOf(
                    layout.panelRect,
                    layout.brandRect,
                    layout.titleRect,
                    layout.bodyRect,
                    layout.qrRect,
                    layout.ctaRect
                )

                rects.forEach { rect ->
                    assertTrue(rect.left >= 0f)
                    assertTrue(rect.top >= 0f)
                    assertTrue(rect.right <= width.toFloat())
                    assertTrue(rect.bottom <= height.toFloat())
                    assertTrue(rect.width > 0f)
                    assertTrue(rect.height > 0f)
                }

                assertFalse(layout.qrRect.overlaps(layout.titleRect))
                assertFalse(layout.qrRect.overlaps(layout.bodyRect))
                assertFalse(layout.qrRect.overlaps(layout.ctaRect))
                assertTrue(layout.ctaRect.top >= layout.qrRect.bottom)
                assertTrue(abs(layout.qrRect.width - layout.qrRect.height) < 0.5f)
            }
        }
    }
}
