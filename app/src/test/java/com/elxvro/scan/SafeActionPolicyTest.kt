package com.elxvro.scan

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SafeActionPolicyTest {
    @Test
    fun onlyLowRiskHttpsUrlsMayAutoOpen() {
        assertTrue(
            SafeActionPolicy.mayAutoOpen(
                SmartAction(SmartActionType.OPEN_URL, "Siteyi Aç", "https://elxvro.com")
            )
        )
        assertFalse(
            SafeActionPolicy.mayAutoOpen(
                SmartAction(SmartActionType.OPEN_URL, "Siteyi Aç", "http://example.com")
            )
        )
    }

    @Test
    fun mediumAndHighRiskHttpsUrlsDoNotAutoOpen() {
        assertFalse(
            SafeActionPolicy.mayAutoOpen(
                SmartAction(SmartActionType.OPEN_URL, "Siteyi Aç", "https://bit.ly/example")
            )
        )
        assertFalse(
            SafeActionPolicy.mayAutoOpen(
                SmartAction(SmartActionType.OPEN_URL, "Siteyi Aç", "https://xn--pple-43d.com")
            )
        )
        assertFalse(
            SafeActionPolicy.mayAutoOpen(
                SmartAction(
                    SmartActionType.OPEN_URL,
                    "Siteyi Aç",
                    "https://trusted.example@evil.example/path"
                )
            )
        )
    }

    @Test
    fun nonWebActionsNeverAutoOpen() {
        assertFalse(SafeActionPolicy.mayAutoOpen(SmartAction(SmartActionType.DIAL, "Ara", "tel:+905551234567")))
        assertFalse(SafeActionPolicy.mayAutoOpen(SmartAction(SmartActionType.SMS, "SMS Gönder", "smsto:+905551234567")))
        assertFalse(SafeActionPolicy.mayAutoOpen(SmartAction(SmartActionType.WIFI, "Wi-Fi", "WIFI:S:test;;")))
        assertFalse(SafeActionPolicy.mayAutoOpen(SmartAction(SmartActionType.SHARE_TEXT, "Paylaş", "hello")))
    }

    @Test
    fun customSchemesDoNotPassEvenWhenMarkedOpenUrl() {
        assertFalse(SafeActionPolicy.mayAutoOpen(SmartAction(SmartActionType.OPEN_URL, "Aç", "intent://example")))
        assertFalse(SafeActionPolicy.mayAutoOpen(SmartAction(SmartActionType.OPEN_URL, "Aç", "javascript:alert(1)")))
    }
}
