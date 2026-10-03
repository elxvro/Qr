package com.elxvro.scan.qrcard

enum class QrCardAccentStyle {
    RAIL,
    HEADER_BAND,
    NETWORK_BADGE,
    PROFILE_RING,
    EVENT_BAND
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
            accentStyle = QrCardAccentStyle.RAIL,
            titleAlignment = QrCardTextAlignment.START,
            qrFrameStyle = QrCardQrFrameStyle.PLAIN,
            titleMaxLines = 2,
            bodyMaxLines = 2
        )
        QrCardTemplate.CORPORATE -> QrCardPresentation(
            accentStyle = QrCardAccentStyle.HEADER_BAND,
            titleAlignment = QrCardTextAlignment.START,
            qrFrameStyle = QrCardQrFrameStyle.BORDERED,
            titleMaxLines = 2,
            bodyMaxLines = 2
        )
        QrCardTemplate.WIFI -> QrCardPresentation(
            accentStyle = QrCardAccentStyle.NETWORK_BADGE,
            titleAlignment = QrCardTextAlignment.START,
            qrFrameStyle = QrCardQrFrameStyle.ELEVATED,
            titleMaxLines = 2,
            bodyMaxLines = 3
        )
        QrCardTemplate.SOCIAL -> QrCardPresentation(
            accentStyle = QrCardAccentStyle.PROFILE_RING,
            titleAlignment = QrCardTextAlignment.CENTER,
            qrFrameStyle = QrCardQrFrameStyle.RING,
            titleMaxLines = 2,
            bodyMaxLines = 3
        )
        QrCardTemplate.EVENT -> QrCardPresentation(
            accentStyle = QrCardAccentStyle.EVENT_BAND,
            titleAlignment = QrCardTextAlignment.START,
            qrFrameStyle = QrCardQrFrameStyle.EVENT,
            titleMaxLines = 2,
            bodyMaxLines = 3
        )
    }
}
