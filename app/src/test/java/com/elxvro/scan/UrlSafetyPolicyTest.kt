package com.elxvro.scan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UrlSafetyPolicyTest {
    @Test
    fun normalHttpsUrlIsLowRisk() {
        val report = UrlSafetyPolicy.analyze("https://www.elxvro.com/scan")

        assertEquals(UrlRiskLevel.LOW, report.level)
        assertTrue(report.reasons.isEmpty())
        assertEquals("www.elxvro.com", report.host)
    }

    @Test
    fun plainHttpIsMediumRisk() {
        val report = UrlSafetyPolicy.analyze("http://example.com/login")

        assertEquals(UrlRiskLevel.MEDIUM, report.level)
        assertTrue(UrlRiskReason.INSECURE_HTTP in report.reasons)
    }

    @Test
    fun ipAddressHostIsFlagged() {
        val report = UrlSafetyPolicy.analyze("https://192.168.1.50/login")

        assertEquals(UrlRiskLevel.MEDIUM, report.level)
        assertTrue(UrlRiskReason.IP_ADDRESS_HOST in report.reasons)
    }

    @Test
    fun userInfoStyleUrlIsHighRisk() {
        val report = UrlSafetyPolicy.analyze("https://trusted.example@evil.example/path")

        assertEquals(UrlRiskLevel.HIGH, report.level)
        assertTrue(UrlRiskReason.USER_INFO_PRESENT in report.reasons)
        assertEquals("evil.example", report.host)
    }

    @Test
    fun punycodeDomainIsFlagged() {
        val report = UrlSafetyPolicy.analyze("https://xn--pple-43d.com")

        assertEquals(UrlRiskLevel.MEDIUM, report.level)
        assertTrue(UrlRiskReason.IDN_OR_PUNYCODE_HOST in report.reasons)
    }

    @Test
    fun unicodeIdnDomainIsFlaggedAndNormalized() {
        val report = UrlSafetyPolicy.analyze("https://bücher.example")

        assertEquals(UrlRiskLevel.MEDIUM, report.level)
        assertTrue(UrlRiskReason.IDN_OR_PUNYCODE_HOST in report.reasons)
        assertEquals("xn--bcher-kva.example", report.host)
    }

    @Test
    fun knownShortenerIsFlagged() {
        val report = UrlSafetyPolicy.analyze("https://bit.ly/3Example")

        assertEquals(UrlRiskLevel.MEDIUM, report.level)
        assertTrue(UrlRiskReason.URL_SHORTENER in report.reasons)
    }

    @Test
    fun malformedOrHostlessWebUrlIsHighRisk() {
        val report = UrlSafetyPolicy.analyze("https:///missing-host")

        assertEquals(UrlRiskLevel.HIGH, report.level)
        assertTrue(UrlRiskReason.INVALID_OR_MISSING_HOST in report.reasons)
    }
}
