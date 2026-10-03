package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement

enum class ProFeature {
    SCANNER,
    CORE_QR,
    OWN_LOGO,
    QR_CARD,
    HIGH_RES_EXPORT,
    PREMIUM_STYLE
}

object ProGate {
    fun canUse(feature: ProFeature, entitlement: ProEntitlement): Boolean {
        if (feature == ProFeature.SCANNER || feature == ProFeature.CORE_QR) return true
        return entitlement is ProEntitlement.Pro
    }
}
