package com.elxvro.scan

import org.junit.Assert.assertEquals
import org.junit.Test

class QrPayloadBuilderV2Test {
    @Test
    fun sms_payload_contains_phone_and_message() {
        assertEquals("SMSTO:+905551234567:Merhaba", QrPayloadBuilder.build("SMS", "+905551234567", "Merhaba"))
    }

    @Test
    fun geo_payload_normalizes_coordinates() {
        assertEquals("geo:41.0082,28.9784", QrPayloadBuilder.build("Konum", "41.0082,28.9784"))
        assertEquals("geo:41.0,29.0", QrPayloadBuilder.build("Konum", "geo:41.0,29.0"))
    }

    @Test
    fun calendar_payload_builds_event() {
        assertEquals(
            "BEGIN:VEVENT\nSUMMARY:Toplantı\nDTSTART:20261002T120000Z\nEND:VEVENT",
            QrPayloadBuilder.build("Takvim", "Toplantı", "20261002T120000Z")
        )
    }

    @Test
    fun wifi_security_modes_are_encoded() {
        assertEquals("WIFI:T:WPA;S:ELXVRO;P:secret;;", QrPayloadBuilder.buildWifi("ELXVRO", "secret", WifiSecurity.WPA))
        assertEquals("WIFI:T:WEP;S:ELXVRO;P:abc123;;", QrPayloadBuilder.buildWifi("ELXVRO", "abc123", WifiSecurity.WEP))
        assertEquals("WIFI:T:nopass;S:ELXVRO;;", QrPayloadBuilder.buildWifi("ELXVRO", "", WifiSecurity.OPEN))
    }

    @Test
    fun wifi_reserved_characters_remain_escaped() {
        assertEquals("WIFI:T:WPA;S:ELX\\;VRO;P:p\\:ass;;", QrPayloadBuilder.buildWifi("ELX;VRO", "p:ass", WifiSecurity.WPA))
    }
}
