package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCardValidatorTest {
    @Test
    fun rejectsLogoLargerThanTwentyPercentOfQrWidth() {
        val model = QrCardModel(
            payload = "https://elxvro.com",
            logoMode = LogoMode.CUSTOM,
            logoScaleFraction = 0.25f
        )

        val result = QrCardValidator.validate(model)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).errors.contains(ValidationError.LOGO_TOO_LARGE))
    }

    @Test
    fun rejectsNonFiniteLogoScale() {
        val model = QrCardModel(
            payload = "https://elxvro.com",
            logoMode = LogoMode.CUSTOM,
            logoScaleFraction = Float.NaN
        )

        val result = QrCardValidator.validate(model)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).errors.contains(ValidationError.LOGO_SIZE_INVALID))
    }

    @Test
    fun requiresFourModuleQuietZone() {
        val model = QrCardModel(payload = "https://elxvro.com", quietZoneModules = 2)

        val result = QrCardValidator.validate(model)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).errors.contains(ValidationError.QUIET_ZONE_TOO_SMALL))
    }

    @Test
    fun rejectsLowContrastQrColors() {
        val model = QrCardModel(
            payload = "https://elxvro.com",
            qrForegroundArgb = 0xFF777777.toInt(),
            qrBackgroundArgb = 0xFF888888.toInt()
        )

        val result = QrCardValidator.validate(model)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).errors.contains(ValidationError.QR_CONTRAST_TOO_LOW))
    }

    @Test
    fun rejectsUnsafeCardAspectRatio() {
        val model = QrCardModel(
            payload = "https://elxvro.com",
            cardAspectRatio = 0.25f
        )

        val result = QrCardValidator.validate(model)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).errors.contains(ValidationError.CARD_ASPECT_RATIO_INVALID))
    }

    @Test
    fun rejectsNonFiniteCardAspectRatio() {
        val model = QrCardModel(
            payload = "https://elxvro.com",
            cardAspectRatio = Float.NaN
        )

        val result = QrCardValidator.validate(model)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).errors.contains(ValidationError.CARD_ASPECT_RATIO_INVALID))
    }

    @Test
    fun wifiTemplateRequiresSsid() {
        val model = QrCardModel(
            template = QrCardTemplate.WIFI,
            payload = "WIFI:T:WPA;S:test;P:secret;;",
            title = "Wi-Fi",
            wifiSsid = ""
        )

        val result = QrCardValidator.validate(model)

        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).errors.contains(ValidationError.WIFI_SSID_REQUIRED))
    }

    @Test
    fun validMinimalCardPassesWithoutChangingSafeValues() {
        val model = QrCardModel(
            template = QrCardTemplate.MINIMAL,
            payload = "https://elxvro.com",
            title = "ELXVRO",
            logoScaleFraction = 0.18f,
            quietZoneModules = 4
        )

        val result = QrCardValidator.validate(model)

        assertTrue(result is ValidationResult.Valid)
        assertEquals(model, (result as ValidationResult.Valid).model)
    }
}
