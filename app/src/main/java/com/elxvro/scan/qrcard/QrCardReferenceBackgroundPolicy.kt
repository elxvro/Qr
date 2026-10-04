package com.elxvro.scan.qrcard

data class QrCardReferenceBackground(
    val useUserPhoto: Boolean,
    val useFixedDarkGold: Boolean,
    val photoOverlayAlpha: Float
)

object QrCardReferenceBackgroundPolicy {
    fun resolve(mode: QrCardBackgroundMode): QrCardReferenceBackground = when (mode) {
        QrCardBackgroundMode.FIXED_BACKGROUND -> QrCardReferenceBackground(
            useUserPhoto = false,
            useFixedDarkGold = true,
            photoOverlayAlpha = 0f
        )
        QrCardBackgroundMode.FULL_BACKGROUND -> QrCardReferenceBackground(
            useUserPhoto = true,
            useFixedDarkGold = false,
            photoOverlayAlpha = 0.34f
        )
    }
}
