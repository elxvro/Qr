package com.elxvro.scan.scanner

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LowLightPolicyTest {
    @Test
    fun entersLowLightOnlyAfterStableDarkFrames() {
        var state = LowLightState()

        state = LowLightPolicy.update(state, 38)
        assertFalse(state.isLowLight)
        state = LowLightPolicy.update(state, 40)
        assertFalse(state.isLowLight)
        state = LowLightPolicy.update(state, 42)

        assertTrue(state.isLowLight)
    }

    @Test
    fun hysteresisKeepsLowLightUntilStableBrightFrames() {
        var state = LowLightState(isLowLight = true)

        state = LowLightPolicy.update(state, 52)
        assertTrue(state.isLowLight)
        state = LowLightPolicy.update(state, 70)
        assertTrue(state.isLowLight)
        state = LowLightPolicy.update(state, 72)
        assertTrue(state.isLowLight)
        state = LowLightPolicy.update(state, 75)

        assertFalse(state.isLowLight)
    }

    @Test
    fun midRangeFramesResetTransitionStreaks() {
        var state = LowLightState()
        state = LowLightPolicy.update(state, 35)
        state = LowLightPolicy.update(state, 55)
        state = LowLightPolicy.update(state, 35)
        state = LowLightPolicy.update(state, 35)

        assertFalse(state.isLowLight)
    }
}
