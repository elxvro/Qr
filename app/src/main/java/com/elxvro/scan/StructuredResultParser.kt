package com.elxvro.scan

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

enum class StructuredResultType {
    WIFI,
    CONTACT,
    CALENDAR,
    EMAIL,
    SMS,
    GEO,
    PRODUCT
}

data class StructuredResultField(
    val label: String,
    val value: String,
    val sensitive: Boolean = false
)

data class StructuredResultCard(
    val type: StructuredResultType,
    val title: String,
    val fields: List<StructuredResultField>
)

object StructuredResultParser {
    fun parse(
        raw: String,
        semanticType: String,
        kind: String,
        format: String
    ): StructuredResultCard? {
        val value = raw.trim()
        if (value.isBlank()) return null
        val semantic = semanticType.trim().uppercase()

        return when {
            semantic == "WIFI" || value.startsWith("WIFI:", ignoreCase = true) -> parseWifi(value)
            semantic == "CONTACT" ||
                value.startsWith("BEGIN:VCARD", ignoreCase = true) ||
                value.startsWith("MECARD:", ignoreCase = true) -> parseContact(value)
            semantic == "CALENDAR" || value.startsWith("BEGIN:VEVENT", ignoreCase = true) -> parseCalendar(value)
            semantic == "EMAIL" || value.startsWith("mailto:", ignoreCase = true) -> parseEmail(value)
            semantic == "SMS" ||
                value.startsWith("sms:", ignoreCase = true) ||
                value.startsWith("smsto:", ignoreCase = true) -> parseSms(value)
            semantic == "GEO" || value.startsWith("geo:", ignoreCase = true) -> parseGeo(value)
            semantic == "PRODUCT" || kind.equals("Barkod", ignoreCase = true) -> parseProduct(value, format)
            else -> null
        }
    }

    private fun parseWifi(raw: String): StructuredResultCard? {
        val body = raw.substringAfter(':', "")
        val values = parseEscapedPairs(body)
        val ssid = values["S"].orEmpty().trim()
        if (ssid.isBlank()) return null

        val securityRaw = values["T"].orEmpty().ifBlank { "Bilinmiyor" }
        val security = when {
            securityRaw.equals("nopass", ignoreCase = true) -> "Şifresiz"
            else -> securityRaw.uppercase()
        }
        val hidden = values["H"]?.equals("true", ignoreCase = true) == true
        val password = values["P"].orEmpty()

        return StructuredResultCard(
            type = StructuredResultType.WIFI,
            title = "Wi-Fi",
            fields = buildList {
                add(StructuredResultField("Ağ adı", ssid))
                add(StructuredResultField("Güvenlik", security))
                add(StructuredResultField("Gizli ağ", if (hidden) "Evet" else "Hayır"))
                if (password.isNotBlank()) {
                    add(StructuredResultField("Şifre", password, sensitive = true))
                }
            }
        )
    }

    private fun parseContact(raw: String): StructuredResultCard? {
        return if (raw.startsWith("MECARD:", ignoreCase = true)) {
            parseMecard(raw)
        } else {
            parseVcard(raw)
        }
    }

    private fun parseVcard(raw: String): StructuredResultCard? {
        val lines = raw.lineSequence()
            .map { it.trimEnd('\r') }
            .filter { it.isNotBlank() }
            .toList()

        fun field(key: String): String? = lines.firstNotNullOfOrNull { line ->
            val colon = line.indexOf(':')
            if (colon <= 0) return@firstNotNullOfOrNull null
            val name = line.substring(0, colon).substringBefore(';')
            if (!name.equals(key, ignoreCase = true)) return@firstNotNullOfOrNull null
            unescapeText(line.substring(colon + 1)).trim().takeIf { it.isNotBlank() }
        }

        val name = field("FN") ?: field("N")?.replace(';', ' ')?.trim()
        val phone = field("TEL")
        val email = field("EMAIL")
        if (name.isNullOrBlank() && phone.isNullOrBlank() && email.isNullOrBlank()) return null

        return StructuredResultCard(
            type = StructuredResultType.CONTACT,
            title = "Kişi",
            fields = buildList {
                name?.takeIf { it.isNotBlank() }?.let { add(StructuredResultField("Ad", it)) }
                phone?.takeIf { it.isNotBlank() }?.let { add(StructuredResultField("Telefon", it)) }
                email?.takeIf { it.isNotBlank() }?.let { add(StructuredResultField("E-posta", it)) }
            }
        )
    }

    private fun parseMecard(raw: String): StructuredResultCard? {
        val values = parseEscapedPairs(raw.substringAfter(':', ""))
        val name = values["N"]?.replace(',', ' ')?.trim()
        val phone = values["TEL"]?.trim()
        val email = values["EMAIL"]?.trim()
        if (name.isNullOrBlank() && phone.isNullOrBlank() && email.isNullOrBlank()) return null

        return StructuredResultCard(
            type = StructuredResultType.CONTACT,
            title = "Kişi",
            fields = buildList {
                name?.takeIf { it.isNotBlank() }?.let { add(StructuredResultField("Ad", it)) }
                phone?.takeIf { it.isNotBlank() }?.let { add(StructuredResultField("Telefon", it)) }
                email?.takeIf { it.isNotBlank() }?.let { add(StructuredResultField("E-posta", it)) }
            }
        )
    }

    private fun parseCalendar(raw: String): StructuredResultCard? {
        val values = parseLineFields(raw)
        val summary = values["SUMMARY"]?.trim()
        val start = values["DTSTART"]?.trim()
        val end = values["DTEND"]?.trim()
        val location = values["LOCATION"]?.trim()

        if (summary.isNullOrBlank() && start.isNullOrBlank() && end.isNullOrBlank() && location.isNullOrBlank()) {
            return null
        }

        return StructuredResultCard(
            type = StructuredResultType.CALENDAR,
            title = "Takvim",
            fields = buildList {
                summary?.takeIf { it.isNotBlank() }?.let { add(StructuredResultField("Etkinlik", it)) }
                start?.takeIf { it.isNotBlank() }?.let { add(StructuredResultField("Başlangıç", it)) }
                end?.takeIf { it.isNotBlank() }?.let { add(StructuredResultField("Bitiş", it)) }
                location?.takeIf { it.isNotBlank() }?.let { add(StructuredResultField("Konum", it)) }
            }
        )
    }

    private fun parseEmail(raw: String): StructuredResultCard? {
        val body = if (raw.startsWith("mailto:", ignoreCase = true)) raw.substringAfter(':') else raw
        val recipientEncoded = body.substringBefore('?').trim()
        val recipient = decodeUrl(recipientEncoded).trim()
        if (recipient.isBlank()) return null

        val query = body.substringAfter('?', "")
        val params = parseQuery(query)
        val subject = params["subject"]?.trim()
        val message = params["body"]?.trim()

        return StructuredResultCard(
            type = StructuredResultType.EMAIL,
            title = "E-posta",
            fields = buildList {
                add(StructuredResultField("Alıcı", recipient))
                subject?.takeIf { it.isNotBlank() }?.let { add(StructuredResultField("Konu", it)) }
                message?.takeIf { it.isNotBlank() }?.let { add(StructuredResultField("Mesaj", it)) }
            }
        )
    }

    private fun parseSms(raw: String): StructuredResultCard? {
        val lower = raw.lowercase()
        val withoutScheme = when {
            lower.startsWith("smsto:") -> raw.substring(6)
            lower.startsWith("sms:") -> raw.substring(4)
            else -> raw
        }

        val number: String
        val message: String?
        if (lower.startsWith("smsto:") && withoutScheme.contains(':')) {
            number = withoutScheme.substringBefore(':').trim()
            message = withoutScheme.substringAfter(':').trim().takeIf { it.isNotBlank() }
        } else {
            number = withoutScheme.substringBefore('?').trim()
            message = parseQuery(withoutScheme.substringAfter('?', ""))["body"]?.trim()
        }
        if (number.isBlank()) return null

        return StructuredResultCard(
            type = StructuredResultType.SMS,
            title = "SMS",
            fields = buildList {
                add(StructuredResultField("Numara", number))
                message?.takeIf { it.isNotBlank() }?.let { add(StructuredResultField("Mesaj", it)) }
            }
        )
    }

    private fun parseGeo(raw: String): StructuredResultCard? {
        val body = if (raw.startsWith("geo:", ignoreCase = true)) raw.substringAfter(':') else raw
        val coordinates = body.substringBefore('?').split(',')
        if (coordinates.size < 2) return null

        val latitude = coordinates[0].trim().toDoubleOrNull() ?: return null
        val longitude = coordinates[1].trim().toDoubleOrNull() ?: return null
        if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) return null

        return StructuredResultCard(
            type = StructuredResultType.GEO,
            title = "Konum",
            fields = listOf(
                StructuredResultField("Enlem", coordinates[0].trim()),
                StructuredResultField("Boylam", coordinates[1].trim())
            )
        )
    }

    private fun parseProduct(raw: String, format: String): StructuredResultCard? {
        if (raw.isBlank()) return null
        return StructuredResultCard(
            type = StructuredResultType.PRODUCT,
            title = "Ürün Barkodu",
            fields = listOf(
                StructuredResultField("Ürün kodu", raw),
                StructuredResultField("Format", format.ifBlank { "Barkod" })
            )
        )
    }

    private fun parseLineFields(raw: String): Map<String, String> = buildMap {
        raw.lineSequence().forEach { line ->
            val colon = line.indexOf(':')
            if (colon <= 0) return@forEach
            val key = line.substring(0, colon).substringBefore(';').trim().uppercase()
            val value = unescapeText(line.substring(colon + 1)).trim()
            if (key.isNotBlank() && value.isNotBlank() && key !in this) {
                put(key, value)
            }
        }
    }

    private fun parseQuery(query: String): Map<String, String> = buildMap {
        if (query.isBlank()) return@buildMap
        query.split('&').forEach { part ->
            val key = decodeUrl(part.substringBefore('=')).lowercase()
            val value = decodeUrl(part.substringAfter('=', ""))
            if (key.isNotBlank() && key !in this) put(key, value)
        }
    }

    private fun parseEscapedPairs(input: String): Map<String, String> {
        val parts = mutableListOf<String>()
        val current = StringBuilder()
        var escaped = false

        input.forEach { char ->
            when {
                escaped -> {
                    current.append(char)
                    escaped = false
                }
                char == '\\' -> escaped = true
                char == ';' -> {
                    parts += current.toString()
                    current.clear()
                }
                else -> current.append(char)
            }
        }
        if (escaped) current.append('\\')
        if (current.isNotEmpty()) parts += current.toString()

        return buildMap {
            parts.forEach { part ->
                val colon = part.indexOf(':')
                if (colon <= 0) return@forEach
                val key = part.substring(0, colon).trim().uppercase()
                val value = part.substring(colon + 1).trim()
                if (key.isNotBlank() && key !in this) put(key, value)
            }
        }
    }

    private fun unescapeText(value: String): String = value
        .replace("\\n", "\n", ignoreCase = true)
        .replace("\\,", ",")
        .replace("\\;", ";")
        .replace("\\\\", "\\")

    private fun decodeUrl(value: String): String =
        runCatching { URLDecoder.decode(value, StandardCharsets.UTF_8.name()) }
            .getOrDefault(value)
}
