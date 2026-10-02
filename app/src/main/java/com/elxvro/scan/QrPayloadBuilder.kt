package com.elxvro.scan

enum class WifiSecurity { WPA, WEP, OPEN }

object QrPayloadBuilder {
    fun build(type: String, primary: String, secondary: String = ""): String {
        val main = primary.trim()
        val extra = secondary.trim()
        return when (type) {
            "URL" -> when {
                main.startsWith("http://", ignoreCase = true) || main.startsWith("https://", ignoreCase = true) -> main
                main.isBlank() -> main
                else -> "https://$main"
            }
            "Telefon" -> if (main.startsWith("tel:", ignoreCase = true)) main else "tel:$main"
            "E-posta" -> if (main.startsWith("mailto:", ignoreCase = true)) main else "mailto:$main"
            "Wi-Fi" -> buildWifi(main, extra, WifiSecurity.WPA)
            "Kişi" -> buildString {
                append("BEGIN:VCARD\nVERSION:3.0\nFN:")
                append(escapeVCard(main))
                if (extra.isNotBlank()) append("\nTEL:").append(escapeVCard(extra))
                append("\nEND:VCARD")
            }
            "SMS" -> {
                val number = main
                    .removePrefix("smsto:")
                    .removePrefix("sms:")
                    .removePrefix("tel:")
                "SMSTO:$number:$extra"
            }
            "Konum" -> if (main.startsWith("geo:", ignoreCase = true)) main else "geo:$main"
            "Takvim" -> buildString {
                append("BEGIN:VEVENT\nSUMMARY:").append(escapeCalendar(main))
                if (extra.isNotBlank()) append("\nDTSTART:").append(extra)
                append("\nEND:VEVENT")
            }
            else -> main
        }
    }

    fun buildWifi(ssid: String, password: String, security: WifiSecurity): String {
        val escapedSsid = escapeWifi(ssid.trim())
        val escapedPassword = escapeWifi(password.trim())
        return when (security) {
            WifiSecurity.WPA -> "WIFI:T:WPA;S:$escapedSsid;P:$escapedPassword;;"
            WifiSecurity.WEP -> "WIFI:T:WEP;S:$escapedSsid;P:$escapedPassword;;"
            WifiSecurity.OPEN -> "WIFI:T:nopass;S:$escapedSsid;;"
        }
    }

    private fun escapeWifi(value: String): String = buildString {
        value.forEach { c ->
            if (c == '\\' || c == ';' || c == ',' || c == ':' || c == '"') append('\\')
            append(c)
        }
    }

    private fun escapeVCard(value: String): String = value
        .replace("\\", "\\\\")
        .replace("\n", "\\n")
        .replace(";", "\\;")
        .replace(",", "\\,")

    private fun escapeCalendar(value: String): String = value
        .replace("\\", "\\\\")
        .replace("\n", "\\n")
        .replace(";", "\\;")
        .replace(",", "\\,")
}
