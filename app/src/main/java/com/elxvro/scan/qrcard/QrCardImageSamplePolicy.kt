package com.elxvro.scan.qrcard

object QrCardImageSamplePolicy {
    private const val TARGET_MAX_DIMENSION = 2048

    fun inSampleSize(width: Int, height: Int): Int {
        if (width <= 0 || height <= 0) return 1
        val maxDimension = maxOf(width, height)
        var sample = 1
        while (maxDimension / sample > TARGET_MAX_DIMENSION) {
            sample *= 2
        }
        return sample.coerceAtLeast(1)
    }
}
