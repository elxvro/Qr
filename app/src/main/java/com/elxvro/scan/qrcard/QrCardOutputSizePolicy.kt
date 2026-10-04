package com.elxvro.scan.qrcard

import kotlin.math.roundToInt

data class QrCardOutputSize(
    val width: Int,
    val height: Int
)

object QrCardOutputSizePolicy {
    fun resolve(requestedLongEdge: Int, aspectRatio: Float): QrCardOutputSize {
        require(requestedLongEdge in 512..4096)
        require(aspectRatio.isFinite() && aspectRatio > 0f)

        return if (aspectRatio >= 1f) {
            val width = requestedLongEdge
            val height = (requestedLongEdge / aspectRatio).roundToInt().coerceAtLeast(1)
            QrCardOutputSize(width, height)
        } else {
            val height = requestedLongEdge
            val width = (requestedLongEdge * aspectRatio).roundToInt().coerceAtLeast(1)
            QrCardOutputSize(width, height)
        }
    }
}
