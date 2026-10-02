package com.elxvro.scan

object SafeActionPolicy {
    fun mayAutoOpen(action: SmartAction): Boolean {
        if (action.type != SmartActionType.OPEN_URL) return false
        val value = action.value.trim()
        return value.startsWith("https://", ignoreCase = true) ||
            value.startsWith("http://", ignoreCase = true)
    }
}
