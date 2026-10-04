package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCardOutputSizePolicyTest {
    @Test
    fun requestedSizeRepresentsLongEdgeForLandscapeAndPortrait() {
        assertEquals(QrCardOutputSize(4096, 2304), QrCardOutputSizePolicy.resolve(4096, 16f / 9f))
        assertEquals(QrCardOutputSize(2304, 4096), QrCardOutputSizePolicy.resolve(4096, 9f / 16f))
        assertEquals(QrCardOutputSize(3277, 4096), QrCardOutputSizePolicy.resolve(4096, 0.8f))
    }

    @Test
    fun smallStoryPreviewKeepsNineBySixteenGeometry() {
        assertEquals(QrCardOutputSize(394, 700), QrCardOutputSizePolicy.resolve(700, 9f / 16f))
    }

    @Test
    fun outputNeverExceedsRequestedLongEdge() {
        val ratios = listOf(1f, 16f / 9f, 1.586f, 0.8f, 9f / 16f)
        ratios.forEach { ratio ->
            val size = QrCardOutputSizePolicy.resolve(3072, ratio)
            assertEquals(3072, maxOf(size.width, size.height))
            assertTrue(size.width >= 512)
            assertTrue(size.height >= 512)
        }
    }
}
