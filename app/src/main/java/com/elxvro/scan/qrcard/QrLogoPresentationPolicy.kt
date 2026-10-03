package com.elxvro.scan.qrcard

object QrLogoPresentationPolicy {
    const val BACKING_FACTOR = 1.36f
    const val CORNER_FACTOR = 0.22f
    const val BORDER_FACTOR = 0.045f

    fun safeScale(requested: Float): Float =
        requested.takeIf { it.isFinite() && it > 0f }
            ?.coerceAtMost(QrCardValidator.MAX_LOGO_SCALE)
            ?: 0.18f
}
