package com.elxvro.scan.qrcard

enum class ValidationError {
    PAYLOAD_REQUIRED,
    LOGO_TOO_LARGE,
    LOGO_SIZE_INVALID,
    QUIET_ZONE_TOO_SMALL,
    QR_CONTRAST_TOO_LOW,
    CARD_ASPECT_RATIO_INVALID,
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
    const val MIN_CARD_ASPECT_RATIO = 0.50f
    const val MAX_CARD_ASPECT_RATIO = 2.00f

    fun validate(model: QrCardModel): ValidationResult {
        val errors = linkedSetOf<ValidationError>()

        if (model.payload.isBlank()) errors += ValidationError.PAYLOAD_REQUIRED
        if (
            model.logoMode != LogoMode.NONE &&
            (!model.logoScaleFraction.isFinite() || model.logoScaleFraction <= 0f)
        ) {
            errors += ValidationError.LOGO_SIZE_INVALID
        }
        if (
            model.logoMode != LogoMode.NONE &&
            model.logoScaleFraction.isFinite() &&
            model.logoScaleFraction > MAX_LOGO_SCALE
        ) {
            errors += ValidationError.LOGO_TOO_LARGE
        }
        if (model.quietZoneModules < MIN_QUIET_ZONE_MODULES) {
            errors += ValidationError.QUIET_ZONE_TOO_SMALL
        }
        if (!QrContrastPolicy.isReadable(model.qrForegroundArgb, model.qrBackgroundArgb)) {
            errors += ValidationError.QR_CONTRAST_TOO_LOW
        }
        if (
            !model.cardAspectRatio.isFinite() ||
            model.cardAspectRatio < MIN_CARD_ASPECT_RATIO ||
            model.cardAspectRatio > MAX_CARD_ASPECT_RATIO
        ) {
            errors += ValidationError.CARD_ASPECT_RATIO_INVALID
        }

        when (model.template) {
            QrCardTemplate.MINIMAL -> Unit
            QrCardTemplate.CORPORATE -> if (model.title.isBlank()) errors += ValidationError.TITLE_REQUIRED
            QrCardTemplate.WIFI -> if (model.wifiSsid.isBlank()) errors += ValidationError.WIFI_SSID_REQUIRED
            QrCardTemplate.SOCIAL -> {
                if (model.title.isBlank()) errors += ValidationError.TITLE_REQUIRED
                if (model.socialHandle.isBlank()) errors += ValidationError.SOCIAL_HANDLE_REQUIRED
            }
            QrCardTemplate.EVENT, QrCardTemplate.TICKET -> {
                if (model.title.isBlank()) errors += ValidationError.TITLE_REQUIRED
                if (model.eventDate.isBlank()) errors += ValidationError.EVENT_DATE_REQUIRED
                if (model.eventLocation.isBlank()) errors += ValidationError.EVENT_LOCATION_REQUIRED
            }
            QrCardTemplate.BUSINESS, QrCardTemplate.PROMO -> {
                if (model.title.isBlank()) errors += ValidationError.TITLE_REQUIRED
            }
        }

        return if (errors.isEmpty()) ValidationResult.Valid(model) else ValidationResult.Invalid(errors)
    }
}
