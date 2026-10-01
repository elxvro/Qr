package com.elxvro.scan

object QrPayloadBuilder {
    fun build(type: String, primary: String, secondary: String = ""): String {
        val main = primary.trim()
        val extra = secondary.trim()
        return when (type) {
            "Telefon" -> if (main.startsWith("tel:", ignoreCase = true)) main else "tel:$main"
            "E-posta" -> if (main.startsWith("mailto:", ignoreCase = true)) main else "mailto:$main"
            "Wi-Fi" -> "WIFI:T:WPA;S:${escapeWifi(main)};P:${escapeWifi(extra)};;"
            "Kişi" -> buildString {
                append("BEGIN:VCARD\nVERSION:3.0\nFN:")
                append(main)
                if (extra.isNotBlank()) append("\nTEL:").append(extra)
                append("\nEND:VCARD")
            }
            else -> main
        }
    }

    private fun escapeWifi(value: String): String = buildString {
        value.forEach { c ->
            if (c == '\\' || c == ';' || c == ',' || c == ':' || c == '"') append('\\')
            append(c)
        }
    }
}
