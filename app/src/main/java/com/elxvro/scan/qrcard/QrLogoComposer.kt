package com.elxvro.scan.qrcard

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

object QrLogoComposer {
    fun compose(
        qrBitmap: Bitmap,
        logoBitmap: Bitmap,
        logoScaleFraction: Float = 0.18f
    ): Bitmap {
        require(logoScaleFraction > 0f && logoScaleFraction <= QrCardValidator.MAX_LOGO_SCALE) {
            "Logo size exceeds safe QR coverage"
        }

        val output = qrBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val side = output.width * logoScaleFraction
        val backing = side * 1.26f
        val cx = output.width / 2f
        val cy = output.height / 2f

        paint.color = 0xFFFFFFFF.toInt()
        val backingRect = RectF(
            cx - backing / 2f,
            cy - backing / 2f,
            cx + backing / 2f,
            cy + backing / 2f
        )
        canvas.drawRoundRect(backingRect, backing * 0.18f, backing * 0.18f, paint)

        val logoRect = RectF(
            cx - side / 2f,
            cy - side / 2f,
            cx + side / 2f,
            cy + side / 2f
        )
        canvas.drawBitmap(logoBitmap, null, logoRect, paint)
        return output
    }
}
