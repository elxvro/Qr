package com.elxvro.scan.qrcard

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

enum class ValidationError {
    PAYLOAD_REQUIRED,
    LOGO_TOO_LARGE,
    LOGO_SIZE_INVALID,
    QUIET_ZONE_TOO_SMALL,
    QR_CONTRAST_TOO_LOW,
    TITLE_REQUIRED,
    WIFI_SSID_REQUIRED,
    SOCIAL_HANDLE_REQUIRED,
    EVENT_DATE_REQUIRED,
    EVENT_LOCATION_REQUIRED
}

sealed interface ValidationResult {
    data class Valid(val model: QrCardModel) : ValidationResult
    data class Invalid(val errors: Set<ValidationError>) : ValidationResult
}

object QrCardValidator {
    const val MAX_LOGO_SCALE = 0.20f
    const val MIN_QUIET_ZONE_MODULES = 4
    const val MIN_QR_CONTRAST_RATIO = 4.5

    fun validate(model: QrCardModel): ValidationResult {
        val errors = linkedSetOf<ValidationError>()

        if (model.payload.isBlank()) errors += ValidationError.PAYLOAD_REQUIRED
        if (model.logoScaleFraction <= 0f) errors += ValidationError.LOGO_SIZE_INVALID
        if (model.logoScaleFraction > MAX_LOGO_SCALE) errors += ValidationError.LOGO_TOO_LARGE
        if (model.quietZoneModules < MIN_QUIET_ZONE_MODULES) errors += ValidationError.QUIET_ZONE_TOO_SMALL
        if (contrastRatio(model.qrForegroundArgb, model.qrBackgroundArgb) < MIN_QR_CONTRAST_RATIO) {
            errors += ValidationError.QR_CONTRAST_TOO_LOW
        }

        when (model.template) {
            QrCardTemplate.MINIMAL -> Unit
            QrCardTemplate.CORPORATE -> if (model.title.isBlank()) errors += ValidationError.TITLE_REQUIRED
            QrCardTemplate.WIFI -> if (model.wifiSsid.isBlank()) errors += ValidationError.WIFI_SSID_REQUIRED
            QrCardTemplate.SOCIAL -> {
                if (model.title.isBlank()) errors += ValidationError.TITLE_REQUIRED
                if (model.socialHandle.isBlank()) errors += ValidationError.SOCIAL_HANDLE_REQUIRED
            }
            QrCardTemplate.EVENT -> {
                if (model.title.isBlank()) errors += ValidationError.TITLE_REQUIRED
                if (model.eventDate.isBlank()) errors += ValidationError.EVENT_DATE_REQUIRED
                if (model.eventLocation.isBlank()) errors += ValidationError.EVENT_LOCATION_REQUIRED
            }
        }

        return if (errors.isEmpty()) ValidationResult.Valid(model) else ValidationResult.Invalid(errors)
    }

    fun contrastRatio(foregroundArgb: Int, backgroundArgb: Int): Double {
        val foreground = relativeLuminance(foregroundArgb)
        val background = relativeLuminance(backgroundArgb)
        val lighter = max(foreground, background)
        val darker = min(foreground, background)
        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun relativeLuminance(argb: Int): Double {
        fun linear(channel: Int): Double {
            val normalized = channel / 255.0
            return if (normalized <= 0.03928) normalized / 12.92
            else ((normalized + 0.055) / 1.055).pow(2.4)
        }

        val red = (argb shr 16) and 0xFF
        val green = (argb shr 8) and 0xFF
        val blue = argb and 0xFF
        return 0.2126 * linear(red) + 0.7152 * linear(green) + 0.0722 * linear(blue)
    }
}
