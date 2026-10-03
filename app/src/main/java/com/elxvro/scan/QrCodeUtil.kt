package com.elxvro.scan

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object QrCodeUtil {
    fun create(
        text: String,
        size: Int = 900,
        margin: Int = 2,
        foreground: Int = Color.BLACK,
        background: Int = Color.WHITE,
        centerLabel: String? = "E"
    ): Bitmap {
        val safeSize = size.coerceIn(256, 2048)
        val hints = mapOf(
            EncodeHintType.MARGIN to margin.coerceIn(1, 8),
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H
        )
        val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, safeSize, safeSize, hints)
        val pixels = IntArray(safeSize * safeSize)
        for (y in 0 until safeSize) {
            val offset = y * safeSize
            for (x in 0 until safeSize) {
                pixels[offset + x] = if (matrix[x, y]) foreground else background
            }
        }
        val bitmap = Bitmap.createBitmap(safeSize, safeSize, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, safeSize, 0, 0, safeSize, safeSize)
        }
        if (!centerLabel.isNullOrBlank()) {
            val canvas = Canvas(bitmap)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val side = safeSize * 0.16f
            val center = safeSize / 2f
            paint.color = background
            canvas.drawRoundRect(
                RectF(center - side / 2, center - side / 2, center + side / 2, center + side / 2),
                side * 0.18f,
                side * 0.18f,
                paint
            )
            paint.color = foreground
            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = side * 0.68f
            canvas.drawText(centerLabel.take(2), center, center - (paint.ascent() + paint.descent()) / 2f, paint)
        }
        return bitmap
    }
}
