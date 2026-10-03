package com.elxvro.scan.qrcard

enum class QrCardAccentStyle {
    RAIL,
    HEADER_BAND,
    NETWORK_BADGE,
    PROFILE_RING,
    EVENT_BAND,
    BRAND_STRIPE,
    PROMO_CORNER,
    TICKET_STUB
}

enum class QrCardTextAlignment {
    START,
    CENTER
}

enum class QrCardQrFrameStyle {
    PLAIN,
    BORDERED,
    ELEVATED,
    RING,
    EVENT
}

data class QrCardPresentation(
    val accentStyle: QrCardAccentStyle,
    val titleAlignment: QrCardTextAlignment,
    val qrFrameStyle: QrCardQrFrameStyle,
    val titleMaxLines: Int,
    val bodyMaxLines: Int
)

object QrCardPresentationPolicy {
    fun forTemplate(template: QrCardTemplate): QrCardPresentation = when (template) {
        QrCardTemplate.MINIMAL -> QrCardPresentation(
            QrCardAccentStyle.RAIL, QrCardTextAlignment.START, QrCardQrFrameStyle.PLAIN, 2, 2
        )
        QrCardTemplate.CORPORATE -> QrCardPresentation(
            QrCardAccentStyle.HEADER_BAND, QrCardTextAlignment.START, QrCardQrFrameStyle.BORDERED, 2, 2
        )
        QrCardTemplate.WIFI -> QrCardPresentation(
            QrCardAccentStyle.NETWORK_BADGE, QrCardTextAlignment.START, QrCardQrFrameStyle.ELEVATED, 2, 3
        )
        QrCardTemplate.SOCIAL -> QrCardPresentation(
            QrCardAccentStyle.PROFILE_RING, QrCardTextAlignment.CENTER, QrCardQrFrameStyle.RING, 2, 3
        )
        QrCardTemplate.EVENT -> QrCardPresentation(
            QrCardAccentStyle.EVENT_BAND, QrCardTextAlignment.START, QrCardQrFrameStyle.EVENT, 2, 3
        )
        QrCardTemplate.BUSINESS -> QrCardPresentation(
            QrCardAccentStyle.BRAND_STRIPE, QrCardTextAlignment.START, QrCardQrFrameStyle.BORDERED, 2, 3
        )
        QrCardTemplate.PROMO -> QrCardPresentation(
            QrCardAccentStyle.PROMO_CORNER, QrCardTextAlignment.CENTER, QrCardQrFrameStyle.RING, 2, 3
        )
        QrCardTemplate.TICKET -> QrCardPresentation(
            QrCardAccentStyle.TICKET_STUB, QrCardTextAlignment.START, QrCardQrFrameStyle.EVENT, 2, 3
        )
    }
}
