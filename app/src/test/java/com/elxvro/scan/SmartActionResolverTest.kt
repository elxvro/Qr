package com.elxvro.scan

import org.junit.Assert.assertEquals
import org.junit.Test

class SmartActionResolverTest {
    @Test fun url_opens_browser() {
        val action = SmartActionResolver.resolve("https://elxvro.com", "QR", "URL")
        assertEquals(SmartActionType.OPEN_URL, action.type)
        assertEquals("Siteyi Aç", action.label)
    }

    @Test fun phone_uses_dialer_even_without_tel_prefix() {
        val action = SmartActionResolver.resolve("+905551234567", "QR", "PHONE")
        assertEquals(SmartActionType.DIAL, action.type)
        assertEquals("tel:+905551234567", action.value)
    }

    @Test fun email_uses_mail_client() {
        val action = SmartActionResolver.resolve("info@elxvro.com", "QR", "EMAIL")
        assertEquals(SmartActionType.EMAIL, action.type)
        assertEquals("mailto:info@elxvro.com", action.value)
    }

    @Test fun sms_uses_sms_client() {
        val action = SmartActionResolver.resolve("+905551234567", "QR", "SMS")
        assertEquals(SmartActionType.SMS, action.type)
        assertEquals("smsto:+905551234567", action.value)
    }

    @Test fun geo_opens_map() {
        assertEquals(SmartActionType.MAP, SmartActionResolver.resolve("geo:41.0082,28.9784", "QR", "GEO").type)
    }

    @Test fun wifi_opens_wifi_settings() {
        assertEquals(SmartActionType.WIFI, SmartActionResolver.resolve("WIFI:T:WPA;S:ELXVRO;P:12345678;;", "QR", "WIFI").type)
    }

    @Test fun vcard_offers_contact_insert() {
        assertEquals(SmartActionType.CONTACT, SmartActionResolver.resolve("BEGIN:VCARD\nFN:Emre\nTEL:+905551234567\nEND:VCARD", "QR", "CONTACT").type)
    }

    @Test fun product_barcode_searches_web() {
        assertEquals(SmartActionType.SEARCH_PRODUCT, SmartActionResolver.resolve("8691234567890", "Barkod", "PRODUCT").type)
    }

    @Test fun plain_text_is_shared() {
        assertEquals(SmartActionType.SHARE_TEXT, SmartActionResolver.resolve("Merhaba", "QR", "TEXT").type)
    }
}
