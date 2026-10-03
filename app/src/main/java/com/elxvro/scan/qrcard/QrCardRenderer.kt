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
        val layout = QrCardLayoutPolicy.resolve(width, height, model.qrPosition)
        val presentation = QrCardPresentationPolicy.forTemplate(model.template)
        val qrRect = layout.qrRect.toRectF()

        canvas.drawColor(model.cardBackgroundArgb)
        drawTemplateChrome(canvas, paint, model, presentation, width.toFloat(), height.toFloat(), qrRect)
        drawQrFrame(canvas, paint, model, presentation, qrRect, width.toFloat())

        paint.style = Paint.Style.FILL
        canvas.drawBitmap(qrBitmap, null, qrRect, paint)

        if (logoBitmap != null && model.logoMode != LogoMode.NONE) {
            drawQrLogo(canvas, paint, logoBitmap, qrRect, model.logoScaleFraction)
        }

        drawCardText(canvas, paint, model, presentation, layout.textRect)
        return output
    }

    private fun drawTemplateChrome(
        canvas: Canvas,
        paint: Paint,
        model: QrCardModel,
        presentation: QrCardPresentation,
        width: Float,
        height: Float,
        qrRect: RectF
    ) {
        val outer = width * 0.055f
        paint.style = Paint.Style.FILL
        paint.color = model.accentArgb

        when (presentation.accentStyle) {
            QrCardAccentStyle.RAIL -> {
                val railWidth = max(8f, width * 0.008f)
                canvas.drawRoundRect(
                    RectF(outer, outer, outer + railWidth, height - outer),
                    railWidth,
                    railWidth,
                    paint
                )
            }
            QrCardAccentStyle.HEADER_BAND -> {
                val bandHeight = max(10f, height * 0.025f)
                canvas.drawRect(0f, 0f, width, bandHeight, paint)
            }
            QrCardAccentStyle.NETWORK_BADGE -> {
                val badgeWidth = width * 0.18f
                val badgeHeight = max(28f, height * 0.052f)
                val badge = RectF(
                    outer,
                    outer * 0.58f,
                    outer + badgeWidth,
                    outer * 0.58f + badgeHeight
                )
                paint.color = withAlpha(model.accentArgb, 0.14f)
                canvas.drawRoundRect(badge, badgeHeight / 2f, badgeHeight / 2f, paint)
                paint.color = model.accentArgb
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textSize = badgeHeight * 0.45f
                paint.textAlign = Paint.Align.CENTER
                val baseline = badge.centerY() - (paint.ascent() + paint.descent()) / 2f
                canvas.drawText("WI-FI", badge.centerX(), baseline, paint)
                paint.textAlign = Paint.Align.LEFT
            }
            QrCardAccentStyle.PROFILE_RING -> {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = max(5f, width * 0.007f)
                paint.color = withAlpha(model.accentArgb, 0.75f)
                val inset = width * 0.025f
                canvas.drawRoundRect(
                    RectF(inset, inset, width - inset, height - inset),
                    width * 0.045f,
                    width * 0.045f,
                    paint
                )
                paint.style = Paint.Style.FILL
            }
            QrCardAccentStyle.EVENT_BAND -> {
                val bandHeight = max(12f, height * 0.028f)
                canvas.drawRect(0f, height - bandHeight, width, height, paint)
                paint.color = withAlpha(model.accentArgb, 0.10f)
                canvas.drawRoundRect(
                    RectF(outer, outer, width - outer, qrRect.top - outer * 0.35f),
                    width * 0.025f,
                    width * 0.025f,
                    paint
                )
            }
            QrCardAccentStyle.BRAND_STRIPE -> {
                val stripe = max(10f, width * 0.012f)
                canvas.drawRoundRect(
                    RectF(outer, outer, outer + stripe, height - outer),
                    stripe,
                    stripe,
                    paint
                )
                paint.color = withAlpha(model.accentArgb, 0.14f)
                canvas.drawRoundRect(
                    RectF(width * 0.56f, outer * 0.55f, width - outer, outer * 1.45f),
                    outer * 0.32f,
                    outer * 0.32f,
                    paint
                )
            }
            QrCardAccentStyle.PROMO_CORNER -> {
                paint.color = withAlpha(model.accentArgb, 0.16f)
                canvas.drawCircle(width * 0.10f, height * 0.12f, width * 0.14f, paint)
                paint.color = model.accentArgb
                canvas.drawRoundRect(
                    RectF(width * 0.72f, height * 0.04f, width * 0.94f, height * 0.085f),
                    height * 0.022f,
                    height * 0.022f,
                    paint
                )
            }
            QrCardAccentStyle.TICKET_STUB -> {
                val rail = max(10f, width * 0.012f)
                canvas.drawRect(0f, 0f, rail, height, paint)
                paint.color = withAlpha(model.accentArgb, 0.18f)
                canvas.drawRoundRect(
                    RectF(outer, height - outer * 1.55f, width - outer, height - outer * 0.55f),
                    outer * 0.22f,
                    outer * 0.22f,
                    paint
                )
                paint.color = model.cardBackgroundArgb
                val notch = max(14f, width * 0.018f)
                canvas.drawCircle(width * 0.53f, 0f, notch, paint)
                canvas.drawCircle(width * 0.53f, height, notch, paint)
            }
        }
    }

    private fun drawQrFrame(
        canvas: Canvas,
        paint: Paint,
        model: QrCardModel,
        presentation: QrCardPresentation,
        qrRect: RectF,
        width: Float
    ) {
        val radius = width * 0.018f
        val frame = width * 0.012f
        paint.style = Paint.Style.FILL

        when (presentation.qrFrameStyle) {
            QrCardQrFrameStyle.PLAIN -> {
                paint.color = model.qrBackgroundArgb
                canvas.drawRoundRect(qrRect, radius, radius, paint)
            }
            QrCardQrFrameStyle.BORDERED -> {
                paint.color = withAlpha(model.accentArgb, 0.78f)
                canvas.drawRoundRect(expand(qrRect, frame), radius * 1.25f, radius * 1.25f, paint)
                paint.color = model.qrBackgroundArgb
                canvas.drawRoundRect(qrRect, radius, radius, paint)
            }
            QrCardQrFrameStyle.ELEVATED -> {
                paint.color = 0x24000000
                canvas.drawRoundRect(
                    RectF(qrRect.left + frame, qrRect.top + frame, qrRect.right + frame, qrRect.bottom + frame),
                    radius,
                    radius,
                    paint
                )
                paint.color = model.qrBackgroundArgb
                canvas.drawRoundRect(qrRect, radius, radius, paint)
            }
            QrCardQrFrameStyle.RING -> {
                paint.color = model.accentArgb
                canvas.drawRoundRect(expand(qrRect, frame * 1.45f), radius * 1.35f, radius * 1.35f, paint)
                paint.color = model.qrBackgroundArgb
                canvas.drawRoundRect(qrRect, radius, radius, paint)
            }
            QrCardQrFrameStyle.EVENT -> {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = max(5f, width * 0.006f)
                paint.color = model.accentArgb
                canvas.drawRoundRect(expand(qrRect, frame * 0.72f), radius * 1.2f, radius * 1.2f, paint)
                paint.style = Paint.Style.FILL
                paint.color = model.qrBackgroundArgb
                canvas.drawRoundRect(qrRect, radius, radius, paint)
            }
        }
    }

    private fun drawQrLogo(
        canvas: Canvas,
        paint: Paint,
        logo: Bitmap,
        qrRect: RectF,
        logoScaleFraction: Float
    ) {
        val side = qrRect.width() * QrLogoPresentationPolicy.safeScale(logoScaleFraction)
        val backing = side * QrLogoPresentationPolicy.BACKING_FACTOR
        val cx = qrRect.centerX()
        val cy = qrRect.centerY()
        val backingRect = RectF(cx - backing / 2f, cy - backing / 2f, cx + backing / 2f, cy + backing / 2f)
        paint.style = Paint.Style.FILL
        paint.color = 0xFFFFFFFF.toInt()
        canvas.drawRoundRect(
            backingRect,
            backing * QrLogoPresentationPolicy.CORNER_FACTOR,
            backing * QrLogoPresentationPolicy.CORNER_FACTOR,
            paint
        )
        val logoRect = RectF(cx - side / 2f, cy - side / 2f, cx + side / 2f, cy + side / 2f)
        canvas.drawBitmap(logo, null, logoRect, paint)
    }

    private fun drawCardText(
        canvas: Canvas,
        paint: Paint,
        model: QrCardModel,
        presentation: QrCardPresentation,
        textRect: LayoutRect
    ) {
        val base = max(24f, min(textRect.width * 0.055f, textRect.height * 0.18f))
        val lineHeight = base * 1.08f
        val center = presentation.titleAlignment == QrCardTextAlignment.CENTER
        val x = if (center) (textRect.left + textRect.right) / 2f else textRect.left
        var y = textRect.top + base * 1.15f

        paint.textAlign = if (center) Paint.Align.CENTER else Paint.Align.LEFT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = model.textArgb
        paint.textSize = base * 1.22f

        y = drawWrapped(
            canvas = canvas,
            paint = paint,
            text = model.title.ifBlank { defaultTitle(model.template) },
            x = x,
            startBaseline = y,
            maxWidth = textRect.width,
            maxLines = presentation.titleMaxLines,
            lineHeight = lineHeight,
            bottom = textRect.bottom
        )

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = withAlpha(model.textArgb, 0.72f)
        paint.textSize = base * 0.70f

        if (model.subtitle.isNotBlank() && y + lineHeight < textRect.bottom) {
            y += base * 0.25f
            y = drawWrapped(
                canvas,
                paint,
                model.subtitle,
                x,
                y + lineHeight,
                textRect.width,
                maxLines = 2,
                lineHeight = lineHeight * 0.88f,
                bottom = textRect.bottom
            )
        }

        val detailLines = when (model.template) {
            QrCardTemplate.MINIMAL -> listOf(model.contactLine)
            QrCardTemplate.CORPORATE -> listOf(model.contactLine)
            QrCardTemplate.WIFI -> listOf(
                model.wifiSsid.takeIf { it.isNotBlank() }?.let { "Ağ: $it" }.orEmpty(),
                "Bağlanmak için QR kodu okut"
            )
            QrCardTemplate.SOCIAL -> listOf(model.socialHandle, model.contactLine)
            QrCardTemplate.EVENT -> listOf(model.eventDate, model.eventLocation)
            QrCardTemplate.BUSINESS -> listOf(model.contactLine)
            QrCardTemplate.PROMO -> listOf(model.contactLine)
            QrCardTemplate.TICKET -> listOf(model.eventDate, model.eventLocation)
        }.filter { it.isNotBlank() }

        var remainingBodyLines = presentation.bodyMaxLines
        paint.textSize = base * 0.66f
        detailLines.forEachIndexed { index, line ->
            if (remainingBodyLines <= 0 || y + lineHeight >= textRect.bottom) return@forEachIndexed
            y += base * 0.18f
            paint.color = if (
                (model.template == QrCardTemplate.EVENT || model.template == QrCardTemplate.TICKET) &&
                index == 0
            ) {
                model.accentArgb
            } else {
                withAlpha(model.textArgb, 0.78f)
            }
            val wrapped = QrCardTextLayout.wrap(
                text = line,
                maxWidth = textRect.width,
                maxLines = remainingBodyLines,
                measure = paint::measureText
            )
            wrapped.forEach { value ->
                if (y + lineHeight < textRect.bottom) {
                    y += lineHeight
                    canvas.drawText(value, x, y, paint)
                    remainingBodyLines -= 1
                }
            }
        }
        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawWrapped(
        canvas: Canvas,
        paint: Paint,
        text: String,
        x: Float,
        startBaseline: Float,
        maxWidth: Float,
        maxLines: Int,
        lineHeight: Float,
        bottom: Float
    ): Float {
        var y = startBaseline
        val lines = QrCardTextLayout.wrap(text, maxWidth, maxLines, paint::measureText)
        lines.forEachIndexed { index, line ->
            if (index > 0) y += lineHeight
            if (y <= bottom) canvas.drawText(line, x, y, paint)
        }
        return y
    }

    private fun defaultTitle(template: QrCardTemplate): String = when (template) {
        QrCardTemplate.MINIMAL -> "ELXVRO"
        QrCardTemplate.CORPORATE -> "Kurumsal QR"
        QrCardTemplate.WIFI -> "Wi-Fi"
        QrCardTemplate.SOCIAL -> "Sosyal Medya"
        QrCardTemplate.EVENT -> "Etkinlik"
        QrCardTemplate.BUSINESS -> "Business"
        QrCardTemplate.PROMO -> "Kampanya"
        QrCardTemplate.TICKET -> "Bilet"
    }

    private fun expand(rect: RectF, amount: Float): RectF = RectF(
        rect.left - amount,
        rect.top - amount,
        rect.right + amount,
        rect.bottom + amount
    )

    private fun withAlpha(color: Int, alpha: Float): Int {
        val a = (255 * alpha.coerceIn(0f, 1f)).toInt()
        return (color and 0x00FFFFFF) or (a shl 24)
    }

    private fun LayoutRect.toRectF(): RectF = RectF(left, top, right, bottom)
}
