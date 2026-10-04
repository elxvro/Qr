package com.elxvro.scan.qrcard

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import kotlin.math.max
import kotlin.math.min

object QrCardV3Renderer {
    fun render(
        model: QrCardModel,
        qrBitmap: Bitmap,
        heroBitmap: Bitmap?,
        requestedLongEdge: Int
    ): Bitmap {
        val preset = requireNotNull(model.designPreset)
        val theme = preset.theme()
        val size = QrCardOutputSizePolicy.resolve(requestedLongEdge, model.cardAspectRatio)
        val output = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val layout = QrCardV3LayoutPolicy.resolve(size.width, size.height, preset)
        val short = min(size.width, size.height).toFloat()

        drawBackground(canvas, paint, theme, size.width.toFloat(), size.height.toFloat())
        drawHero(canvas, paint, heroBitmap, layout.imageRect.toRectF(), theme, preset, short)
        drawDecor(canvas, paint, theme, preset, size.width.toFloat(), size.height.toFloat(), short)
        drawQr(canvas, paint, qrBitmap, layout.qrRect.toRectF(), theme, short)
        drawText(canvas, paint, model, theme, layout.textRect, short)
        drawCta(canvas, paint, theme, layout.ctaRect.toRectF(), short)

        return output
    }

    private fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        theme: QrCardDesignTheme,
        width: Float,
        height: Float
    ) {
        paint.shader = LinearGradient(
            0f,
            0f,
            width,
            height,
            theme.backgroundArgb,
            blend(theme.backgroundArgb, theme.accentArgb, 0.10f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, width, height, paint)
        paint.shader = null
    }

    private fun drawHero(
        canvas: Canvas,
        paint: Paint,
        heroBitmap: Bitmap?,
        rect: RectF,
        theme: QrCardDesignTheme,
        preset: QrCardDesignPreset,
        short: Float
    ) {
        val radius = short * 0.035f
        canvas.save()
        val clip = Path().apply { addRoundRect(rect, radius, radius, Path.Direction.CW) }
        canvas.clipPath(clip)

        if (heroBitmap != null) {
            drawBitmapCenterCrop(canvas, paint, heroBitmap, rect)
        } else {
            paint.shader = LinearGradient(
                rect.left,
                rect.top,
                rect.right,
                rect.bottom,
                blend(theme.accentArgb, 0xFFFFFFFF.toInt(), 0.18f),
                blend(theme.backgroundArgb, 0xFF000000.toInt(), 0.28f),
                Shader.TileMode.CLAMP
            )
            canvas.drawRect(rect, paint)
            paint.shader = null
            paint.color = withAlpha(theme.accentArgb, 0.18f)
            val step = max(20f, short * 0.08f)
            var x = rect.left - rect.height()
            while (x < rect.right + rect.height()) {
                canvas.drawRect(x, rect.top, x + step * 0.20f, rect.bottom, paint)
                x += step
            }
        }

        val overlayAlpha = when (preset) {
            QrCardDesignPreset.SQUARE_PHOTO,
            QrCardDesignPreset.WIDE_CINEMATIC,
            QrCardDesignPreset.PORTRAIT_CAMPAIGN,
            QrCardDesignPreset.STORY_LUXURY -> theme.photoOverlayAlpha
            else -> theme.photoOverlayAlpha * 0.45f
        }
        if (overlayAlpha > 0f) {
            paint.color = withAlpha(0xFF000000.toInt(), overlayAlpha)
            canvas.drawRect(rect, paint)
        }
        canvas.restore()
    }

    private fun drawDecor(
        canvas: Canvas,
        paint: Paint,
        theme: QrCardDesignTheme,
        preset: QrCardDesignPreset,
        width: Float,
        height: Float,
        short: Float
    ) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = max(3f, short * 0.008f)
        paint.color = withAlpha(theme.accentArgb, 0.88f)

        val path = Path()
        if (preset.ordinal % 2 == 0) {
            path.moveTo(-short * 0.06f, height * 0.70f)
            path.cubicTo(
                width * 0.24f, height * 0.52f,
                width * 0.56f, height * 0.84f,
                width + short * 0.06f, height * 0.58f
            )
        } else {
            path.moveTo(width * 0.18f, -short * 0.03f)
            path.cubicTo(
                width * 0.34f, height * 0.22f,
                width * 0.62f, height * 0.18f,
                width + short * 0.05f, height * 0.38f
            )
        }
        canvas.drawPath(path, paint)

        paint.style = Paint.Style.FILL
        paint.color = withAlpha(theme.accentArgb, 0.10f)
        canvas.drawCircle(width * 0.92f, height * 0.10f, short * 0.12f, paint)
        canvas.drawCircle(width * 0.08f, height * 0.92f, short * 0.16f, paint)
    }

    private fun drawQr(
        canvas: Canvas,
        paint: Paint,
        qrBitmap: Bitmap,
        rect: RectF,
        theme: QrCardDesignTheme,
        short: Float
    ) {
        val frame = short * 0.018f
        val radius = short * 0.032f
        paint.style = Paint.Style.FILL
        paint.color = withAlpha(0xFF000000.toInt(), 0.22f)
        canvas.drawRoundRect(
            RectF(rect.left + frame, rect.top + frame, rect.right + frame, rect.bottom + frame),
            radius,
            radius,
            paint
        )
        paint.color = theme.accentArgb
        canvas.drawRoundRect(expand(rect, frame), radius * 1.15f, radius * 1.15f, paint)
        paint.color = theme.qrBackgroundArgb
        canvas.drawRoundRect(rect, radius, radius, paint)

        val inset = short * 0.012f
        canvas.drawBitmap(
            qrBitmap,
            null,
            RectF(rect.left + inset, rect.top + inset, rect.right - inset, rect.bottom - inset),
            paint
        )
    }

    private fun drawText(
        canvas: Canvas,
        paint: Paint,
        model: QrCardModel,
        theme: QrCardDesignTheme,
        textRect: LayoutRect,
        short: Float
    ) {
        val x = textRect.left
        var y = textRect.top
        val width = textRect.width

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = theme.accentArgb
        paint.textSize = max(15f, short * 0.035f)
        y -= paint.ascent()
        canvas.drawText("ELXVRO", x, y, paint)

        val titleSize = max(24f, min(short * 0.085f, textRect.height * 0.25f))
        paint.color = theme.titleArgb
        paint.textSize = titleSize
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        y += titleSize * 1.15f

        val title = model.title.ifBlank { defaultTitle(model.template) }
        val titleLines = QrCardTextLayout.wrap(title, width, 2, paint::measureText)
        titleLines.forEach { line ->
            if (y <= textRect.bottom) canvas.drawText(line, x, y, paint)
            y += titleSize * 1.05f
        }

        if (model.subtitle.isNotBlank() && y < textRect.bottom) {
            paint.color = theme.accentArgb
            paint.textSize = titleSize * 0.48f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val lines = QrCardTextLayout.wrap(model.subtitle, width, 2, paint::measureText)
            lines.forEach { line ->
                if (y <= textRect.bottom) canvas.drawText(line, x, y, paint)
                y += titleSize * 0.58f
            }
        }

        val details = detailLines(model)
        paint.color = theme.bodyArgb
        paint.textSize = max(15f, titleSize * 0.38f)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        details.take(3).forEach { detail ->
            if (detail.isBlank() || y > textRect.bottom) return@forEach
            val lines = QrCardTextLayout.wrap(detail, width, 1, paint::measureText)
            lines.firstOrNull()?.let { line ->
                canvas.drawText(line, x, y, paint)
                y += titleSize * 0.50f
            }
        }
        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawCta(
        canvas: Canvas,
        paint: Paint,
        theme: QrCardDesignTheme,
        rect: RectF,
        short: Float
    ) {
        val radius = rect.height() / 2f
        paint.style = Paint.Style.FILL
        paint.shader = LinearGradient(
            rect.left,
            rect.top,
            rect.right,
            rect.bottom,
            blend(theme.accentArgb, 0xFFFFFFFF.toInt(), 0.28f),
            theme.accentArgb,
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rect, radius, radius, paint)
        paint.shader = null

        paint.color = readableOn(theme.accentArgb)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = max(14f, min(short * 0.034f, rect.height() * 0.42f))
        paint.textAlign = Paint.Align.CENTER
        val baseline = rect.centerY() - (paint.ascent() + paint.descent()) / 2f
        canvas.drawText("Scan to explore  →", rect.centerX(), baseline, paint)
        paint.textAlign = Paint.Align.LEFT
    }

    private fun detailLines(model: QrCardModel): List<String> = when (model.template) {
        QrCardTemplate.MINIMAL,
        QrCardTemplate.CORPORATE,
        QrCardTemplate.BUSINESS,
        QrCardTemplate.PROMO -> listOf(model.contactLine)
        QrCardTemplate.WIFI -> listOf(
            model.wifiSsid.takeIf { it.isNotBlank() }?.let { "Wi-Fi • $it" }.orEmpty()
        )
        QrCardTemplate.SOCIAL -> listOf(model.socialHandle, model.contactLine)
        QrCardTemplate.EVENT,
        QrCardTemplate.TICKET -> listOf(model.eventDate, model.eventLocation)
    }.filter { it.isNotBlank() }

    private fun defaultTitle(template: QrCardTemplate): String = when (template) {
        QrCardTemplate.MINIMAL -> "Premium QR Card"
        QrCardTemplate.CORPORATE -> "Kurumsal QR"
        QrCardTemplate.WIFI -> "Wi-Fi"
        QrCardTemplate.SOCIAL -> "Sosyal Medya"
        QrCardTemplate.EVENT -> "Etkinlik"
        QrCardTemplate.BUSINESS -> "Business"
        QrCardTemplate.PROMO -> "Kampanya"
        QrCardTemplate.TICKET -> "Bilet"
    }

    private fun drawBitmapCenterCrop(
        canvas: Canvas,
        paint: Paint,
        bitmap: Bitmap,
        destination: RectF
    ) {
        val srcWidth = bitmap.width
        val srcHeight = bitmap.height
        if (srcWidth <= 0 || srcHeight <= 0) return

        val srcRatio = srcWidth.toFloat() / srcHeight
        val dstRatio = destination.width() / destination.height()
        val src = if (srcRatio > dstRatio) {
            val cropWidth = (srcHeight * dstRatio).toInt().coerceAtLeast(1)
            val left = ((srcWidth - cropWidth) / 2).coerceAtLeast(0)
            Rect(left, 0, (left + cropWidth).coerceAtMost(srcWidth), srcHeight)
        } else {
            val cropHeight = (srcWidth / dstRatio).toInt().coerceAtLeast(1)
            val top = ((srcHeight - cropHeight) / 2).coerceAtLeast(0)
            Rect(0, top, srcWidth, (top + cropHeight).coerceAtMost(srcHeight))
        }
        canvas.drawBitmap(bitmap, src, destination, paint)
    }

    private fun LayoutRect.toRectF() = RectF(left, top, right, bottom)

    private fun expand(rect: RectF, amount: Float) =
        RectF(rect.left - amount, rect.top - amount, rect.right + amount, rect.bottom + amount)

    private fun withAlpha(color: Int, alpha: Float): Int {
        val a = (255 * alpha.coerceIn(0f, 1f)).toInt()
        return (color and 0x00FFFFFF) or (a shl 24)
    }

    private fun blend(a: Int, b: Int, fraction: Float): Int {
        val f = fraction.coerceIn(0f, 1f)
        fun channel(shift: Int): Int {
            val ca = (a shr shift) and 0xFF
            val cb = (b shr shift) and 0xFF
            return (ca + ((cb - ca) * f)).toInt().coerceIn(0, 255)
        }
        return (0xFF shl 24) or
            (channel(16) shl 16) or
            (channel(8) shl 8) or
            channel(0)
    }

    private fun readableOn(color: Int): Int {
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        val luminance = 0.299 * r + 0.587 * g + 0.114 * b
        return if (luminance > 150) 0xFF111318.toInt() else 0xFFFFFFFF.toInt()
    }
}
