package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement

object ProPaywallPolicy {
    fun canStartPurchase(entitlement: ProEntitlement): Boolean {
        return entitlement is ProEntitlement.Free
    }
}
