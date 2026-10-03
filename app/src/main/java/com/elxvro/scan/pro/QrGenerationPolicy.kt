package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement
import com.elxvro.scan.qrcard.LogoMode

data class QrGenerationOptions(
    val size: Int,
    val margin: Int,
    val highErrorCorrection: Boolean
)

object QrGenerationPolicy {
    fun resolve(
        requestedSize: Int,
        requestedMargin: Int,
        logoMode: LogoMode,
        entitlement: ProEntitlement
    ): QrGenerationOptions {
        val maxSize = QrPremiumPolicy.maxQrExportSize(entitlement)
        val hasEmbeddedLogo = logoMode != LogoMode.NONE
        return QrGenerationOptions(
            size = requestedSize.coerceIn(256, maxSize),
            margin = if (hasEmbeddedLogo) requestedMargin.coerceIn(4, 8) else requestedMargin.coerceIn(1, 8),
            highErrorCorrection = hasEmbeddedLogo
        )
    }
}
