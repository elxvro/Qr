package com.elxvro.scan

object SafeActionPolicy {
    fun mayAutoOpen(action: SmartAction): Boolean {
        if (action.type != SmartActionType.OPEN_URL) return false
        val value = action.value.trim()
        if (!value.startsWith("https://", ignoreCase = true)) return false
        return UrlSafetyPolicy.analyze(value).level == UrlRiskLevel.LOW
    }
}
