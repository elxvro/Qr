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

        val qrSize = when {
            model.cardAspectRatio < 0.9f -> min(width * 0.68f, height * 0.42f)
            model.cardAspectRatio < 1.15f -> min(width * 0.58f, height * 0.58f)
            else -> min(width * 0.39f, height * 0.72f)
        }.toInt().coerceAtLeast(256)

        val qrLeft = when {
            model.cardAspectRatio > 1.15f -> width - outer - qrSize
            else -> (width - qrSize) / 2f
        }
        val qrTop = when (model.qrPosition) {
            QrPosition.TOP -> outer + width * 0.04f
            QrPosition.CENTER -> (height - qrSize) / 2f
            QrPosition.BOTTOM -> height - outer - qrSize
        }.coerceIn(outer, height - outer - qrSize)

        val qrRect = RectF(qrLeft, qrTop, qrLeft + qrSize, qrTop + qrSize)
        paint.color = model.qrBackgroundArgb
        canvas.drawRoundRect(qrRect, width * 0.018f, width * 0.018f, paint)
        canvas.drawBitmap(qrBitmap, null, qrRect, paint)

        if (logoBitmap != null && model.logoMode != LogoMode.NONE) {
            drawQrLogo(canvas, paint, logoBitmap, qrRect, model.logoScaleFraction)
        }

        val contentLeft = outer + accentWidth + width * 0.035f
        val contentRight = if (model.cardAspectRatio > 1.15f) qrLeft - width * 0.04f else width - outer
        val contentWidth = (contentRight - contentLeft).coerceAtLeast(width * 0.4f)
        drawCardText(canvas, paint, model, contentLeft, contentWidth, outer, height.toFloat())

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
        left: Float,
        availableWidth: Float,
        outer: Float,
        cardHeight: Float
    ) {
        val base = max(28f, availableWidth * 0.055f)
        var y = outer + base * 1.6f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = model.textArgb
        paint.textSize = base * 1.25f
        y = drawEllipsized(canvas, paint, model.title.ifBlank { defaultTitle(model.template) }, left, y, availableWidth)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = withAlpha(model.textArgb, 0.72f)
        paint.textSize = base * 0.72f
        if (model.subtitle.isNotBlank()) {
            y += base * 0.45f
            y = drawEllipsized(canvas, paint, model.subtitle, left, y, availableWidth)
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
            if (y < cardHeight - outer - base) {
                y += base * 1.05f
                y = drawEllipsized(canvas, paint, line, left, y, availableWidth)
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
}
