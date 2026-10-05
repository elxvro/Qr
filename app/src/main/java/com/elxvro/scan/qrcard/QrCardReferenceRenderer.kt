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
        val fixedPreset = FixedCardLibrary.find(model.backgroundPresetId)
        val editableTheme = QrCardDesignColorPolicy.resolve(
            preset = preset,
            cardBackgroundArgb = model.cardBackgroundArgb,
            accentArgb = model.accentArgb,
            textArgb = model.textArgb,
            qrForegroundArgb = model.qrForegroundArgb,
            qrBackgroundArgb = model.qrBackgroundArgb
        )
        val theme = if (mode == QrCardBackgroundMode.FIXED_BACKGROUND) {
            QrCardDesignTheme(
                backgroundArgb = fixedPreset.startArgb,
                accentArgb = fixedPreset.accentArgb,
                titleArgb = fixedPreset.titleArgb,
                bodyArgb = fixedPreset.bodyArgb,
                qrForegroundArgb = 0xFF0A0D12.toInt(),
                qrBackgroundArgb = 0xFFFFFFFF.toInt(),
                photoOverlayAlpha = 0f
            )
        } else {
            editableTheme
        }
        val copy = QrCardCopyPolicy.resolve(model)
        val textColors = if (mode == QrCardBackgroundMode.FIXED_BACKGROUND) {
            QrCardTextColors(
                brandArgb = fixedPreset.accentArgb,
                titleArgb = fixedPreset.titleArgb,
                bodyArgb = fixedPreset.bodyArgb,
                ctaArgb = fixedPreset.ctaTextArgb
            )
        } else {
            QrCardTextColorPolicy.resolve(
                theme = theme,
                brandTextArgb = model.brandTextArgb,
                titleTextArgb = model.textArgb,
                bodyTextArgb = model.bodyTextArgb,
                ctaTextArgb = model.ctaTextArgb
            )
        }
        val size = QrCardOutputSizePolicy.resolve(requestedLongEdge, model.cardAspectRatio)
        val output = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val fixedLayout = if (mode == QrCardBackgroundMode.FIXED_BACKGROUND) {
            FixedCardLayoutPolicy.resolve(size.width, size.height, fixedPreset.layout)
        } else {
            null
        }
        val layout = fixedLayout?.let { fixed ->
            QrCardReferenceLayout(
                brandRect = fixed.brandRect,
                titleRect = fixed.titleRect,
                qrRect = fixed.qrRect,
                ctaRect = fixed.ctaRect
            )
        } ?: QrCardReferenceLayoutPolicy.resolve(size.width, size.height, aspect)
        val background = QrCardReferenceBackgroundPolicy.resolve(mode)
        val short = min(size.width, size.height).toFloat()

        drawReferenceBackground(
            canvas = canvas,
            paint = paint,
            theme = theme,
            background = background,
            fixedPreset = fixedPreset,
            heroBitmap = heroBitmap,
            width = size.width.toFloat(),
            height = size.height.toFloat(),
            short = short
        )
        drawQr(canvas, paint, qrBitmap, layout.qrRect.toRectF(), theme, short)
        drawCopy(
            canvas = canvas,
            paint = paint,
            copy = copy,
            colors = textColors,
            layout = layout,
            aspect = aspect,
            short = short,
            fixedAlignment = fixedLayout?.textAlignment
        )
        drawCta(canvas, paint, copy, textColors, theme, layout.ctaRect.toRectF(), short)

        return output
    }

    private fun drawReferenceBackground(
        canvas: Canvas,
        paint: Paint,
        theme: QrCardDesignTheme,
        background: QrCardReferenceBackground,
        fixedPreset: FixedCardPreset,
        heroBitmap: Bitmap?,
        width: Float,
        height: Float,
        short: Float
    ) {
        if (background.useFixedDarkGold) {
            FixedCardVisualSceneRenderer.draw(
                canvas = canvas,
                paint = paint,
                preset = fixedPreset,
                width = width,
                height = height
            )
            return
        }

        if (background.useUserPhoto && heroBitmap != null) {
            drawBitmapCenterCrop(canvas, paint, heroBitmap, RectF(0f, 0f, width, height))
            paint.shader = LinearGradient(
                0f,
                0f,
                0f,
                height,
                intArrayOf(
                    withAlpha(theme.backgroundArgb, 0.58f),
                    withAlpha(theme.backgroundArgb, 0.18f),
                    withAlpha(theme.backgroundArgb, 0.64f)
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

        paint.color = withAlpha(theme.accentArgb, 0.07f)
        canvas.drawCircle(width * 0.90f, height * 0.08f, short * 0.18f, paint)
        canvas.drawCircle(width * 0.10f, height * 0.92f, short * 0.22f, paint)
    }

    private fun drawPremiumPattern(
        canvas: Canvas,
        paint: Paint,
        preset: FixedCardPreset,
        width: Float,
        height: Float,
        short: Float
    ) {
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = withAlpha(preset.accentArgb, 0.72f)

        when (preset.pattern) {
            FixedCardPattern.SWEEP -> {
                paint.strokeWidth = max(2f, short * 0.010f)
                val p1 = Path().apply {
                    moveTo(-short * 0.10f, height * 0.30f)
                    cubicTo(width * 0.22f, height * 0.08f, width * 0.55f, height * 0.18f, width + short * 0.08f, height * 0.02f)
                }
                canvas.drawPath(p1, paint)
                paint.color = withAlpha(preset.accentArgb, 0.34f)
                val p2 = Path().apply {
                    moveTo(width * 0.18f, height + short * 0.04f)
                    cubicTo(width * 0.48f, height * 0.70f, width * 0.76f, height * 0.96f, width + short * 0.08f, height * 0.62f)
                }
                canvas.drawPath(p2, paint)
            }
            FixedCardPattern.RINGS -> {
                paint.strokeWidth = max(2f, short * 0.008f)
                listOf(0.20f, 0.31f, 0.44f).forEachIndexed { index, radius ->
                    paint.color = withAlpha(preset.accentArgb, 0.18f + index * 0.14f)
                    canvas.drawCircle(width * 0.84f, height * 0.18f, short * radius, paint)
                }
                listOf(0.17f, 0.28f).forEachIndexed { index, radius ->
                    paint.color = withAlpha(preset.accentArgb, 0.18f + index * 0.12f)
                    canvas.drawCircle(width * 0.12f, height * 0.88f, short * radius, paint)
                }
            }
            FixedCardPattern.DIAGONAL -> {
                paint.strokeWidth = max(2f, short * 0.007f)
                var offset = -height
                var index = 0
                while (offset < width + height) {
                    paint.color = withAlpha(preset.accentArgb, if (index % 3 == 0) 0.34f else 0.12f)
                    canvas.drawLine(offset, height, offset + height, 0f, paint)
                    offset += short * 0.15f
                    index++
                }
            }
            FixedCardPattern.FRAME -> {
                paint.strokeWidth = max(2f, short * 0.010f)
                val inset = short * 0.055f
                canvas.drawRoundRect(
                    RectF(inset, inset, width - inset, height - inset),
                    short * 0.05f,
                    short * 0.05f,
                    paint
                )
                paint.color = withAlpha(preset.accentArgb, 0.28f)
                val inset2 = inset + short * 0.035f
                canvas.drawRoundRect(
                    RectF(inset2, inset2, width - inset2, height - inset2),
                    short * 0.04f,
                    short * 0.04f,
                    paint
                )
            }
            FixedCardPattern.HORIZON -> {
                paint.strokeWidth = max(2f, short * 0.008f)
                val baseY = height * 0.72f
                repeat(5) { index ->
                    paint.color = withAlpha(preset.accentArgb, 0.12f + index * 0.09f)
                    val y = baseY + index * short * 0.045f
                    canvas.drawLine(0f, y, width, y - short * 0.12f, paint)
                }
                paint.color = withAlpha(preset.accentArgb, 0.56f)
                canvas.drawLine(0f, baseY, width, baseY - short * 0.12f, paint)
            }
            FixedCardPattern.FACETS -> {
                paint.strokeWidth = max(2f, short * 0.007f)
                val points = listOf(
                    0f to height * 0.18f,
                    width * 0.24f to 0f,
                    width * 0.58f to height * 0.25f,
                    width to height * 0.06f,
                    width * 0.80f to height * 0.62f,
                    width to height,
                    width * 0.45f to height * 0.82f,
                    0f to height
                )
                for (i in 0 until points.lastIndex) {
                    paint.color = withAlpha(preset.accentArgb, if (i % 2 == 0) 0.30f else 0.14f)
                    canvas.drawLine(points[i].first, points[i].second, points[i + 1].first, points[i + 1].second, paint)
                }
                paint.color = withAlpha(preset.accentArgb, 0.18f)
                canvas.drawLine(points[1].first, points[1].second, points[6].first, points[6].second, paint)
                canvas.drawLine(points[2].first, points[2].second, points[4].first, points[4].second, paint)
            }
            FixedCardPattern.GRID -> {
                paint.strokeWidth = max(1.5f, short * 0.004f)
                val step = short * 0.12f
                var x = -step
                while (x < width + step) {
                    paint.color = withAlpha(preset.accentArgb, 0.12f)
                    canvas.drawLine(x, 0f, x, height, paint)
                    x += step
                }
                var y = -step
                while (y < height + step) {
                    canvas.drawLine(0f, y, width, y, paint)
                    y += step
                }
                paint.color = withAlpha(preset.accentArgb, 0.44f)
                canvas.drawLine(width * 0.08f, height * 0.12f, width * 0.92f, height * 0.12f, paint)
            }
            FixedCardPattern.ORBIT -> {
                paint.strokeWidth = max(2f, short * 0.006f)
                repeat(3) { index ->
                    paint.color = withAlpha(preset.accentArgb, 0.22f + index * 0.08f)
                    canvas.drawOval(
                        RectF(
                            width * 0.52f - short * (0.20f + index * 0.09f),
                            height * 0.48f - short * (0.11f + index * 0.06f),
                            width * 0.52f + short * (0.20f + index * 0.09f),
                            height * 0.48f + short * (0.11f + index * 0.06f)
                        ),
                        paint
                    )
                }
            }
            FixedCardPattern.WAVE -> {
                paint.strokeWidth = max(2f, short * 0.008f)
                repeat(3) { index ->
                    val y = height * (0.72f + index * 0.06f)
                    val wave = Path().apply {
                        moveTo(-short * 0.05f, y)
                        cubicTo(width * 0.24f, y - short * 0.11f, width * 0.42f, y + short * 0.10f, width * 0.63f, y)
                        cubicTo(width * 0.80f, y - short * 0.09f, width * 0.93f, y + short * 0.06f, width + short * 0.05f, y - short * 0.02f)
                    }
                    paint.color = withAlpha(preset.accentArgb, 0.42f - index * 0.10f)
                    canvas.drawPath(wave, paint)
                }
            }
            FixedCardPattern.CUT -> {
                paint.strokeWidth = max(2f, short * 0.007f)
                paint.color = withAlpha(preset.accentArgb, 0.36f)
                canvas.drawLine(width * 0.68f, 0f, width * 0.43f, height, paint)
                paint.color = withAlpha(preset.accentArgb, 0.18f)
                canvas.drawLine(width * 0.78f, 0f, width * 0.53f, height, paint)
                canvas.drawLine(width * 0.88f, 0f, width * 0.63f, height, paint)
            }
        }

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
        short: Float,
        fixedAlignment: FixedCardTextAlignment?
    ) {
        val resolvedAlignment = fixedAlignment ?: if (
            aspect == QrCardAspectPreset.SQUARE ||
            aspect == QrCardAspectPreset.PORTRAIT ||
            aspect == QrCardAspectPreset.STORY
        ) {
            FixedCardTextAlignment.CENTER
        } else {
            FixedCardTextAlignment.LEFT
        }

        val align = when (resolvedAlignment) {
            FixedCardTextAlignment.LEFT -> Paint.Align.LEFT
            FixedCardTextAlignment.CENTER -> Paint.Align.CENTER
            FixedCardTextAlignment.RIGHT -> Paint.Align.RIGHT
        }
        val centered = resolvedAlignment == FixedCardTextAlignment.CENTER
        val x = when (resolvedAlignment) {
            FixedCardTextAlignment.LEFT -> layout.brandRect.left
            FixedCardTextAlignment.CENTER -> (layout.brandRect.left + layout.brandRect.right) / 2f
            FixedCardTextAlignment.RIGHT -> layout.brandRect.right
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

        val titleX = when (resolvedAlignment) {
            FixedCardTextAlignment.LEFT -> layout.titleRect.left
            FixedCardTextAlignment.CENTER -> (layout.titleRect.left + layout.titleRect.right) / 2f
            FixedCardTextAlignment.RIGHT -> layout.titleRect.right
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
