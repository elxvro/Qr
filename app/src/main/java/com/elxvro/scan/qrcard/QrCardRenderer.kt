package com.elxvro.scan.qrcard

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import kotlin.math.max
import kotlin.math.min

object QrCardRenderer {
    fun render(
        model: QrCardModel,
        qrBitmap: Bitmap,
        logoBitmap: Bitmap? = null,
        outputWidth: Int = 1600
    ): Bitmap {
        require(QrCardValidator.validate(model) is ValidationResult.Valid) {
            "QR Card model is not safe to render"
        }
        require(outputWidth in 512..4096) { "Unsupported output width" }

        val width = outputWidth
        val height = max(512, (width / model.cardAspectRatio).toInt())
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        canvas.drawColor(model.cardBackgroundArgb)

        val outer = width * 0.055f
        val accentWidth = max(8f, width * 0.008f)
        paint.color = model.accentArgb
        canvas.drawRoundRect(
            RectF(outer, outer, outer + accentWidth, height - outer),
            accentWidth,
            accentWidth,
            paint
        )

        val layout = QrCardLayoutPolicy.resolve(width, height, model.qrPosition)
        val qrRect = layout.qrRect.toRectF()
        paint.color = model.qrBackgroundArgb
        canvas.drawRoundRect(qrRect, width * 0.018f, width * 0.018f, paint)
        canvas.drawBitmap(qrBitmap, null, qrRect, paint)

        if (logoBitmap != null && model.logoMode != LogoMode.NONE) {
            drawQrLogo(canvas, paint, logoBitmap, qrRect, model.logoScaleFraction)
        }

        drawCardText(canvas, paint, model, layout.textRect)

        return output
    }

    private fun drawQrLogo(
        canvas: Canvas,
        paint: Paint,
        logo: Bitmap,
        qrRect: RectF,
        logoScaleFraction: Float
    ) {
        val side = qrRect.width() * logoScaleFraction.coerceAtMost(QrCardValidator.MAX_LOGO_SCALE)
        val backing = side * 1.24f
        val cx = qrRect.centerX()
        val cy = qrRect.centerY()
        val backingRect = RectF(cx - backing / 2f, cy - backing / 2f, cx + backing / 2f, cy + backing / 2f)
        paint.color = 0xFFFFFFFF.toInt()
        canvas.drawRoundRect(backingRect, backing * 0.18f, backing * 0.18f, paint)
        val logoRect = RectF(cx - side / 2f, cy - side / 2f, cx + side / 2f, cy + side / 2f)
        canvas.drawBitmap(logo, null, logoRect, paint)
    }

    private fun drawCardText(
        canvas: Canvas,
        paint: Paint,
        model: QrCardModel,
        textRect: LayoutRect
    ) {
        val base = max(24f, min(textRect.width * 0.055f, textRect.height * 0.18f))
        var y = textRect.top + base * 1.15f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = model.textArgb
        paint.textSize = base * 1.25f
        y = drawEllipsized(
            canvas = canvas,
            paint = paint,
            text = model.title.ifBlank { defaultTitle(model.template) },
            x = textRect.left,
            baseline = y,
            maxWidth = textRect.width
        )

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = withAlpha(model.textArgb, 0.72f)
        paint.textSize = base * 0.72f
        if (model.subtitle.isNotBlank() && y + base * 0.9f < textRect.bottom) {
            y += base * 0.9f
            y = drawEllipsized(canvas, paint, model.subtitle, textRect.left, y, textRect.width)
        }

        val lines = when (model.template) {
            QrCardTemplate.MINIMAL -> listOf(model.contactLine)
            QrCardTemplate.CORPORATE -> listOf(model.contactLine)
            QrCardTemplate.WIFI -> listOf(
                model.wifiSsid.takeIf { it.isNotBlank() }?.let { "Ağ: $it" }.orEmpty(),
                "Bağlanmak için QR kodu okut"
            )
            QrCardTemplate.SOCIAL -> listOf(model.socialHandle, model.contactLine)
            QrCardTemplate.EVENT -> listOf(model.eventDate, model.eventLocation)
        }.filter { it.isNotBlank() }

        paint.textSize = base * 0.66f
        lines.forEach { line ->
            if (y + base * 1.05f < textRect.bottom) {
                y += base * 1.05f
                y = drawEllipsized(canvas, paint, line, textRect.left, y, textRect.width)
            }
        }
    }

    private fun drawEllipsized(
        canvas: Canvas,
        paint: Paint,
        text: String,
        x: Float,
        baseline: Float,
        maxWidth: Float
    ): Float {
        if (text.isBlank()) return baseline
        var value = text
        if (paint.measureText(value) > maxWidth) {
            while (value.length > 1 && paint.measureText("$value…") > maxWidth) {
                value = value.dropLast(1)
            }
            value += "…"
        }
        canvas.drawText(value, x, baseline, paint)
        return baseline
    }

    private fun defaultTitle(template: QrCardTemplate): String = when (template) {
        QrCardTemplate.MINIMAL -> "ELXVRO"
        QrCardTemplate.CORPORATE -> "Kurumsal QR"
        QrCardTemplate.WIFI -> "Wi-Fi"
        QrCardTemplate.SOCIAL -> "Sosyal Medya"
        QrCardTemplate.EVENT -> "Etkinlik"
    }

    private fun withAlpha(color: Int, alpha: Float): Int {
        val a = (255 * alpha.coerceIn(0f, 1f)).toInt()
        return (color and 0x00FFFFFF) or (a shl 24)
    }

    private fun LayoutRect.toRectF(): RectF = RectF(left, top, right, bottom)
}
