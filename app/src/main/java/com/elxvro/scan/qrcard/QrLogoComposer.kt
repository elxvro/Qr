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
        val safeScale = QrLogoPresentationPolicy.safeScale(logoScaleFraction)
        require(logoScaleFraction > 0f && logoScaleFraction <= QrCardValidator.MAX_LOGO_SCALE) {
            "Logo size exceeds safe QR coverage"
        }

        val output = qrBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val side = output.width * safeScale
        val backing = side * QrLogoPresentationPolicy.BACKING_FACTOR
        val cx = output.width / 2f
        val cy = output.height / 2f
        val backingRect = RectF(
            cx - backing / 2f,
            cy - backing / 2f,
            cx + backing / 2f,
            cy + backing / 2f
        )

        paint.style = Paint.Style.FILL
        paint.color = 0xFFFFFFFF.toInt()
        canvas.drawRoundRect(
            backingRect,
            backing * QrLogoPresentationPolicy.CORNER_FACTOR,
            backing * QrLogoPresentationPolicy.CORNER_FACTOR,
            paint
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = (backing * QrLogoPresentationPolicy.BORDER_FACTOR).coerceAtLeast(2f)
        paint.color = 0x24000000
        canvas.drawRoundRect(
            backingRect,
            backing * QrLogoPresentationPolicy.CORNER_FACTOR,
            backing * QrLogoPresentationPolicy.CORNER_FACTOR,
            paint
        )

        paint.style = Paint.Style.FILL
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
