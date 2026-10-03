package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCardTextLayoutTest {
    private val monoMeasure: (String) -> Float = { it.length * 10f }

    @Test
    fun wrapsLongTextAcrossMultipleLines() {
        val lines = QrCardTextLayout.wrap(
            text = "ELXVRO ile hızlı ve güvenli QR paylaşımı",
            maxWidth = 120f,
            maxLines = 3,
            measure = monoMeasure
        )

        assertTrue(lines.size in 2..3)
        assertTrue(lines.all { monoMeasure(it) <= 120f })
    }

    @Test
    fun finalLineEllipsizesWhenContentExceedsLimit() {
        val lines = QrCardTextLayout.wrap(
            text = "Bir iki üç dört beş altı yedi sekiz dokuz on",
            maxWidth = 80f,
            maxLines = 2,
            measure = monoMeasure
        )

        assertEquals(2, lines.size)
        assertTrue(lines.last().endsWith("…"))
        assertTrue(monoMeasure(lines.last()) <= 80f)
    }

    @Test
    fun keepsShortTextOnOneLine() {
        val lines = QrCardTextLayout.wrap(
            text = "ELXVRO",
            maxWidth = 120f,
            maxLines = 3,
            measure = monoMeasure
        )

        assertEquals(listOf("ELXVRO"), lines)
    }
}
