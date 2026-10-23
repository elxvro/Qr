package com.elxvro.scan.qrcard

import kotlin.math.min

object ProfessionalEditableLayoutPolicy {
    fun resolve(
        width: Int,
        height: Int,
        model: QrCardModel,
        preset: ProfessionalCardPreset,
        usePhotoLayout: Boolean
    ): ProfessionalCardLayout {
        val base = if (usePhotoLayout) {
            ProfessionalPhotoLayoutEngine.resolve(width, height, preset.artVariant)
        } else {
            ProfessionalCardLayoutEngine.resolve(width, height, preset.composition)
        }
        if (!model.freeformEnabled || preset.tier != ProfessionalCardTier.PRO) {
            return base
        }

        val short = min(width, height).toFloat()
        return base.copy(
            brandRect = transformTextRect(base.brandRect, model.brandAnchorX, model.brandAnchorY, model.brandScale, width.toFloat(), height.toFloat(), short, 0.70f, 1.60f),
            titleRect = transformTextRect(base.titleRect, model.titleAnchorX, model.titleAnchorY, model.titleScale, width.toFloat(), height.toFloat(), short, 0.72f, 1.70f),
            bodyRect = transformTextRect(base.bodyRect, model.bodyAnchorX, model.bodyAnchorY, model.bodyScale, width.toFloat(), height.toFloat(), short, 0.74f, 1.70f),
            qrRect = transformSquareRect(base.qrRect, model.qrAnchorX, model.qrAnchorY, model.qrScale, width.toFloat(), height.toFloat(), short, 0.72f, 1.50f),
            ctaRect = transformTextRect(base.ctaRect, model.ctaAnchorX, model.ctaAnchorY, model.ctaScale, width.toFloat(), height.toFloat(), short, 0.72f, 1.60f),
            textAlignment = when (model.textAlignment) {
                CardTextAlignment.LEFT -> FixedCardTextAlignment.LEFT
                CardTextAlignment.CENTER -> FixedCardTextAlignment.CENTER
                CardTextAlignment.RIGHT -> FixedCardTextAlignment.RIGHT
            }
        )
    }

    private fun transformTextRect(
        base: LayoutRect,
        anchorX: Float,
        anchorY: Float,
        scale: Float,
        width: Float,
        height: Float,
        short: Float,
        minScale: Float,
        maxScale: Float
    ): LayoutRect {
        val safeScale = scale.takeIf { it.isFinite() }?.coerceIn(minScale, maxScale) ?: 1f
        val targetWidth = (base.width * safeScale).coerceIn(short * 0.16f, width * 0.72f)
        val targetHeight = (base.height * safeScale).coerceIn(short * 0.05f, height * 0.26f)
        return rectFromCenter(
            cx = normalizedCenter(anchorX, width, (base.left + base.right) / 2f),
            cy = normalizedCenter(anchorY, height, (base.top + base.bottom) / 2f),
            targetWidth = targetWidth,
            targetHeight = targetHeight,
            width = width,
            height = height
        )
    }

    private fun transformSquareRect(
        base: LayoutRect,
        anchorX: Float,
        anchorY: Float,
        scale: Float,
        width: Float,
        height: Float,
        short: Float,
        minScale: Float,
        maxScale: Float
    ): LayoutRect {
        val safeScale = scale.takeIf { it.isFinite() }?.coerceIn(minScale, maxScale) ?: 1f
        val side = (base.width * safeScale).coerceIn(short * 0.22f, short * 0.62f)
        return rectFromCenter(
            cx = normalizedCenter(anchorX, width, (base.left + base.right) / 2f),
            cy = normalizedCenter(anchorY, height, (base.top + base.bottom) / 2f),
            targetWidth = side,
            targetHeight = side,
            width = width,
            height = height
        )
    }

    private fun rectFromCenter(
        cx: Float,
        cy: Float,
        targetWidth: Float,
        targetHeight: Float,
        width: Float,
        height: Float
    ): LayoutRect {
        val horizontalPadding = width * 0.03f
        val verticalPadding = height * 0.03f
        val clampedCx = cx.coerceIn(horizontalPadding + targetWidth / 2f, width - horizontalPadding - targetWidth / 2f)
        val clampedCy = cy.coerceIn(verticalPadding + targetHeight / 2f, height - verticalPadding - targetHeight / 2f)
        return LayoutRect(
            left = clampedCx - targetWidth / 2f,
            top = clampedCy - targetHeight / 2f,
            right = clampedCx + targetWidth / 2f,
            bottom = clampedCy + targetHeight / 2f
        )
    }

    private fun normalizedCenter(value: Float, size: Float, fallback: Float): Float {
        return if (value.isFinite()) {
            value.coerceIn(0.05f, 0.95f) * size
        } else {
            fallback
        }
    }
}
