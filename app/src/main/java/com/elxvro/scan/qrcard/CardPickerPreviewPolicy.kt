package com.elxvro.scan.qrcard

data class CardPickerPreviewSpec(
    val longEdge: Int,
    val showQr: Boolean,
    val showText: Boolean,
    val showCta: Boolean
)

object CardPickerPreviewPolicy {
    fun thumbnailSpec() = CardPickerPreviewSpec(
        longEdge = 320,
        showQr = true,
        showText = true,
        showCta = true
    )
}
