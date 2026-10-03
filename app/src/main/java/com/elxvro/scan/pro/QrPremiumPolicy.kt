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
        return if (entitlement is ProEntitlement.Pro) 4096 else 900
    }

    fun allowedQrExportSizes(entitlement: ProEntitlement): List<Int> {
        return if (entitlement is ProEntitlement.Pro) {
            listOf(512, 900, 1200, 2048, 3072, 4096)
        } else {
            listOf(512, 900)
        }
    }
}
