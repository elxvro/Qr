package com.elxvro.scan.qrcard

object QrCardImagePlacementPolicy {
    fun resolve(
        width: Int,
        height: Int,
        preset: QrCardDesignPreset,
        layout: QrCardV3Layout
    ): LayoutRect {
        return if (preset.fullBleedPhoto) {
            LayoutRect(0f, 0f, width.toFloat(), height.toFloat())
        } else {
            layout.imageRect
        }
    }
}
