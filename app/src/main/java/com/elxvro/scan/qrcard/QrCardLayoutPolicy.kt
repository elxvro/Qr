package com.elxvro.scan.qrcard

import kotlin.math.min

data class LayoutRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top

    fun overlaps(other: LayoutRect): Boolean {
        return left < other.right && right > other.left && top < other.bottom && bottom > other.top
    }
}

data class QrCardLayout(
    val qrRect: LayoutRect,
    val textRect: LayoutRect
)

object QrCardLayoutPolicy {
    fun resolve(width: Int, height: Int, qrPosition: QrPosition): QrCardLayout {
        require(width > 0 && height > 0)

        val w = width.toFloat()
        val h = height.toFloat()
        val aspect = w / h
        val outer = w * 0.055f
        val accentWidth = maxOf(8f, w * 0.008f)
        val contentLeft = outer + accentWidth + w * 0.035f
        val gap = w * 0.04f

        return if (aspect > 1.15f) {
            landscapeLayout(w, h, outer, contentLeft, gap, qrPosition)
        } else {
            stackedLayout(w, h, outer, contentLeft, gap, qrPosition)
        }
    }

    private fun landscapeLayout(
        width: Float,
        height: Float,
        outer: Float,
        contentLeft: Float,
        gap: Float,
        qrPosition: QrPosition
    ): QrCardLayout {
        val qrSize = min(width * 0.36f, height * 0.72f).coerceAtLeast(256f)
        val qrLeft = width - outer - qrSize
        val maxTop = height - outer - qrSize
        val qrTop = when (qrPosition) {
            QrPosition.TOP -> outer
            QrPosition.CENTER -> (height - qrSize) / 2f
            QrPosition.BOTTOM -> maxTop
        }.coerceIn(outer, maxTop)

        return QrCardLayout(
            qrRect = LayoutRect(qrLeft, qrTop, qrLeft + qrSize, qrTop + qrSize),
            textRect = LayoutRect(contentLeft, outer, qrLeft - gap, height - outer)
        )
    }

    private fun stackedLayout(
        width: Float,
        height: Float,
        outer: Float,
        contentLeft: Float,
        gap: Float,
        qrPosition: QrPosition
    ): QrCardLayout {
        val aspect = width / height
        val qrSize = if (aspect < 0.9f) {
            min(width * 0.64f, height * 0.34f)
        } else {
            min(width * 0.52f, height * 0.52f)
        }.coerceAtLeast(256f)

        val qrLeft = (width - qrSize) / 2f
        val maxTop = height - outer - qrSize
        val qrTop = when (qrPosition) {
            QrPosition.TOP -> outer
            QrPosition.CENTER -> ((height - qrSize) * 0.62f).coerceIn(outer, maxTop)
            QrPosition.BOTTOM -> maxTop
        }

        val qrRect = LayoutRect(qrLeft, qrTop, qrLeft + qrSize, qrTop + qrSize)
        val textRight = width - outer
        val textRect = if (qrPosition == QrPosition.TOP) {
            LayoutRect(contentLeft, qrRect.bottom + gap, textRight, height - outer)
        } else {
            LayoutRect(contentLeft, outer, textRight, qrRect.top - gap)
        }

        return QrCardLayout(qrRect = qrRect, textRect = textRect)
    }
}
