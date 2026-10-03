package com.elxvro.scan.qrcard

import org.junit.Assert.assertTrue
import org.junit.Test

class QrNoLogoValidationTest {
    @Test
    fun logosuzCardDoesNotRequireLogoScale() {
        val model = QrCardModel(
            payload = "https://elxvro.com",
            logoMode = LogoMode.NONE,
            logoScaleFraction = 0f
        )

        val result = QrCardValidator.validate(model)

        assertTrue(result is ValidationResult.Valid)
    }
}
