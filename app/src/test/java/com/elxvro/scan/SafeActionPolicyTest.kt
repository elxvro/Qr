package com.elxvro.scan

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SafeActionPolicyTest {
    @Test
    fun http_and_https_urls_may_auto_open() {
        assertTrue(SafeActionPolicy.mayAutoOpen(SmartAction(SmartActionType.OPEN_URL, "Siteyi Aç", "https://elxvro.com")))
        assertTrue(SafeActionPolicy.mayAutoOpen(SmartAction(SmartActionType.OPEN_URL, "Siteyi Aç", "http://example.com")))
    }

    @Test
    fun non_web_actions_never_auto_open() {
        assertFalse(SafeActionPolicy.mayAutoOpen(SmartAction(SmartActionType.DIAL, "Ara", "tel:+905551234567")))
        assertFalse(SafeActionPolicy.mayAutoOpen(SmartAction(SmartActionType.SMS, "SMS Gönder", "smsto:+905551234567")))
        assertFalse(SafeActionPolicy.mayAutoOpen(SmartAction(SmartActionType.WIFI, "Wi-Fi", "WIFI:S:test;;")))
        assertFalse(SafeActionPolicy.mayAutoOpen(SmartAction(SmartActionType.SHARE_TEXT, "Paylaş", "hello")))
    }

    @Test
    fun custom_schemes_do_not_pass_even_when_marked_open_url() {
        assertFalse(SafeActionPolicy.mayAutoOpen(SmartAction(SmartActionType.OPEN_URL, "Aç", "intent://example")))
        assertFalse(SafeActionPolicy.mayAutoOpen(SmartAction(SmartActionType.OPEN_URL, "Aç", "javascript:alert(1)")))
    }
}
