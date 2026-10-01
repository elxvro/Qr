package com.elxvro.scan

import org.junit.Assert.assertEquals
import org.junit.Test

class QrPayloadBuilderTest {
    @Test
    fun url_is_kept_verbatim() {
        assertEquals("https://elxvro.com", QrPayloadBuilder.build("URL", "https://elxvro.com"))
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
}
