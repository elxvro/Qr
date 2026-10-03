package com.elxvro.scan

import android.graphics.Bitmap
import android.graphics.Color
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
        highErrorCorrection: Boolean = false
    ): Bitmap {
        val safeSize = size.coerceIn(256, 2048)
        val hints = mutableMapOf<EncodeHintType, Any>(
            EncodeHintType.MARGIN to margin.coerceIn(0, 8)
        )
        if (highErrorCorrection) {
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        }
        val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, safeSize, safeSize, hints)
        val pixels = IntArray(safeSize * safeSize)
        for (y in 0 until safeSize) {
            val offset = y * safeSize
            for (x in 0 until safeSize) {
                pixels[offset + x] = if (matrix[x, y]) foreground else background
            }
        }
        return Bitmap.createBitmap(safeSize, safeSize, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, safeSize, 0, 0, safeSize, safeSize)
        }
    }
}
