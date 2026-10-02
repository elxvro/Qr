package com.elxvro.scan

import java.net.URI

/** Safety and preference bounds shared by the v2 scanner UI. */
object V2Policy {
    fun isSafeAutoOpen(raw: String): Boolean {
        val value = raw.trim()
        if (value.isEmpty()) return false
        return runCatching {
            val uri = URI(value)
            val scheme = uri.scheme?.lowercase()
            (scheme == "http" || scheme == "https") && !uri.host.isNullOrBlank()
        }.getOrDefault(false)
    }

    fun normalizeCooldownMs(valueMs: Int): Long = valueMs.coerceIn(500, 5000).toLong()
}
