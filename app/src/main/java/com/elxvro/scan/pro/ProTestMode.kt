package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement

object ProTestMode {
    fun resolve(
        entitlement: ProEntitlement,
        testMode: Boolean
    ): ProEntitlement = if (testMode) ProEntitlement.Pro else entitlement
}
