package com.elxvro.scan.qrcard

import kotlin.math.min

data class QrCardReferenceLayout(
    val brandRect: LayoutRect,
    val titleRect: LayoutRect,
    val qrRect: LayoutRect,
    val ctaRect: LayoutRect
)

object QrCardReferenceLayoutPolicy {
    fun resolve(
        width: Int,
        height: Int,
        aspect: QrCardAspectPreset
    ): QrCardReferenceLayout {
        require(width > 0 && height > 0)

        val w = width.toFloat()
        val h = height.toFloat()
        val short = min(w, h)

        fun rect(l: Float, t: Float, r: Float, b: Float) =
            LayoutRect(w * l, h * t, w * r, h * b)

        fun square(cx: Float, cy: Float, fractionOfShort: Float): LayoutRect {
            val side = short * fractionOfShort
            val centerX = w * cx
            val centerY = h * cy
            return LayoutRect(
                centerX - side / 2f,
                centerY - side / 2f,
                centerX + side / 2f,
                centerY + side / 2f
            )
        }

        return when (aspect) {
            QrCardAspectPreset.WIDE -> QrCardReferenceLayout(
                brandRect = rect(0.07f, 0.12f, 0.48f, 0.22f),
                titleRect = rect(0.07f, 0.24f, 0.53f, 0.72f),
                qrRect = square(0.78f, 0.43f, 0.50f),
                ctaRect = rect(0.66f, 0.73f, 0.91f, 0.86f)
            )
            QrCardAspectPreset.CARD -> QrCardReferenceLayout(
                brandRect = rect(0.07f, 0.12f, 0.48f, 0.22f),
                titleRect = rect(0.07f, 0.24f, 0.52f, 0.70f),
                qrRect = square(0.79f, 0.43f, 0.49f),
                ctaRect = rect(0.65f, 0.73f, 0.92f, 0.87f)
            )
            QrCardAspectPreset.SQUARE -> QrCardReferenceLayout(
                brandRect = rect(0.10f, 0.08f, 0.90f, 0.15f),
                titleRect = rect(0.10f, 0.16f, 0.90f, 0.31f),
                qrRect = square(0.50f, 0.54f, 0.42f),
                ctaRect = rect(0.27f, 0.80f, 0.73f, 0.90f)
            )
            QrCardAspectPreset.PORTRAIT -> QrCardReferenceLayout(
                brandRect = rect(0.10f, 0.08f, 0.90f, 0.14f),
                titleRect = rect(0.10f, 0.15f, 0.90f, 0.31f),
                qrRect = square(0.50f, 0.55f, 0.48f),
                ctaRect = rect(0.22f, 0.79f, 0.78f, 0.87f)
            )
            QrCardAspectPreset.STORY -> QrCardReferenceLayout(
                brandRect = rect(0.10f, 0.08f, 0.90f, 0.13f),
                titleRect = rect(0.10f, 0.14f, 0.90f, 0.29f),
                qrRect = square(0.50f, 0.51f, 0.54f),
                ctaRect = rect(0.20f, 0.70f, 0.80f, 0.76f)
            )
            QrCardAspectPreset.DEFAULT -> resolve(width, height, QrCardAspectPreset.CARD)
        }
    }
}
