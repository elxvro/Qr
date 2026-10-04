package com.elxvro.scan

object UrlSafetyPresentation {
    fun label(level: UrlRiskLevel): String = when (level) {
        UrlRiskLevel.LOW -> "Düşük risk"
        UrlRiskLevel.MEDIUM -> "Orta risk"
        UrlRiskLevel.HIGH -> "Yüksek risk"
    }

    fun reasonText(reason: UrlRiskReason): String = when (reason) {
        UrlRiskReason.INSECURE_HTTP -> "Bağlantı şifreli HTTPS kullanmıyor."
        UrlRiskReason.IP_ADDRESS_HOST -> "Bağlantı bir alan adı yerine doğrudan IP adresine gidiyor."
        UrlRiskReason.USER_INFO_PRESENT -> "Adres, gerçek hedefi gizleyebilen @ kullanıcı bilgisi içeriyor."
        UrlRiskReason.IDN_OR_PUNYCODE_HOST -> "Alan adı IDN/Punycode içeriyor; benzer görünen karakterlere dikkat et."
        UrlRiskReason.URL_SHORTENER -> "Bağlantı gerçek hedefi gizleyen bir URL kısaltıcı kullanıyor."
        UrlRiskReason.INVALID_OR_MISSING_HOST -> "Bağlantının hedef alan adı doğrulanamadı."
    }
}
