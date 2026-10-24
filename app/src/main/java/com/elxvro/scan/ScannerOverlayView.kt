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
    private val scanRect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = min(width * 0.72f, height * 0.42f)
        val left = (width - size) / 2f
        val top = (height - size) / 2f - height * 0.04f
        scanRect.set(left, top, left + size, top + size)
        val len = size * 0.18f

        canvas.drawLine(scanRect.left, scanRect.top + len, scanRect.left, scanRect.top, corner)
        canvas.drawLine(scanRect.left, scanRect.top, scanRect.left + len, scanRect.top, corner)
        canvas.drawLine(scanRect.right - len, scanRect.top, scanRect.right, scanRect.top, corner)
        canvas.drawLine(scanRect.right, scanRect.top, scanRect.right, scanRect.top + len, corner)
        canvas.drawLine(scanRect.left, scanRect.bottom - len, scanRect.left, scanRect.bottom, corner)
        canvas.drawLine(scanRect.left, scanRect.bottom, scanRect.left + len, scanRect.bottom, corner)
        canvas.drawLine(scanRect.right - len, scanRect.bottom, scanRect.right, scanRect.bottom, corner)
        canvas.drawLine(scanRect.right, scanRect.bottom - len, scanRect.right, scanRect.bottom, corner)

        val progress = (SystemClock.uptimeMillis() % 1700L) / 1700f
        val y = scanRect.top + progress * scanRect.height()
        canvas.drawLine(scanRect.left + 18f, y, scanRect.right - 18f, y, line)
        postInvalidateOnAnimation()
    }
}
