package com.elxvro.scan.qrcard

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

object QrContrastPolicy {
    const val MIN_CONTRAST_RATIO = 4.5

    fun isReadable(foregroundArgb: Int, backgroundArgb: Int): Boolean =
        contrastRatio(foregroundArgb, backgroundArgb) >= MIN_CONTRAST_RATIO

    fun contrastRatio(foregroundArgb: Int, backgroundArgb: Int): Double {
        val foreground = relativeLuminance(foregroundArgb)
        val background = relativeLuminance(backgroundArgb)
        val lighter = max(foreground, background)
        val darker = min(foreground, background)
        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun relativeLuminance(argb: Int): Double {
        fun linear(channel: Int): Double {
            val normalized = channel / 255.0
            return if (normalized <= 0.03928) normalized / 12.92
            else ((normalized + 0.055) / 1.055).pow(2.4)
        }

        val red = (argb shr 16) and 0xFF
        val green = (argb shr 8) and 0xFF
        val blue = argb and 0xFF
        return 0.2126 * linear(red) + 0.7152 * linear(green) + 0.0722 * linear(blue)
    }
}
