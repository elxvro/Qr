package com.elxvro.scan

import org.junit.Assert.assertEquals
import org.junit.Test

class QrPayloadBuilderTest {
    @Test
    fun url_is_kept_verbatim_when_scheme_exists() {
        assertEquals("https://elxvro.com", QrPayloadBuilder.build("URL", "https://elxvro.com"))
    }

    @Test
    fun url_without_scheme_gets_https() {
        assertEquals("https://elxvro.com", QrPayloadBuilder.build("URL", "elxvro.com"))
    }

    @Test
    fun text_is_kept_as_text() {
        assertEquals("ELXVRO Scan", QrPayloadBuilder.build("Metin", "ELXVRO Scan"))
    }

    @Test
    fun phone_gets_tel_scheme() {
        assertEquals("tel:+905551234567", QrPayloadBuilder.build("Telefon", "+905551234567"))
    }

    @Test
    fun email_gets_mailto_scheme() {
        assertEquals("mailto:info@elxvro.com", QrPayloadBuilder.build("E-posta", "info@elxvro.com"))
    }

    @Test
    fun wifi_escapes_reserved_characters() {
        assertEquals(
            "WIFI:T:WPA;S:ELX\\;VRO;P:p\\:ass;;",
            QrPayloadBuilder.build("Wi-Fi", "ELX;VRO", "p:ass")
        )
    }

    @Test
    fun contact_creates_vcard() {
        assertEquals(
            "BEGIN:VCARD\nVERSION:3.0\nFN:Emre\nTEL:+905551234567\nEND:VCARD",
            QrPayloadBuilder.build("Kişi", "Emre", "+905551234567")
        )
    }
}
