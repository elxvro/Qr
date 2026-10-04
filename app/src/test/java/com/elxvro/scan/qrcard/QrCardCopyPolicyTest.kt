package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCardCopyPolicyTest {
    @Test
    fun proCardCopyKeepsEveryVisibleTextIndependent() {
        val model = QrCardModel(
            payload = "https://example.com",
            brandText = "ACME",
            title = "Yeni Koleksiyon",
            descriptionText = "Bugünü keşfet",
            ctaText = "HEMEN TARA"
        )

        val copy = QrCardCopyPolicy.resolve(model)

        assertEquals("ACME", copy.brand)
        assertEquals("Yeni Koleksiyon", copy.title)
        assertEquals("Bugünü keşfet", copy.description)
        assertEquals("HEMEN TARA", copy.cta)
    }

    @Test
    fun brandMayBeBlankInsteadOfFallingBackToElxvro() {
        val model = QrCardModel(
            payload = "https://example.com",
            brandText = "",
            title = "Başlık"
        )

        val copy = QrCardCopyPolicy.resolve(model)

        assertTrue(copy.brand.isEmpty())
        assertEquals("Başlık", copy.title)
    }

    @Test
    fun visibleTextIsTrimmedButNotReplaced() {
        val model = QrCardModel(
            payload = "https://example.com",
            brandText = "  Marka  ",
            title = "  Başlık  ",
            descriptionText = "  Açıklama  ",
            ctaText = "  Tara  "
        )

        val copy = QrCardCopyPolicy.resolve(model)

        assertEquals("Marka", copy.brand)
        assertEquals("Başlık", copy.title)
        assertEquals("Açıklama", copy.description)
        assertEquals("Tara", copy.cta)
    }
}
