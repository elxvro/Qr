package com.elxvro.scan.barcode

import com.google.zxing.BarcodeFormat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BarcodeEncodingPolicyTest {
    @Test
    fun mapsCreationTypesToZxingFormats() {
        assertEquals(BarcodeFormat.CODE_128, BarcodeEncodingPolicy.format(BarcodeCreateType.CODE_128))
        assertEquals(BarcodeFormat.EAN_13, BarcodeEncodingPolicy.format(BarcodeCreateType.EAN_13))
        assertEquals(BarcodeFormat.UPC_A, BarcodeEncodingPolicy.format(BarcodeCreateType.UPC_A))
    }

    @Test
    fun retailBarcodesUseReadableWideCanvas() {
        val ean = BarcodeEncodingPolicy.canvas(BarcodeCreateType.EAN_13)
        val upc = BarcodeEncodingPolicy.canvas(BarcodeCreateType.UPC_A)

        assertTrue(ean.width > ean.height)
        assertTrue(upc.width > upc.height)
        assertTrue(ean.height >= 420)
        assertTrue(upc.height >= 420)
    }

    @Test
    fun code128AlsoUsesWideCanvas() {
        val canvas = BarcodeEncodingPolicy.canvas(BarcodeCreateType.CODE_128)

        assertTrue(canvas.width >= 1200)
        assertTrue(canvas.width > canvas.height)
    }
}
