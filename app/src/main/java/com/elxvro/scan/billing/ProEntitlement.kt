package com.elxvro.scan.billing

sealed interface ProEntitlement {
    data object Unknown : ProEntitlement
    data object Free : ProEntitlement
    data object Pending : ProEntitlement
    data class Pro(val source: String = "play") : ProEntitlement
    data class Error(val message: String) : ProEntitlement
}
