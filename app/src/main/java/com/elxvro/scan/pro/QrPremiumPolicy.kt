package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement
import com.elxvro.scan.qrcard.LogoMode

object QrPremiumPolicy {
    fun allowedLogoModes(entitlement: ProEntitlement): List<LogoMode> {
        return if (entitlement is ProEntitlement.Pro) {
            listOf(LogoMode.ELXVRO, LogoMode.CUSTOM, LogoMode.NONE)
        } else {
            listOf(LogoMode.ELXVRO)
        }
    }

    fun defaultLogoMode(entitlement: ProEntitlement): LogoMode = LogoMode.ELXVRO

    fun canBrowseQrCards(entitlement: ProEntitlement): Boolean = true

    fun canEditQrCards(entitlement: ProEntitlement): Boolean = entitlement is ProEntitlement.Pro

    fun canExportQrCards(entitlement: ProEntitlement): Boolean = entitlement is ProEntitlement.Pro

    fun maxQrExportSize(entitlement: ProEntitlement): Int {
        return if (entitlement is ProEntitlement.Pro) 2048 else 900
    }
}
