package com.elxvro.scan.barcode

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BarcodeCreatePolicyTest {
    @Test
    fun code128AcceptsTrimmedText() {
        val result = BarcodeCreatePolicy.validate(BarcodeCreateType.CODE_128, "  ELXVRO-123  ")

        assertEquals(BarcodeCreateResult.Valid("ELXVRO-123"), result)
    }

    @Test
    fun code128RejectsBlankValue() {
        val result = BarcodeCreatePolicy.validate(BarcodeCreateType.CODE_128, "   ")

        assertTrue(result is BarcodeCreateResult.Invalid)
    }

    @Test
    fun ean13AddsCheckDigitToTwelveDigits() {
        val result = BarcodeCreatePolicy.validate(BarcodeCreateType.EAN_13, "590123412345")

        assertEquals(BarcodeCreateResult.Valid("5901234123457"), result)
    }

    @Test
    fun ean13RejectsInvalidExistingCheckDigit() {
        val result = BarcodeCreatePolicy.validate(BarcodeCreateType.EAN_13, "5901234123458")

        assertTrue(result is BarcodeCreateResult.Invalid)
    }

    @Test
    fun upcAAddsCheckDigitToElevenDigits() {
        val result = BarcodeCreatePolicy.validate(BarcodeCreateType.UPC_A, "03600029145")

        assertEquals(BarcodeCreateResult.Valid("036000291452"), result)
    }

    @Test
    fun retailFormatsRejectNonDigits() {
        val ean = BarcodeCreatePolicy.validate(BarcodeCreateType.EAN_13, "59012341A345")
        val upc = BarcodeCreatePolicy.validate(BarcodeCreateType.UPC_A, "03600029A45")

        assertTrue(ean is BarcodeCreateResult.Invalid)
        assertTrue(upc is BarcodeCreateResult.Invalid)
    }

    @Test
    fun typeLabelsAndHistoryFormatsStayStable() {
        assertEquals("CODE 128", BarcodeCreateType.CODE_128.label)
        assertEquals("CODE_128", BarcodeCreateType.CODE_128.historyFormat)
        assertEquals("EAN-13", BarcodeCreateType.EAN_13.label)
        assertEquals("EAN_13", BarcodeCreateType.EAN_13.historyFormat)
        assertEquals("UPC-A", BarcodeCreateType.UPC_A.label)
        assertEquals("UPC_A", BarcodeCreateType.UPC_A.historyFormat)
    }
}
