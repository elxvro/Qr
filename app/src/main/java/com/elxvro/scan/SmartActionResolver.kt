package com.elxvro.scan

enum class SmartActionType {
    OPEN_URL,
    DIAL,
    EMAIL,
    SMS,
    MAP,
    WIFI,
    CONTACT,
    CALENDAR,
    SEARCH_PRODUCT,
    SHARE_TEXT
}

data class SmartAction(
    val type: SmartActionType,
    val label: String,
    val value: String
)

object SmartActionResolver {
    fun resolve(raw: String, kind: String, semanticType: String = ""): SmartAction {
        val value = raw.trim()
        val semantic = semanticType.trim().uppercase()
        return when {
            semantic == "URL" || value.startsWith("http://", true) || value.startsWith("https://", true) ->
                SmartAction(SmartActionType.OPEN_URL, "Siteyi Aç", normalizeUrl(value))

            semantic == "PHONE" || value.startsWith("tel:", true) ->
                SmartAction(SmartActionType.DIAL, "Ara", withScheme(value, "tel:"))

            semantic == "EMAIL" || value.startsWith("mailto:", true) ->
                SmartAction(SmartActionType.EMAIL, "E-posta Gönder", withScheme(value, "mailto:"))

            semantic == "SMS" || value.startsWith("sms:", true) || value.startsWith("smsto:", true) ->
                SmartAction(SmartActionType.SMS, "SMS Gönder", normalizeSms(value))

            semantic == "GEO" || value.startsWith("geo:", true) ->
                SmartAction(SmartActionType.MAP, "Haritada Aç", value)

            semantic == "WIFI" || value.startsWith("WIFI:", true) ->
                SmartAction(SmartActionType.WIFI, "Wi-Fi Ayarları", value)

            semantic == "CONTACT" || value.startsWith("BEGIN:VCARD", true) || value.startsWith("MECARD:", true) ->
                SmartAction(SmartActionType.CONTACT, "Kişilere Ekle", value)

            semantic == "CALENDAR" || value.startsWith("BEGIN:VEVENT", true) ->
                SmartAction(SmartActionType.CALENDAR, "Takvime Ekle", value)

            semantic == "PRODUCT" || kind.equals("Barkod", true) ->
                SmartAction(SmartActionType.SEARCH_PRODUCT, "Ürünü Ara", value)

            else -> SmartAction(SmartActionType.SHARE_TEXT, "Paylaş", value)
        }
    }

    private fun normalizeUrl(value: String): String = when {
        value.startsWith("http://", true) || value.startsWith("https://", true) -> value
        else -> "https://$value"
    }

    private fun withScheme(value: String, scheme: String): String =
        if (value.startsWith(scheme, true)) value else scheme + value

    private fun normalizeSms(value: String): String = when {
        value.startsWith("smsto:", true) || value.startsWith("sms:", true) -> value
        else -> "smsto:$value"
    }
}
