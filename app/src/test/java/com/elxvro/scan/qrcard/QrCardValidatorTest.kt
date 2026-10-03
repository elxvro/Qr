package com.elxvro.scan.qrcard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCardValidatorTest {
    @Test
    fun validCardPassesValidation() {
        val model = QrCardModel(payload = "https://elxvro.com", title = "ELXVRO")
        assertTrue(QrCardValidator.validate(model).isValid)
    }

    @Test
    fun blankPayloadIsRejected() {
        val model = QrCardModel(payload = "   ", title = "ELXVRO")
        assertFalse(QrCardValidator.validate(model).isValid)
    }

    @Test
    fun customLogoCannotExceedTwentyTwoPercentOfQrWidth() {
        val model = QrCardModel(payload = "https://elxvro.com", logoScale = 0.23f)
        assertFalse(QrCardValidator.validate(model).isValid)
        assertTrue(QrCardValidator.validate(model.copy(logoScale = 0.22f)).isValid)
    }

    @Test
    fun templatesHaveStableDefaults() {
        assertTrue(QrCardTemplate.entries.containsAll(listOf(
            QrCardTemplate.MINIMAL,
            QrCardTemplate.CORPORATE,
            QrCardTemplate.WIFI,
            QrCardTemplate.SOCIAL,
            QrCardTemplate.EVENT
        )))
    }
}
