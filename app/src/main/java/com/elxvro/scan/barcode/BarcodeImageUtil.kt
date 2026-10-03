package com.elxvro.scan.barcode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter

object BarcodeImageUtil {
    fun create(
        type: BarcodeCreateType,
        value: String,
        foreground: Int = Color.BLACK,
        background: Int = Color.WHITE
    ): Bitmap {
        val validated = BarcodeCreatePolicy.validate(type, value)
        require(validated is BarcodeCreateResult.Valid) { "Invalid barcode value" }

        val normalized = validated.normalizedValue
        val spec = BarcodeEncodingPolicy.canvas(type)
        val matrix = MultiFormatWriter().encode(
            normalized,
            BarcodeEncodingPolicy.format(type),
            spec.width,
            spec.barsHeight,
            mapOf(EncodeHintType.MARGIN to 8)
        )

        val bars = Bitmap.createBitmap(spec.width, spec.barsHeight, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(spec.width * spec.barsHeight)
        for (y in 0 until spec.barsHeight) {
            val offset = y * spec.width
            for (x in 0 until spec.width) {
                pixels[offset + x] = if (matrix[x, y]) foreground else background
            }
        }
        bars.setPixels(pixels, 0, spec.width, 0, 0, spec.width, spec.barsHeight)

        val output = Bitmap.createBitmap(spec.width, spec.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawColor(background)
        canvas.drawBitmap(bars, 0f, 0f, Paint(Paint.FILTER_BITMAP_FLAG))

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = foreground
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            textSize = if (type == BarcodeCreateType.CODE_128) 46f else 58f
        }
        val baseline = spec.barsHeight + (spec.height - spec.barsHeight) / 2f -
            (paint.ascent() + paint.descent()) / 2f
        val visible = fitLabel(normalized, paint, spec.width * 0.88f)
        canvas.drawText(visible, spec.width / 2f, baseline, paint)
        bars.recycle()
        return output
    }

    private fun fitLabel(value: String, paint: Paint, maxWidth: Float): String {
        if (paint.measureText(value) <= maxWidth) return value
        var fitted = value
        while (fitted.length > 1 && paint.measureText(fitted + "…") > maxWidth) {
            fitted = fitted.dropLast(1)
        }
        return fitted + "…"
    }
}
