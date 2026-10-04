package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement

data class ProSettingsState(
    val statusLabel: String,
    val showUpgrade: Boolean,
    val showManage: Boolean,
    val showRestore: Boolean,
    val showRetry: Boolean
)

object ProSettingsPresentation {
    fun from(
        entitlement: ProEntitlement,
        testMode: Boolean = false
    ): ProSettingsState {
        if (testMode) {
            return ProSettingsState(
                statusLabel = "PRO TEST aktif",
                showUpgrade = false,
                showManage = false,
                showRestore = false,
                showRetry = false
            )
        }

        return when (entitlement) {
        ProEntitlement.Pro -> ProSettingsState(
            statusLabel = "PRO aktif",
            showUpgrade = false,
            showManage = true,
            showRestore = true,
            showRetry = false
        )
        ProEntitlement.Pending -> ProSettingsState(
            statusLabel = "Satın alma beklemede",
            showUpgrade = false,
            showManage = false,
            showRestore = true,
            showRetry = true
        )
        ProEntitlement.Unknown -> ProSettingsState(
            statusLabel = "Google Play kontrol ediliyor",
            showUpgrade = false,
            showManage = false,
            showRestore = true,
            showRetry = true
        )
        is ProEntitlement.Error -> ProSettingsState(
            statusLabel = "Google Play bağlantı hatası",
            showUpgrade = true,
            showManage = false,
            showRestore = true,
            showRetry = true
        )
        ProEntitlement.Free -> ProSettingsState(
            statusLabel = "Ücretsiz plan",
            showUpgrade = true,
            showManage = false,
            showRestore = true,
            showRetry = false
        )
        }
    }
}
