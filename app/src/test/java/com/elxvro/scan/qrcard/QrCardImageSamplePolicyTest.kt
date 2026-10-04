package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Test

class QrCardImageSamplePolicyTest {
    @Test
    fun smallImagesAreNotDownsampled() {
        assertEquals(1, QrCardImageSamplePolicy.inSampleSize(1600, 1200))
    }

    @Test
    fun largeImagesUsePowerOfTwoSamplingNearTwoK() {
        assertEquals(2, QrCardImageSamplePolicy.inSampleSize(4000, 3000))
        assertEquals(4, QrCardImageSamplePolicy.inSampleSize(8000, 6000))
    }

    @Test
    fun invalidBoundsFallBackToOne() {
        assertEquals(1, QrCardImageSamplePolicy.inSampleSize(0, 0))
    }
}
