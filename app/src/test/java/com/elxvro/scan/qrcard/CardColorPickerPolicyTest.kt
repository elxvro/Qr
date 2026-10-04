package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CardColorPickerPolicyTest {
    @Test
    fun hueSaturationValueProducesOpaqueArgb() {
        val color = CardColorPickerPolicy.toArgb(hue = 210f, saturation = 0.75f, value = 0.80f)
        assertEquals(0xFF, color ushr 24)
    }

    @Test
    fun hueWrapsAndChannelsStayValid() {
        val a = CardColorPickerPolicy.toArgb(-30f, 1f, 1f)
        val b = CardColorPickerPolicy.toArgb(330f, 1f, 1f)
        assertEquals(a, b)
    }

    @Test
    fun pickerCanRoundTripCommonColorsClosely() {
        listOf(
            0xFFFFFFFF.toInt(),
            0xFF000000.toInt(),
            0xFF0068F8.toInt(),
            0xFFE0B35D.toInt(),
            0xFF9C27B0.toInt()
        ).forEach { original ->
            val hsv = CardColorPickerPolicy.fromArgb(original)
            val rebuilt = CardColorPickerPolicy.toArgb(hsv.hue, hsv.saturation, hsv.value)
            assertTrue(CardColorPickerPolicy.channelDistance(original, rebuilt) <= 4)
        }
    }
}
