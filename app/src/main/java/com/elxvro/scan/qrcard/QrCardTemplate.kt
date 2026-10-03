package com.elxvro.scan.qrcard

enum class QrCardTemplate {
    MINIMAL,
    CORPORATE,
    WIFI,
    SOCIAL,
    EVENT,
    BUSINESS,
    PROMO,
    TICKET;

    fun defaults(): QrCardDefaults = when (this) {
        MINIMAL -> QrCardDefaults(
            cardBackgroundArgb = 0xFFFFFFFF.toInt(),
            accentArgb = 0xFF0068F8.toInt(),
            textArgb = 0xFF111820.toInt(),
            qrForegroundArgb = 0xFF111820.toInt(),
            qrBackgroundArgb = 0xFFFFFFFF.toInt(),
            qrPosition = QrPosition.CENTER,
            cardAspectRatio = 1.586f
        )
        CORPORATE -> QrCardDefaults(
            cardBackgroundArgb = 0xFF090E15.toInt(),
            accentArgb = 0xFF00A3FF.toInt(),
            textArgb = 0xFFF8FAFC.toInt(),
            qrForegroundArgb = 0xFF111820.toInt(),
            qrBackgroundArgb = 0xFFFFFFFF.toInt(),
            qrPosition = QrPosition.CENTER,
            cardAspectRatio = 1.586f
        )
        WIFI -> QrCardDefaults(
            cardBackgroundArgb = 0xFFF7F8F9.toInt(),
            accentArgb = 0xFF00A86B.toInt(),
            textArgb = 0xFF111820.toInt(),
            qrForegroundArgb = 0xFF111820.toInt(),
            qrBackgroundArgb = 0xFFFFFFFF.toInt(),
            qrPosition = QrPosition.CENTER,
            cardAspectRatio = 1.25f
        )
        SOCIAL -> QrCardDefaults(
            cardBackgroundArgb = 0xFF111821.toInt(),
            accentArgb = 0xFF8B5CF6.toInt(),
            textArgb = 0xFFF8FAFC.toInt(),
            qrForegroundArgb = 0xFF111820.toInt(),
            qrBackgroundArgb = 0xFFFFFFFF.toInt(),
            qrPosition = QrPosition.CENTER,
            cardAspectRatio = 1.0f
        )
        EVENT -> QrCardDefaults(
            cardBackgroundArgb = 0xFFFFFFFF.toInt(),
            accentArgb = 0xFFFF7A00.toInt(),
            textArgb = 0xFF111820.toInt(),
            qrForegroundArgb = 0xFF111820.toInt(),
            qrBackgroundArgb = 0xFFFFFFFF.toInt(),
            qrPosition = QrPosition.BOTTOM,
            cardAspectRatio = 0.75f
        )
        BUSINESS -> QrCardDefaults(
            cardBackgroundArgb = 0xFFF3F7FC.toInt(),
            accentArgb = 0xFF1F4B99.toInt(),
            textArgb = 0xFF122033.toInt(),
            qrForegroundArgb = 0xFF122033.toInt(),
            qrBackgroundArgb = 0xFFFFFFFF.toInt(),
            qrPosition = QrPosition.CENTER,
            cardAspectRatio = 1.586f
        )
        PROMO -> QrCardDefaults(
            cardBackgroundArgb = 0xFFFFF4F8.toInt(),
            accentArgb = 0xFFFF3D7F.toInt(),
            textArgb = 0xFF2A1020.toInt(),
            qrForegroundArgb = 0xFF221018.toInt(),
            qrBackgroundArgb = 0xFFFFFFFF.toInt(),
            qrPosition = QrPosition.CENTER,
            cardAspectRatio = 1.0f
        )
        TICKET -> QrCardDefaults(
            cardBackgroundArgb = 0xFF10131A.toInt(),
            accentArgb = 0xFFFFD166.toInt(),
            textArgb = 0xFFF8FAFC.toInt(),
            qrForegroundArgb = 0xFF111820.toInt(),
            qrBackgroundArgb = 0xFFFFFFFF.toInt(),
            qrPosition = QrPosition.CENTER,
            cardAspectRatio = 1.586f
        )
    }
}

data class QrCardDefaults(
    val cardBackgroundArgb: Int,
    val accentArgb: Int,
    val textArgb: Int,
    val qrForegroundArgb: Int,
    val qrBackgroundArgb: Int,
    val logoScaleFraction: Float = 0.18f,
    val quietZoneModules: Int = 4,
    val qrPosition: QrPosition,
    val cardAspectRatio: Float
)
