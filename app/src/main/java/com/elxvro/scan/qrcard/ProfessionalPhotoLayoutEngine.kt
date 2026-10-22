package com.elxvro.scan.qrcard

import kotlin.math.min

/**
 * Layouts tuned for packaged sector photos. Landscape photos keep the business
 * scene on the right and reserve a readable left content zone. Square/portrait
 * cards dock copy and QR below the hero region.
 */
object ProfessionalPhotoLayoutEngine {
    fun resolve(width: Int, height: Int, variant: Int): ProfessionalCardLayout {
        require(width > 0 && height > 0)
        return if (width.toFloat() / height >= 1.12f) {
            landscape(width, height, variant)
        } else {
            portrait(width, height, variant)
        }
    }

    private fun landscape(width: Int, height: Int, variant: Int): ProfessionalCardLayout {
        val w = width.toFloat()
        val h = height.toFloat()
        val short = min(w, h)
        fun rect(l: Float, t: Float, r: Float, b: Float) =
            LayoutRect(w * l, h * t, w * r, h * b)
        fun qr(cx: Float, cy: Float, side: Float): LayoutRect {
            val d = short * side
            return LayoutRect(w * cx - d / 2f, h * cy - d / 2f, w * cx + d / 2f, h * cy + d / 2f)
        }

        val qrCenterX = when (variant % 5) {
            1, 4 -> 0.39f
            else -> 0.19f
        }
        val align =
            if (variant % 5 == 2) FixedCardTextAlignment.CENTER else FixedCardTextAlignment.LEFT
        val brand =
            if (align == FixedCardTextAlignment.CENTER) rect(.08f, .11f, .54f, .18f)
            else rect(.07f, .11f, .51f, .18f)
        val title =
            if (align == FixedCardTextAlignment.CENTER) rect(.08f, .21f, .54f, .35f)
            else rect(.07f, .21f, .51f, .35f)
        val body =
            if (align == FixedCardTextAlignment.CENTER) rect(.08f, .37f, .54f, .48f)
            else rect(.07f, .37f, .51f, .48f)
        val qrRect = qr(qrCenterX, .69f, .30f)
        val ctaWidth = qrRect.width * .90f
        val ctaHeight = (h * .065f).coerceAtLeast(32f)
        val ctaLeft =
            ((qrRect.left + qrRect.right - ctaWidth) / 2f).coerceAtLeast(w * .05f)
        val ctaTop = (qrRect.bottom + h * .025f).coerceAtMost(h * .88f)

        return ProfessionalCardLayout(
            panelRect = rect(.035f, .055f, .575f, .945f),
            brandRect = brand,
            titleRect = title,
            bodyRect = body,
            qrRect = qrRect,
            ctaRect = LayoutRect(ctaLeft, ctaTop, ctaLeft + ctaWidth, ctaTop + ctaHeight),
            textAlignment = align
        )
    }

    private fun portrait(width: Int, height: Int, variant: Int): ProfessionalCardLayout {
        val w = width.toFloat()
        val h = height.toFloat()
        val short = min(w, h)
        fun rect(l: Float, t: Float, r: Float, b: Float) =
            LayoutRect(w * l, h * t, w * r, h * b)
        fun qr(cx: Float, cy: Float, side: Float): LayoutRect {
            val d = short * side
            return LayoutRect(w * cx - d / 2f, h * cy - d / 2f, w * cx + d / 2f, h * cy + d / 2f)
        }

        val centerX = when (variant % 5) {
            1 -> .30f
            3 -> .70f
            else -> .50f
        }
        val qrRect = qr(centerX, .82f, .17f)
        val ctaWidth = (qrRect.width * 1.30f).coerceAtMost(w * .38f)
        val ctaHeight = (h * .045f).coerceAtLeast(30f)
        val ctaLeft =
            ((qrRect.left + qrRect.right - ctaWidth) / 2f)
                .coerceIn(w * .06f, w * .94f - ctaWidth)
        val ctaTop = (qrRect.bottom + h * .015f).coerceAtMost(h * .92f)

        return ProfessionalCardLayout(
            panelRect = rect(.055f, .46f, .945f, .965f),
            brandRect = rect(.10f, .50f, .90f, .56f),
            titleRect = rect(.10f, .58f, .90f, .66f),
            bodyRect = rect(.10f, .67f, .90f, .73f),
            qrRect = qrRect,
            ctaRect = LayoutRect(ctaLeft, ctaTop, ctaLeft + ctaWidth, ctaTop + ctaHeight),
            textAlignment = FixedCardTextAlignment.CENTER
        )
    }
}
