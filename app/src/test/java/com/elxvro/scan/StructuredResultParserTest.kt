package com.elxvro.scan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StructuredResultParserTest {
    @Test
    fun parsesWifiAndMarksPasswordSensitive() {
        val card = StructuredResultParser.parse(
            raw = "WIFI:T:WPA;S:ELX\\;VRO;P:p\\:ass;H:true;;",
            semanticType = "WIFI",
            kind = "QR",
            format = "QR Code"
        )!!

        assertEquals(StructuredResultType.WIFI, card.type)
        assertEquals("ELX;VRO", card.field("Ağ adı")?.value)
        assertEquals("WPA", card.field("Güvenlik")?.value)
        assertEquals("Evet", card.field("Gizli ağ")?.value)
        assertEquals("p:ass", card.field("Şifre")?.value)
        assertTrue(card.field("Şifre")?.sensitive == true)
    }

    @Test
    fun parsesVcardNamePhoneAndEmail() {
        val raw = """
            BEGIN:VCARD
            VERSION:3.0
            FN:Emre Karip
            TEL:+905551234567
            EMAIL:emre@example.com
            END:VCARD
        """.trimIndent()

        val card = StructuredResultParser.parse(raw, "CONTACT", "QR", "QR Code")!!

        assertEquals(StructuredResultType.CONTACT, card.type)
        assertEquals("Emre Karip", card.field("Ad")?.value)
        assertEquals("+905551234567", card.field("Telefon")?.value)
        assertEquals("emre@example.com", card.field("E-posta")?.value)
    }

    @Test
    fun parsesMecardContact() {
        val card = StructuredResultParser.parse(
            "MECARD:N:Emre Karip;TEL:+905551234567;EMAIL:emre@example.com;;",
            "CONTACT",
            "QR",
            "QR Code"
        )!!

        assertEquals("Emre Karip", card.field("Ad")?.value)
        assertEquals("+905551234567", card.field("Telefon")?.value)
    }

    @Test
    fun parsesCalendarEvent() {
        val raw = """
            BEGIN:VEVENT
            SUMMARY:Toplantı
            DTSTART:20261004T120000Z
            DTEND:20261004T130000Z
            LOCATION:İstanbul
            END:VEVENT
        """.trimIndent()

        val card = StructuredResultParser.parse(raw, "CALENDAR", "QR", "QR Code")!!

        assertEquals(StructuredResultType.CALENDAR, card.type)
        assertEquals("Toplantı", card.field("Etkinlik")?.value)
        assertEquals("20261004T120000Z", card.field("Başlangıç")?.value)
        assertEquals("20261004T130000Z", card.field("Bitiş")?.value)
        assertEquals("İstanbul", card.field("Konum")?.value)
    }

    @Test
    fun parsesMailtoRecipientSubjectAndBody() {
        val card = StructuredResultParser.parse(
            "mailto:info@elxvro.com?subject=Merhaba%20D%C3%BCnya&body=Test%20mesaj%C4%B1",
            "EMAIL",
            "QR",
            "QR Code"
        )!!

        assertEquals("info@elxvro.com", card.field("Alıcı")?.value)
        assertEquals("Merhaba Dünya", card.field("Konu")?.value)
        assertEquals("Test mesajı", card.field("Mesaj")?.value)
    }

    @Test
    fun parsesSmsNumberAndMessage() {
        val card = StructuredResultParser.parse(
            "SMSTO:+905551234567:Merhaba dünya",
            "SMS",
            "QR",
            "QR Code"
        )!!

        assertEquals("+905551234567", card.field("Numara")?.value)
        assertEquals("Merhaba dünya", card.field("Mesaj")?.value)
    }

    @Test
    fun parsesGeoCoordinates() {
        val card = StructuredResultParser.parse(
            "geo:41.0082,28.9784?q=Galata",
            "GEO",
            "QR",
            "QR Code"
        )!!

        assertEquals("41.0082", card.field("Enlem")?.value)
        assertEquals("28.9784", card.field("Boylam")?.value)
    }

    @Test
    fun productBarcodeUsesCodeAndFormat() {
        val card = StructuredResultParser.parse(
            "5901234123457",
            "PRODUCT",
            "Barkod",
            "EAN-13"
        )!!

        assertEquals(StructuredResultType.PRODUCT, card.type)
        assertEquals("5901234123457", card.field("Ürün kodu")?.value)
        assertEquals("EAN-13", card.field("Format")?.value)
    }

    @Test
    fun malformedStructuredDataFallsBackToRawResult() {
        assertNull(StructuredResultParser.parse("WIFI:;;;;", "WIFI", "QR", "QR Code"))
        assertNull(StructuredResultParser.parse("BEGIN:VCARD\nEND:VCARD", "CONTACT", "QR", "QR Code"))
        assertNull(StructuredResultParser.parse("geo:not-a-coordinate", "GEO", "QR", "QR Code"))
    }

    @Test
    fun ordinaryTextDoesNotCreateStructuredCard() {
        val card = StructuredResultParser.parse("hello", "TEXT", "QR", "QR Code")
        assertNull(card)
    }

    private fun StructuredResultCard.field(label: String): StructuredResultField? =
        fields.firstOrNull { it.label == label }
}
