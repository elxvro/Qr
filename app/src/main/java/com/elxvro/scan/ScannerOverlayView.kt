package com.elxvro.scan

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.SystemClock
import android.view.View
import kotlin.math.min

class ScannerOverlayView(context: Context) : View(context) {
    private val corner = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(24, 140, 255)
        style = Paint.Style.STROKE
        strokeWidth = 7f
        strokeCap = Paint.Cap.ROUND
    }
    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(210, 32, 184, 255)
        strokeWidth = 4f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = min(width * 0.72f, height * 0.42f)
        val left = (width - size) / 2f
        val top = (height - size) / 2f - height * 0.04f
        val rect = RectF(left, top, left + size, top + size)
        val len = size * 0.18f

        canvas.drawLine(rect.left, rect.top + len, rect.left, rect.top, corner)
        canvas.drawLine(rect.left, rect.top, rect.left + len, rect.top, corner)
        canvas.drawLine(rect.right - len, rect.top, rect.right, rect.top, corner)
        canvas.drawLine(rect.right, rect.top, rect.right, rect.top + len, corner)
        canvas.drawLine(rect.left, rect.bottom - len, rect.left, rect.bottom, corner)
        canvas.drawLine(rect.left, rect.bottom, rect.left + len, rect.bottom, corner)
        canvas.drawLine(rect.right - len, rect.bottom, rect.right, rect.bottom, corner)
        canvas.drawLine(rect.right, rect.bottom - len, rect.right, rect.bottom, corner)

        val progress = (SystemClock.uptimeMillis() % 1700L) / 1700f
        val y = rect.top + progress * rect.height()
        canvas.drawLine(rect.left + 18f, y, rect.right - 18f, y, line)
        postInvalidateOnAnimation()
    }
}
