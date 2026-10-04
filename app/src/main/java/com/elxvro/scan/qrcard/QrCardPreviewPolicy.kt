package com.elxvro.scan.qrcard

object QrCardPreviewPolicy {
    fun sanitize(model: QrCardModel): QrCardModel {
        val defaults = model.template.defaults()
        val readableColors = QrContrastPolicy.isReadable(
            model.qrForegroundArgb,
            model.qrBackgroundArgb
        )
        val safeLogoScale = model.logoScaleFraction
            .takeIf { it.isFinite() && it > 0f && it <= QrCardValidator.MAX_LOGO_SCALE }
            ?: defaults.logoScaleFraction
        val safeAspect = model.cardAspectRatio
            .takeIf {
                it.isFinite() &&
                    it >= QrCardValidator.MIN_CARD_ASPECT_RATIO &&
                    it <= QrCardValidator.MAX_CARD_ASPECT_RATIO
            }
            ?: defaults.cardAspectRatio

        return model.copy(
            payload = model.payload.ifBlank { "https://elxvro.com" },
            title = if (model.designPreset != null) model.title else model.title.ifBlank { defaultTitle(model.template) },
            wifiSsid = model.wifiSsid.ifBlank { "ELXVRO Wi-Fi" },
            socialHandle = model.socialHandle.ifBlank { "@elxvro" },
            eventDate = model.eventDate.ifBlank { "Etkinlik tarihi" },
            eventLocation = model.eventLocation.ifBlank { "Etkinlik konumu" },
            qrForegroundArgb = if (readableColors) model.qrForegroundArgb else defaults.qrForegroundArgb,
            qrBackgroundArgb = if (readableColors) model.qrBackgroundArgb else defaults.qrBackgroundArgb,
            logoScaleFraction = safeLogoScale,
            quietZoneModules = model.quietZoneModules.coerceAtLeast(QrCardValidator.MIN_QUIET_ZONE_MODULES),
            cardAspectRatio = safeAspect
        )
    }

    private fun defaultTitle(template: QrCardTemplate): String = when (template) {
        QrCardTemplate.MINIMAL -> "ELXVRO"
        QrCardTemplate.CORPORATE -> "Kurumsal QR"
        QrCardTemplate.WIFI -> "Wi-Fi"
        QrCardTemplate.SOCIAL -> "Sosyal Medya"
        QrCardTemplate.EVENT -> "Etkinlik"
        QrCardTemplate.BUSINESS -> "Business"
        QrCardTemplate.PROMO -> "Kampanya"
        QrCardTemplate.TICKET -> "Bilet"
    }
}
