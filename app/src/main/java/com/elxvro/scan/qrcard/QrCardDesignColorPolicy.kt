package com.elxvro.scan.qrcard

object QrCardDesignColorPolicy {
    fun resolve(
        preset: QrCardDesignPreset,
        cardBackgroundArgb: Int,
        accentArgb: Int,
        textArgb: Int,
        qrForegroundArgb: Int,
        qrBackgroundArgb: Int
    ): QrCardDesignTheme {
        val base = preset.theme()
        val body = if (textArgb == base.titleArgb) {
            base.bodyArgb
        } else {
            blend(textArgb, cardBackgroundArgb, 0.34f)
        }
        return base.copy(
            backgroundArgb = cardBackgroundArgb,
            accentArgb = accentArgb,
            titleArgb = textArgb,
            bodyArgb = body,
            qrForegroundArgb = qrForegroundArgb,
            qrBackgroundArgb = qrBackgroundArgb
        )
    }

    private fun blend(a: Int, b: Int, fraction: Float): Int {
        val f = fraction.coerceIn(0f, 1f)
        fun channel(shift: Int): Int {
            val ca = (a shr shift) and 0xFF
            val cb = (b shr shift) and 0xFF
            return (ca + ((cb - ca) * f)).toInt().coerceIn(0, 255)
        }
        return (0xFF shl 24) or
            (channel(16) shl 16) or
            (channel(8) shl 8) or
            channel(0)
    }
}
