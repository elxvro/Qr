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

object QrCardReferenceRenderer {
    fun render(
        model: QrCardModel,
        qrBitmap: Bitmap,
        heroBitmap: Bitmap?,
        requestedLongEdge: Int
    ): Bitmap {
        val preset = requireNotNull(model.designPreset)
        val aspect = preset.aspectPreset
        val mode = preset.backgroundMode
        val theme = QrCardDesignColorPolicy.resolve(
            preset = preset,
            cardBackgroundArgb = model.cardBackgroundArgb,
            accentArgb = model.accentArgb,
            textArgb = model.textArgb,
            qrForegroundArgb = model.qrForegroundArgb,
            qrBackgroundArgb = model.qrBackgroundArgb
        )
        val copy = QrCardCopyPolicy.resolve(model)
        val textColors = QrCardTextColorPolicy.resolve(
            theme = theme,
            brandTextArgb = model.brandTextArgb,
            titleTextArgb = model.textArgb,
            bodyTextArgb = model.bodyTextArgb,
            ctaTextArgb = model.ctaTextArgb
        )
        val size = QrCardOutputSizePolicy.resolve(requestedLongEdge, model.cardAspectRatio)
        val output = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val layout = QrCardReferenceLayoutPolicy.resolve(size.width, size.height, aspect)
        val background = QrCardReferenceBackgroundPolicy.resolve(mode)
        val short = min(size.width, size.height).toFloat()

        drawReferenceBackground(
            canvas = canvas,
            paint = paint,
            theme = theme,
            background = background,
            heroBitmap = heroBitmap,
            width = size.width.toFloat(),
            height = size.height.toFloat(),
            short = short
        )
        drawQr(canvas, paint, qrBitmap, layout.qrRect.toRectF(), theme, short)
        drawCopy(canvas, paint, copy, textColors, layout, aspect, short)
        drawCta(canvas, paint, copy, textColors, theme, layout.ctaRect.toRectF(), short)

        return output
    }

    private fun drawReferenceBackground(
        canvas: Canvas,
        paint: Paint,
        theme: QrCardDesignTheme,
        background: QrCardReferenceBackground,
        heroBitmap: Bitmap?,
        width: Float,
        height: Float,
        short: Float
    ) {
        if (background.useUserPhoto && heroBitmap != null) {
            drawBitmapCenterCrop(canvas, paint, heroBitmap, RectF(0f, 0f, width, height))
            paint.shader = LinearGradient(
                0f,
                0f,
                0f,
                height,
                intArrayOf(
                    0x66000000,
                    0x22000000,
                    0x5A000000
                ),
                floatArrayOf(0f, 0.48f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawRect(0f, 0f, width, height, paint)
            paint.shader = null
            return
        }

        paint.shader = LinearGradient(
            0f,
            0f,
            width,
            height,
            blend(theme.backgroundArgb, 0xFF000000.toInt(), 0.10f),
            blend(theme.backgroundArgb, 0xFF18212B.toInt(), 0.18f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, width, height, paint)
        paint.shader = null

        if (background.useFixedDarkGold) {
            drawFixedGoldLines(canvas, paint, theme.accentArgb, width, height, short)
        } else {
            paint.color = withAlpha(theme.accentArgb, 0.07f)
            canvas.drawCircle(width * 0.90f, height * 0.08f, short * 0.18f, paint)
            canvas.drawCircle(width * 0.10f, height * 0.92f, short * 0.22f, paint)
        }
    }

    private fun drawFixedGoldLines(
        canvas: Canvas,
        paint: Paint,
        accent: Int,
        width: Float,
        height: Float,
        short: Float
    ) {
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND

        val top = Path().apply {
            moveTo(-short * 0.15f, height * 0.26f)
            cubicTo(
                width * 0.12f, height * 0.20f,
                width * 0.20f, height * 0.02f,
                width * 0.34f, -short * 0.05f
            )
        }
        paint.strokeWidth = max(2f, short * 0.012f)
        paint.color = withAlpha(accent, 0.92f)
        canvas.drawPath(top, paint)

        val bottom = Path().apply {
            moveTo(width * 0.30f, height + short * 0.04f)
            cubicTo(
                width * 0.52f, height * 0.80f,
                width * 0.72f, height * 0.98f,
                width + short * 0.10f, height * 0.70f
            )
        }
        paint.strokeWidth = max(3f, short * 0.018f)
        paint.color = withAlpha(accent, 0.78f)
        canvas.drawPath(bottom, paint)

        val bottomFine = Path().apply {
            moveTo(width * 0.22f, height + short * 0.02f)
            cubicTo(
                width * 0.50f, height * 0.73f,
                width * 0.74f, height * 0.92f,
                width + short * 0.08f, height * 0.62f
            )
        }
        paint.strokeWidth = max(1.5f, short * 0.006f)
        paint.color = withAlpha(accent, 0.42f)
        canvas.drawPath(bottomFine, paint)

        paint.style = Paint.Style.FILL
    }

    private fun drawQr(
        canvas: Canvas,
        paint: Paint,
        qrBitmap: Bitmap,
        rect: RectF,
        theme: QrCardDesignTheme,
        short: Float
    ) {
        val outer = short * 0.018f
        val radius = short * 0.028f

        paint.style = Paint.Style.FILL
        paint.color = withAlpha(0xFF000000.toInt(), 0.24f)
        canvas.drawRoundRect(
            RectF(rect.left + outer, rect.top + outer, rect.right + outer, rect.bottom + outer),
            radius,
            radius,
            paint
        )

        paint.color = theme.accentArgb
        canvas.drawRoundRect(expand(rect, outer), radius * 1.14f, radius * 1.14f, paint)

        paint.color = 0xFFFFFFFF.toInt()
        canvas.drawRoundRect(rect, radius, radius, paint)

        val inset = short * 0.018f
        canvas.drawBitmap(
            qrBitmap,
            null,
            RectF(rect.left + inset, rect.top + inset, rect.right - inset, rect.bottom - inset),
            paint
        )
    }

    private fun drawCopy(
        canvas: Canvas,
        paint: Paint,
        copy: QrCardCopy,
        colors: QrCardTextColors,
        layout: QrCardReferenceLayout,
        aspect: QrCardAspectPreset,
        short: Float
    ) {
        val centered = aspect == QrCardAspectPreset.SQUARE ||
            aspect == QrCardAspectPreset.PORTRAIT ||
            aspect == QrCardAspectPreset.STORY

        val align = if (centered) Paint.Align.CENTER else Paint.Align.LEFT
        val x = if (centered) {
            (layout.brandRect.left + layout.brandRect.right) / 2f
        } else {
            layout.brandRect.left
        }

        if (copy.brand.isNotBlank()) {
            paint.textAlign = align
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.color = colors.brandArgb
            paint.textSize = max(18f, short * if (centered) 0.070f else 0.062f)
            val baseline = layout.brandRect.centerBaseline(paint)
            val line = QrCardTextLayout.wrap(
                copy.brand,
                layout.brandRect.width,
                1,
                paint::measureText
            ).firstOrNull().orEmpty()
            canvas.drawText(line, x, baseline, paint)
        }

        val titleX = if (centered) {
            (layout.titleRect.left + layout.titleRect.right) / 2f
        } else {
            layout.titleRect.left
        }
        var y = layout.titleRect.top

        if (copy.title.isNotBlank()) {
            paint.textAlign = align
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.color = colors.titleArgb
            paint.textSize = max(16f, short * if (centered) 0.044f else 0.050f)
            y -= paint.ascent()
            QrCardTextLayout.wrap(copy.title, layout.titleRect.width, 2, paint::measureText)
                .forEach { line ->
                    if (y <= layout.titleRect.bottom) {
                        canvas.drawText(line, titleX, y, paint)
                        y += paint.textSize * 1.14f
                    }
                }
        }

        if (copy.description.isNotBlank() && y < layout.titleRect.bottom) {
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.color = colors.bodyArgb
            paint.textSize = max(13f, short * if (centered) 0.030f else 0.028f)
            y += paint.textSize * 0.34f
            QrCardTextLayout.wrap(copy.description, layout.titleRect.width, 3, paint::measureText)
                .forEach { line ->
                    if (y <= layout.titleRect.bottom) {
                        canvas.drawText(line, titleX, y, paint)
                        y += paint.textSize * 1.24f
                    }
                }
        }

        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawCta(
        canvas: Canvas,
        paint: Paint,
        copy: QrCardCopy,
        colors: QrCardTextColors,
        theme: QrCardDesignTheme,
        rect: RectF,
        short: Float
    ) {
        if (copy.cta.isBlank()) return

        paint.style = Paint.Style.FILL
        paint.color = theme.accentArgb
        canvas.drawRoundRect(rect, rect.height() / 2f, rect.height() / 2f, paint)

        paint.color = colors.ctaArgb
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = max(13f, min(short * 0.034f, rect.height() * 0.43f))
        paint.textAlign = Paint.Align.CENTER
        val baseline = rect.centerY() - (paint.ascent() + paint.descent()) / 2f
        val label = QrCardTextLayout.wrap(copy.cta, rect.width() * 0.82f, 1, paint::measureText)
            .firstOrNull()
            .orEmpty()
        canvas.drawText(label, rect.centerX(), baseline, paint)
        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawBitmapCenterCrop(
        canvas: Canvas,
        paint: Paint,
        bitmap: Bitmap,
        destination: RectF
    ) {
        if (bitmap.width <= 0 || bitmap.height <= 0) return
        val srcRatio = bitmap.width.toFloat() / bitmap.height
        val dstRatio = destination.width() / destination.height()
        val src = if (srcRatio > dstRatio) {
            val cropWidth = (bitmap.height * dstRatio).toInt().coerceAtLeast(1)
            val left = ((bitmap.width - cropWidth) / 2).coerceAtLeast(0)
            Rect(left, 0, (left + cropWidth).coerceAtMost(bitmap.width), bitmap.height)
        } else {
            val cropHeight = (bitmap.width / dstRatio).toInt().coerceAtLeast(1)
            val top = ((bitmap.height - cropHeight) / 2).coerceAtLeast(0)
            Rect(0, top, bitmap.width, (top + cropHeight).coerceAtMost(bitmap.height))
        }
        canvas.drawBitmap(bitmap, src, destination, paint)
    }

    private fun LayoutRect.toRectF() = RectF(left, top, right, bottom)

    private fun LayoutRect.centerBaseline(paint: Paint): Float =
        (top + bottom) / 2f - (paint.ascent() + paint.descent()) / 2f

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
}
