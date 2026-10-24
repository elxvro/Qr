package com.elxvro.scan.qrnew

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import kotlin.math.max
import kotlin.math.min

enum class QrV2Aspect(val ratio: Float, val label: String) {
    SQUARE(1f, "1:1"),
    CARD(1.586f, "Kart"),
    PORTRAIT(0.8f, "4:5"),
    STORY(9f / 16f, "9:16"),
    WIDE(16f / 9f, "16:9")
}

enum class QrV2Font(val label: String, val family: String) {
    SANS("Modern", "sans-serif"),
    SERIF("Serif", "serif"),
    ROUNDED("Yuvarlak", "sans-serif-rounded"),
    CONDENSED("Dar", "sans-serif-condensed"),
    MONO("Mono", "monospace"),
    SCRIPT("El yazısı", "cursive"),
    DISPLAY("Kalın", "sans-serif-black"),
    LIGHT("İnce", "sans-serif-light")
}

enum class QrV2Element(val label: String) {
    BRAND("Marka"),
    TITLE("Başlık"),
    BODY("Alt Yazı"),
    QR("QR Kod"),
    CTA("Buton")
}

enum class QrV2Align(val label: String) {
    LEFT("Sola"),
    CENTER("Ortala"),
    RIGHT("Sağa")
}

data class QrV2State(
    val aspect: QrV2Aspect = QrV2Aspect.CARD,
    val brand: String = "ELXVRO",
    val title: String = "Bizi Keşfet",
    val body: String = "QR kodu tara ve keşfet",
    val cta: String = "TARA",
    val font: QrV2Font = QrV2Font.SANS,
    val textColor: Int = Color.WHITE,
    val accentColor: Int = 0xFF168CFF.toInt(),
    val textAlign: QrV2Align = QrV2Align.CENTER,
    val bold: Boolean = true,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val qrColor: Int = Color.BLACK,
    val qrBorderColor: Int = Color.WHITE,
    val qrBorderScale: Float = 1f,
    val brandX: Float = 0.24f,
    val brandY: Float = 0.18f,
    val titleX: Float = 0.25f,
    val titleY: Float = 0.32f,
    val bodyX: Float = 0.25f,
    val bodyY: Float = 0.48f,
    val qrX: Float = 0.77f,
    val qrY: Float = 0.43f,
    val ctaX: Float = 0.77f,
    val ctaY: Float = 0.76f,
    val brandScale: Float = 1f,
    val titleScale: Float = 1f,
    val bodyScale: Float = 1f,
    val qrScale: Float = 1f,
    val ctaScale: Float = 1f
)

data class QrV2Layout(
    val brand: RectF,
    val title: RectF,
    val body: RectF,
    val qr: RectF,
    val cta: RectF
)


fun QrV2State.withSafeAspectLayout(newAspect: QrV2Aspect): QrV2State {
    return when (newAspect) {
        QrV2Aspect.CARD -> copy(
            aspect = newAspect,
            brandX = 0.24f, brandY = 0.17f,
            titleX = 0.24f, titleY = 0.31f,
            bodyX = 0.24f, bodyY = 0.47f,
            qrX = 0.77f, qrY = 0.40f,
            ctaX = 0.77f, ctaY = 0.74f,
            brandScale = 1f, titleScale = 1f, bodyScale = 1f,
            qrScale = 1f, ctaScale = 1f
        )
        QrV2Aspect.WIDE -> copy(
            aspect = newAspect,
            brandX = 0.23f, brandY = 0.18f,
            titleX = 0.23f, titleY = 0.34f,
            bodyX = 0.23f, bodyY = 0.52f,
            qrX = 0.78f, qrY = 0.40f,
            ctaX = 0.78f, ctaY = 0.76f,
            brandScale = 1f, titleScale = 1f, bodyScale = 1f,
            qrScale = 1f, ctaScale = 1f
        )
        QrV2Aspect.SQUARE -> copy(
            aspect = newAspect,
            brandX = 0.50f, brandY = 0.10f,
            titleX = 0.50f, titleY = 0.19f,
            bodyX = 0.50f, bodyY = 0.28f,
            qrX = 0.50f, qrY = 0.54f,
            ctaX = 0.50f, ctaY = 0.84f,
            brandScale = 1f, titleScale = 1f, bodyScale = 1f,
            qrScale = 1f, ctaScale = 1f
        )
        QrV2Aspect.PORTRAIT -> copy(
            aspect = newAspect,
            brandX = 0.50f, brandY = 0.08f,
            titleX = 0.50f, titleY = 0.16f,
            bodyX = 0.50f, bodyY = 0.24f,
            qrX = 0.50f, qrY = 0.49f,
            ctaX = 0.50f, ctaY = 0.76f,
            brandScale = 1f, titleScale = 1f, bodyScale = 1f,
            qrScale = 1f, ctaScale = 1f
        )
        QrV2Aspect.STORY -> copy(
            aspect = newAspect,
            brandX = 0.50f, brandY = 0.07f,
            titleX = 0.50f, titleY = 0.14f,
            bodyX = 0.50f, bodyY = 0.21f,
            qrX = 0.50f, qrY = 0.43f,
            ctaX = 0.50f, ctaY = 0.67f,
            brandScale = 1f, titleScale = 1f, bodyScale = 1f,
            qrScale = 1f, ctaScale = 1f
        )
    }
}

object QrV2Renderer {
    fun renderFree(background: Bitmap, qr: Bitmap, width: Int = 1080): Bitmap {
        val safeWidth = width.coerceIn(720, 2160)
        val backgroundRatio = background.width.toFloat() / background.height.coerceAtLeast(1)
        val height = (safeWidth / backgroundRatio).toInt().coerceAtLeast(1)
        val out = Bitmap.createBitmap(safeWidth, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val w = safeWidth.toFloat()
        val h = height.toFloat()

        // The professional ELXVRO card is a bundled raster asset.
        canvas.drawBitmap(background, null, RectF(0f, 0f, w, h), paint)

        // Only the generated QR is placed inside the card's reserved white frame.
        val qrSide = w * 0.42f
        val qrCenterX = w * 0.50f
        val qrCenterY = h * 0.412f
        val qrRect = RectF(
            qrCenterX - qrSide / 2f,
            qrCenterY - qrSide / 2f,
            qrCenterX + qrSide / 2f,
            qrCenterY + qrSide / 2f
        )
        canvas.drawBitmap(qr, null, qrRect, paint)
        return out
    }

    fun renderPro(
        qr: Bitmap,
        background: Bitmap?,
        state: QrV2State,
        width: Int = 1400
    ): Bitmap {
        val outWidth = width.coerceIn(720, 4096)
        val outHeight = (outWidth / state.aspect.ratio).toInt().coerceAtLeast(1)
        val out = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val w = outWidth.toFloat()
        val h = outHeight.toFloat()
        val short = min(w, h)

        if (background != null) {
            drawCenterCrop(canvas, paint, background, RectF(0f, 0f, w, h))
        } else {
            canvas.drawColor(0xFF101820.toInt())
        }

        val layout = layout(outWidth, outHeight, state)
        drawText(canvas, paint, state.brand, layout.brand, state, state.brandScale, true)
        drawText(canvas, paint, state.title, layout.title, state, state.titleScale, true)
        drawText(canvas, paint, state.body, layout.body, state, state.bodyScale, false)

        paint.color = state.qrBorderColor
        val qrPad = short * 0.018f * state.qrBorderScale.coerceIn(0.4f, 2.2f)
        canvas.drawRoundRect(expand(layout.qr, qrPad), short * 0.022f, short * 0.022f, paint)
        canvas.drawBitmap(qr, null, layout.qr, paint)

        if (state.cta.isNotBlank()) {
            paint.color = state.accentColor
            canvas.drawRoundRect(layout.cta, layout.cta.height() / 2f, layout.cta.height() / 2f, paint)
            paint.typeface = Typeface.create(state.font.family, Typeface.BOLD)
            paint.textAlign = Paint.Align.CENTER
            paint.color = Color.WHITE
            paint.textSize = max(short * 0.026f * state.ctaScale.coerceIn(0.7f, 1.8f), 14f)
            val baseline = layout.cta.centerY() - (paint.ascent() + paint.descent()) / 2f
            canvas.drawText(ellipsize(state.cta, paint, layout.cta.width() * 0.82f), layout.cta.centerX(), baseline, paint)
            paint.textAlign = Paint.Align.LEFT
        }

        return out
    }

    fun layout(width: Int, height: Int, state: QrV2State): QrV2Layout {
        val w = width.toFloat()
        val h = height.toFloat()
        val short = min(w, h)

        fun rect(cx: Float, cy: Float, rw: Float, rh: Float): RectF {
            val clampedW = rw.coerceAtMost(w * 0.88f)
            val clampedH = rh.coerceAtMost(h * 0.30f)
            val centerX = (cx.coerceIn(0.05f, 0.95f) * w)
                .coerceIn(clampedW / 2f + w * 0.025f, w - clampedW / 2f - w * 0.025f)
            val centerY = (cy.coerceIn(0.05f, 0.95f) * h)
                .coerceIn(clampedH / 2f + h * 0.025f, h - clampedH / 2f - h * 0.025f)
            return RectF(
                centerX - clampedW / 2f,
                centerY - clampedH / 2f,
                centerX + clampedW / 2f,
                centerY + clampedH / 2f
            )
        }

        val vertical = state.aspect == QrV2Aspect.PORTRAIT || state.aspect == QrV2Aspect.STORY
        val centered = vertical || state.aspect == QrV2Aspect.SQUARE
        val textWidth = if (centered) w * 0.78f else w * 0.42f
        val bodyWidth = if (centered) w * 0.82f else w * 0.46f
        val qrBase = when (state.aspect) {
            QrV2Aspect.STORY -> 0.42f
            QrV2Aspect.PORTRAIT -> 0.38f
            QrV2Aspect.SQUARE -> 0.34f
            QrV2Aspect.CARD -> 0.30f
            QrV2Aspect.WIDE -> 0.30f
        }
        val qrSide = (short * qrBase * state.qrScale.coerceIn(0.65f, 1.65f))
            .coerceIn(short * 0.18f, min(short * 0.50f, min(w * 0.82f, h * 0.82f)))

        fun square(cx: Float, cy: Float, side: Float): RectF {
            val centerX = (cx.coerceIn(0.05f, 0.95f) * w)
                .coerceIn(side / 2f + w * 0.025f, w - side / 2f - w * 0.025f)
            val centerY = (cy.coerceIn(0.05f, 0.95f) * h)
                .coerceIn(side / 2f + h * 0.025f, h - side / 2f - h * 0.025f)
            return RectF(
                centerX - side / 2f,
                centerY - side / 2f,
                centerX + side / 2f,
                centerY + side / 2f
            )
        }

        return QrV2Layout(
            brand = rect(state.brandX, state.brandY, textWidth, short * 0.10f * state.brandScale.coerceIn(0.7f, 1.8f)),
            title = rect(state.titleX, state.titleY, textWidth, short * 0.14f * state.titleScale.coerceIn(0.7f, 1.8f)),
            body = rect(state.bodyX, state.bodyY, bodyWidth, short * 0.13f * state.bodyScale.coerceIn(0.7f, 1.8f)),
            qr = square(state.qrX, state.qrY, qrSide),
            cta = rect(
                state.ctaX,
                state.ctaY,
                (if (centered) short * 0.44f else short * 0.34f) * state.ctaScale.coerceIn(0.7f, 1.8f),
                short * 0.085f * state.ctaScale.coerceIn(0.7f, 1.8f)
            )
        )
    }

    private fun drawText(
        canvas: Canvas,
        paint: Paint,
        text: String,
        rect: RectF,
        state: QrV2State,
        scale: Float,
        bold: Boolean
    ) {
        if (text.isBlank()) return
        val style = when {
            state.bold && state.italic -> Typeface.BOLD_ITALIC
            state.bold || bold -> Typeface.BOLD
            state.italic -> Typeface.ITALIC
            else -> Typeface.NORMAL
        }
        paint.typeface = Typeface.create(state.font.family, style)
        paint.color = state.textColor
        paint.isUnderlineText = state.underline
        paint.textAlign = when (state.textAlign) {
            QrV2Align.LEFT -> Paint.Align.LEFT
            QrV2Align.CENTER -> Paint.Align.CENTER
            QrV2Align.RIGHT -> Paint.Align.RIGHT
        }
        paint.textSize = max(rect.height() * 0.54f, 14f)
        paint.setShadowLayer(max(2f, rect.height() * 0.045f), 0f, max(1f, rect.height() * 0.025f), 0x88000000.toInt())
        val baseline = rect.centerY() - (paint.ascent() + paint.descent()) / 2f
        val x = when (state.textAlign) {
            QrV2Align.LEFT -> rect.left
            QrV2Align.CENTER -> rect.centerX()
            QrV2Align.RIGHT -> rect.right
        }
        canvas.drawText(ellipsize(text, paint, rect.width()), x, baseline, paint)
        paint.clearShadowLayer()
        paint.isUnderlineText = false
        paint.textAlign = Paint.Align.LEFT
    }

    private fun ellipsize(text: String, paint: Paint, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        var value = text
        while (value.length > 1 && paint.measureText("$value…") > maxWidth) {
            value = value.dropLast(1)
        }
        return "$value…"
    }

    private fun drawCenterCrop(canvas: Canvas, paint: Paint, bitmap: Bitmap, dst: RectF) {
        val srcRatio = bitmap.width.toFloat() / bitmap.height.coerceAtLeast(1)
        val dstRatio = dst.width() / dst.height().coerceAtLeast(1f)
        val src = if (srcRatio > dstRatio) {
            val cropWidth = (bitmap.height * dstRatio).toInt().coerceAtLeast(1)
            val left = ((bitmap.width - cropWidth) / 2).coerceAtLeast(0)
            Rect(left, 0, (left + cropWidth).coerceAtMost(bitmap.width), bitmap.height)
        } else {
            val cropHeight = (bitmap.width / dstRatio).toInt().coerceAtLeast(1)
            val top = ((bitmap.height - cropHeight) / 2).coerceAtLeast(0)
            Rect(0, top, bitmap.width, (top + cropHeight).coerceAtMost(bitmap.height))
        }
        canvas.drawBitmap(bitmap, src, dst, paint)
    }

    private fun expand(rect: RectF, amount: Float): RectF =
        RectF(rect.left - amount, rect.top - amount, rect.right + amount, rect.bottom + amount)
}
