package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCardV27TemplateTest {
    @Test
    fun proTemplateCatalogIncludesBusinessPromoAndTicket() {
        assertTrue(QrCardTemplate.entries.contains(QrCardTemplate.BUSINESS))
        assertTrue(QrCardTemplate.entries.contains(QrCardTemplate.PROMO))
        assertTrue(QrCardTemplate.entries.contains(QrCardTemplate.TICKET))
        assertEquals(8, QrCardTemplate.entries.size)
    }

    @Test
    fun newTemplatesHaveDistinctAccentColors() {
        val accents = listOf(
            QrCardTemplate.BUSINESS,
            QrCardTemplate.PROMO,
            QrCardTemplate.TICKET
        ).map { it.defaults().accentArgb }

        assertEquals(3, accents.toSet().size)
    }
}
