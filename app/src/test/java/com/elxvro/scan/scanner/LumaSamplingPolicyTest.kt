package com.elxvro.scan.scanner

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LumaSamplingPolicyTest {
    @Test
    fun firstFrameIsAlwaysSampled() {
        assertTrue(LumaSamplingPolicy.shouldSample(100L, Long.MIN_VALUE))
    }

    @Test
    fun throttlesFramesInsideSamplingInterval() {
        assertFalse(LumaSamplingPolicy.shouldSample(1_200L, 1_000L))
        assertTrue(LumaSamplingPolicy.shouldSample(1_500L, 1_000L))
    }
}
