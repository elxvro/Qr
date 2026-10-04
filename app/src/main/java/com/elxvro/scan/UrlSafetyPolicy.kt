package com.elxvro.scan

import java.net.IDN
import java.net.URI

enum class UrlRiskLevel {
    LOW,
    MEDIUM,
    HIGH
}

enum class UrlRiskReason {
    INSECURE_HTTP,
    IP_ADDRESS_HOST,
    USER_INFO_PRESENT,
    IDN_OR_PUNYCODE_HOST,
    URL_SHORTENER,
    INVALID_OR_MISSING_HOST
}

data class UrlSafetyReport(
    val level: UrlRiskLevel,
    val host: String?,
    val reasons: Set<UrlRiskReason>
)

object UrlSafetyPolicy {
    private val shorteners = setOf(
        "bit.ly",
        "t.co",
        "tinyurl.com",
        "goo.gl",
        "is.gd",
        "buff.ly",
        "ow.ly",
        "rebrand.ly",
        "cutt.ly",
        "shorturl.at"
    )

    fun analyze(value: String): UrlSafetyReport {
        val trimmed = value.trim()
        val uri = runCatching { URI(trimmed) }.getOrNull()
            ?: return invalidReport()

        val scheme = uri.scheme?.lowercase()
        if (scheme != "http" && scheme != "https") {
            return invalidReport()
        }

        val authority = uri.rawAuthority.orEmpty()
        val userInfoPresent = !uri.rawUserInfo.isNullOrBlank() || authority.contains('@')
        val rawHost = extractHost(uri, authority)
        if (rawHost.isBlank()) {
            return UrlSafetyReport(
                level = UrlRiskLevel.HIGH,
                host = null,
                reasons = buildSet {
                    add(UrlRiskReason.INVALID_OR_MISSING_HOST)
                    if (userInfoPresent) add(UrlRiskReason.USER_INFO_PRESENT)
                }
            )
        }

        val normalizedHost = normalizeHost(rawHost)
        if (normalizedHost.isBlank()) return invalidReport()

        val reasons = linkedSetOf<UrlRiskReason>()
        if (scheme == "http") reasons += UrlRiskReason.INSECURE_HTTP
        if (userInfoPresent) reasons += UrlRiskReason.USER_INFO_PRESENT
        if (isIpAddress(normalizedHost)) reasons += UrlRiskReason.IP_ADDRESS_HOST
        if (isIdnOrPunycode(rawHost, normalizedHost)) reasons += UrlRiskReason.IDN_OR_PUNYCODE_HOST
        if (isShortener(normalizedHost)) reasons += UrlRiskReason.URL_SHORTENER

        val level = when {
            UrlRiskReason.USER_INFO_PRESENT in reasons ||
                UrlRiskReason.INVALID_OR_MISSING_HOST in reasons -> UrlRiskLevel.HIGH
            reasons.isNotEmpty() -> UrlRiskLevel.MEDIUM
            else -> UrlRiskLevel.LOW
        }

        return UrlSafetyReport(
            level = level,
            host = normalizedHost,
            reasons = reasons
        )
    }

    private fun invalidReport(): UrlSafetyReport = UrlSafetyReport(
        level = UrlRiskLevel.HIGH,
        host = null,
        reasons = setOf(UrlRiskReason.INVALID_OR_MISSING_HOST)
    )

    private fun extractHost(uri: URI, authority: String): String {
        uri.host?.takeIf { it.isNotBlank() }?.let { return it.trim('[', ']') }
        if (authority.isBlank()) return ""

        val withoutUserInfo = authority.substringAfterLast('@')
        if (withoutUserInfo.startsWith("[")) {
            return withoutUserInfo.substringAfter('[').substringBefore(']')
        }

        val colonCount = withoutUserInfo.count { it == ':' }
        return if (colonCount == 1 && withoutUserInfo.substringAfterLast(':').all { it.isDigit() }) {
            withoutUserInfo.substringBeforeLast(':')
        } else {
            withoutUserInfo
        }
    }

    private fun normalizeHost(host: String): String {
        val cleaned = host.trim().trimEnd('.').lowercase()
        return runCatching { IDN.toASCII(cleaned) }.getOrDefault(cleaned).lowercase()
    }

    private fun isIdnOrPunycode(rawHost: String, normalizedHost: String): Boolean {
        if (rawHost.any { it.code > 127 }) return true
        return normalizedHost.split('.').any { it.startsWith("xn--", ignoreCase = true) }
    }

    private fun isShortener(host: String): Boolean =
        shorteners.any { host == it || host.endsWith(".$it") }

    private fun isIpAddress(host: String): Boolean {
        if (host.contains(':')) {
            return host.matches(Regex("^[0-9a-fA-F:]+$"))
        }

        val parts = host.split('.')
        if (parts.size != 4) return false
        return parts.all { part ->
            part.isNotEmpty() &&
                part.all(Char::isDigit) &&
                part.toIntOrNull()?.let { it in 0..255 } == true
        }
    }
}
