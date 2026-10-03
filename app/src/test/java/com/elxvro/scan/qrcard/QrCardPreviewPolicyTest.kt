package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCardPreviewPolicyTest {
    @Test
    fun sanitizesUnsafePreviewWithoutChangingTemplate() {
        val unsafe = QrCardModel(
            template = QrCardTemplate.EVENT,
            payload = "",
            title = "",
            eventDate = "",
            eventLocation = "",
            qrForegroundArgb = 0xFF777777.toInt(),
            qrBackgroundArgb = 0xFF888888.toInt(),
            logoScaleFraction = Float.NaN,
            cardAspectRatio = Float.NaN
        )

        val safe = QrCardPreviewPolicy.sanitize(unsafe)

        assertEquals(QrCardTemplate.EVENT, safe.template)
        assertTrue(QrCardValidator.validate(safe) is ValidationResult.Valid)
        assertEquals(QrCardTemplate.EVENT.defaults().cardAspectRatio, safe.cardAspectRatio)
    }

    @Test
    fun preservesAlreadyValidUserColors() {
        val valid = QrCardModel(
            template = QrCardTemplate.MINIMAL,
            payload = "https://elxvro.com",
            title = "ELXVRO",
            qrForegroundArgb = 0xFF000000.toInt(),
            qrBackgroundArgb = 0xFFFFFFFF.toInt()
        )

        val safe = QrCardPreviewPolicy.sanitize(valid)

        assertEquals(valid.qrForegroundArgb, safe.qrForegroundArgb)
        assertEquals(valid.qrBackgroundArgb, safe.qrBackgroundArgb)
    }
}
