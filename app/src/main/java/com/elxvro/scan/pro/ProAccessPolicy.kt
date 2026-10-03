package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement

enum class ProFeature {
    CUSTOM_LOGO,
    REMOVE_LOGO,
    QR_CARD,
    HIGH_RES_EXPORT,
    ADVANCED_STYLE
}

object ProAccessPolicy {
    fun canUse(feature: ProFeature, entitlement: ProEntitlement): Boolean = entitlement is ProEntitlement.Pro
}
