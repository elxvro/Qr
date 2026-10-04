package com.elxvro.scan.qrcard

data class QrCardTextColors(
    val brandArgb: Int,
    val titleArgb: Int,
    val bodyArgb: Int,
    val ctaArgb: Int
)

object QrCardTextColorPolicy {
    fun resolve(
        theme: QrCardDesignTheme,
        brandTextArgb: Int?,
        titleTextArgb: Int,
        bodyTextArgb: Int?,
        ctaTextArgb: Int?
    ): QrCardTextColors = QrCardTextColors(
        brandArgb = brandTextArgb ?: theme.accentArgb,
        titleArgb = titleTextArgb,
        bodyArgb = bodyTextArgb ?: theme.bodyArgb,
        ctaArgb = ctaTextArgb ?: readableOn(theme.accentArgb)
    )

    fun readableOn(color: Int): Int {
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        val luminance = 0.299 * r + 0.587 * g + 0.114 * b
        return if (luminance > 150) 0xFF111318.toInt() else 0xFFFFFFFF.toInt()
    }
}
