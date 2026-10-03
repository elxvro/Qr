package com.elxvro.scan.barcode

import com.google.zxing.BarcodeFormat

data class BarcodeCanvas(
    val width: Int,
    val height: Int,
    val barsHeight: Int
)

object BarcodeEncodingPolicy {
    fun format(type: BarcodeCreateType): BarcodeFormat = when (type) {
        BarcodeCreateType.CODE_128 -> BarcodeFormat.CODE_128
        BarcodeCreateType.EAN_13 -> BarcodeFormat.EAN_13
        BarcodeCreateType.UPC_A -> BarcodeFormat.UPC_A
    }

    fun canvas(type: BarcodeCreateType): BarcodeCanvas = when (type) {
        BarcodeCreateType.CODE_128 -> BarcodeCanvas(width = 1600, height = 560, barsHeight = 420)
        BarcodeCreateType.EAN_13 -> BarcodeCanvas(width = 1600, height = 600, barsHeight = 450)
        BarcodeCreateType.UPC_A -> BarcodeCanvas(width = 1600, height = 600, barsHeight = 450)
    }
}
