package com.elxvro.scan.qrcard

import kotlin.math.min

data class QrCardV3Layout(
    val qrRect: LayoutRect,
    val textRect: LayoutRect,
    val imageRect: LayoutRect,
    val ctaRect: LayoutRect
)

object QrCardV3LayoutPolicy {
    fun resolve(width: Int, height: Int, preset: QrCardDesignPreset): QrCardV3Layout {
        require(width > 0 && height > 0)
        val w = width.toFloat()
        val h = height.toFloat()
        val short = min(w, h)

        fun rect(l: Float, t: Float, r: Float, b: Float) =
            LayoutRect(w * l, h * t, w * r, h * b)

        fun square(centerX: Float, centerY: Float, sideOfShort: Float): LayoutRect {
            val side = short * sideOfShort
            val cx = w * centerX
            val cy = h * centerY
            return LayoutRect(cx - side / 2f, cy - side / 2f, cx + side / 2f, cy + side / 2f)
        }

        return when (preset) {
            QrCardDesignPreset.SQUARE_MINIMAL -> QrCardV3Layout(
                qrRect = square(0.23f, 0.75f, 0.32f),
                textRect = rect(0.07f, 0.08f, 0.45f, 0.56f),
                imageRect = rect(0.43f, 0.05f, 0.95f, 0.66f),
                ctaRect = rect(0.43f, 0.72f, 0.91f, 0.84f)
            )
            QrCardDesignPreset.SQUARE_PHOTO -> QrCardV3Layout(
                qrRect = square(0.74f, 0.76f, 0.30f),
                textRect = rect(0.07f, 0.08f, 0.58f, 0.48f),
                imageRect = rect(0f, 0f, 1f, 1f),
                ctaRect = rect(0.08f, 0.76f, 0.50f, 0.87f)
            )
            QrCardDesignPreset.WIDE_EDITORIAL -> QrCardV3Layout(
                qrRect = square(0.15f, 0.72f, 0.29f),
                textRect = rect(0.06f, 0.10f, 0.40f, 0.51f),
                imageRect = rect(0.40f, 0f, 1f, 1f),
                ctaRect = rect(0.25f, 0.68f, 0.49f, 0.80f)
            )
            QrCardDesignPreset.WIDE_CINEMATIC -> QrCardV3Layout(
                qrRect = square(0.82f, 0.59f, 0.31f),
                textRect = rect(0.06f, 0.10f, 0.48f, 0.56f),
                imageRect = rect(0f, 0f, 1f, 1f),
                ctaRect = rect(0.68f, 0.80f, 0.95f, 0.91f)
            )
            QrCardDesignPreset.CLASSIC_EXECUTIVE -> QrCardV3Layout(
                qrRect = square(0.84f, 0.47f, 0.37f),
                textRect = rect(0.06f, 0.12f, 0.50f, 0.62f),
                imageRect = rect(0.47f, 0f, 0.72f, 1f),
                ctaRect = rect(0.65f, 0.73f, 0.95f, 0.87f)
            )
            QrCardDesignPreset.CLASSIC_LUXURY -> QrCardV3Layout(
                qrRect = square(0.80f, 0.45f, 0.35f),
                textRect = rect(0.06f, 0.12f, 0.48f, 0.60f),
                imageRect = rect(0.38f, 0f, 0.73f, 1f),
                ctaRect = rect(0.65f, 0.72f, 0.94f, 0.87f)
            )
            QrCardDesignPreset.PORTRAIT_EDITORIAL -> QrCardV3Layout(
                qrRect = square(0.27f, 0.78f, 0.40f),
                textRect = rect(0.07f, 0.46f, 0.93f, 0.61f),
                imageRect = rect(0f, 0f, 1f, 0.44f),
                ctaRect = rect(0.51f, 0.77f, 0.92f, 0.87f)
            )
            QrCardDesignPreset.PORTRAIT_CAMPAIGN -> QrCardV3Layout(
                qrRect = square(0.29f, 0.77f, 0.38f),
                textRect = rect(0.07f, 0.08f, 0.90f, 0.38f),
                imageRect = rect(0f, 0f, 1f, 1f),
                ctaRect = rect(0.52f, 0.78f, 0.92f, 0.88f)
            )
            QrCardDesignPreset.STORY_EDITORIAL -> QrCardV3Layout(
                qrRect = square(0.50f, 0.68f, 0.58f),
                textRect = rect(0.07f, 0.07f, 0.82f, 0.31f),
                imageRect = rect(0f, 0f, 1f, 0.57f),
                ctaRect = rect(0.14f, 0.86f, 0.86f, 0.92f)
            )
            QrCardDesignPreset.STORY_LUXURY -> QrCardV3Layout(
                qrRect = square(0.50f, 0.70f, 0.54f),
                textRect = rect(0.07f, 0.08f, 0.90f, 0.29f),
                imageRect = rect(0f, 0f, 1f, 1f),
                ctaRect = rect(0.17f, 0.87f, 0.83f, 0.93f)
            )
        }
    }
}
