package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement

enum class LogoMode { ELXVRO, CUSTOM, NONE }

object QrBrandingPolicy {
    fun effectiveLogoMode(entitlement: ProEntitlement, requested: LogoMode): LogoMode =
        if (entitlement is ProEntitlement.Pro) requested else LogoMode.ELXVRO

    fun maxExportSize(entitlement: ProEntitlement): Int =
        if (entitlement is ProEntitlement.Pro) 2048 else 900
}
