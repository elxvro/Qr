package com.elxvro.scan.qrcard

import kotlin.math.min

object QrCardReferenceFreeformPolicy {
    fun resolve(width: Int, height: Int, model: QrCardModel, aspect: QrCardAspectPreset): QrCardReferenceLayout {
        val base = QrCardReferenceLayoutPolicy.resolve(width, height, aspect)
        if (!model.freeformEnabled) return base
        val w = width.toFloat()
        val h = height.toFloat()
        val short = min(w, h)
        return base.copy(
            brandRect = textRect(base.brandRect, model.brandAnchorX, model.brandAnchorY, model.brandScale, w, h, short),
            titleRect = textRect(base.titleRect, model.titleAnchorX, model.titleAnchorY, model.titleScale, w, h, short),
            bodyRect = textRect(base.bodyRect, model.bodyAnchorX, model.bodyAnchorY, model.bodyScale, w, h, short),
            qrRect = squareRect(base.qrRect, model.qrAnchorX, model.qrAnchorY, model.qrScale, w, h, short),
            ctaRect = textRect(base.ctaRect, model.ctaAnchorX, model.ctaAnchorY, model.ctaScale, w, h, short)
        )
    }

    private fun textRect(base: LayoutRect, x: Float, y: Float, scale: Float, w: Float, h: Float, short: Float): LayoutRect {
        val s = scale.takeIf { it.isFinite() }?.coerceIn(0.70f, 1.70f) ?: 1f
        val rw = (base.width * s).coerceIn(short * 0.14f, w * 0.78f)
        val rh = (base.height * s).coerceIn(short * 0.04f, h * 0.24f)
        return centered(x, y, rw, rh, w, h)
    }

    private fun squareRect(base: LayoutRect, x: Float, y: Float, scale: Float, w: Float, h: Float, short: Float): LayoutRect {
        val s = scale.takeIf { it.isFinite() }?.coerceIn(0.72f, 1.55f) ?: 1f
        val side = (base.width * s).coerceIn(short * 0.22f, short * 0.62f)
        return centered(x, y, side, side, w, h)
    }

    private fun centered(x: Float, y: Float, rw: Float, rh: Float, w: Float, h: Float): LayoutRect {
        val padX = w * 0.03f
        val padY = h * 0.03f
        val cx = (x.coerceIn(0.05f, 0.95f) * w).coerceIn(padX + rw / 2f, w - padX - rw / 2f)
        val cy = (y.coerceIn(0.05f, 0.95f) * h).coerceIn(padY + rh / 2f, h - padY - rh / 2f)
        return LayoutRect(cx - rw / 2f, cy - rh / 2f, cx + rw / 2f, cy + rh / 2f)
    }
}
