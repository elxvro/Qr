package com.elxvro.scan

import com.google.mlkit.vision.barcode.common.Barcode

data class ScanPresentation(
    val raw: String,
    val kind: String,
    val format: String,
    val typeLabel: String,
    val semanticType: String,
    val action: SmartAction,
    val historyId: String
)

object BarcodePresentation {
    fun formatName(format: Int): String = when (format) {
        Barcode.FORMAT_QR_CODE -> "QR Code"
        Barcode.FORMAT_EAN_13 -> "EAN-13"
        Barcode.FORMAT_EAN_8 -> "EAN-8"
        Barcode.FORMAT_UPC_A -> "UPC-A"
        Barcode.FORMAT_UPC_E -> "UPC-E"
        Barcode.FORMAT_CODE_128 -> "Code 128"
        Barcode.FORMAT_CODE_39 -> "Code 39"
        Barcode.FORMAT_CODE_93 -> "Code 93"
        Barcode.FORMAT_DATA_MATRIX -> "Data Matrix"
        Barcode.FORMAT_PDF417 -> "PDF417"
        Barcode.FORMAT_AZTEC -> "Aztec"
        Barcode.FORMAT_ITF -> "ITF"
        Barcode.FORMAT_CODABAR -> "Codabar"
        else -> "Barkod"
    }

    fun semanticType(valueType: Int): String = when (valueType) {
        Barcode.TYPE_URL -> "URL"
        Barcode.TYPE_PHONE -> "PHONE"
        Barcode.TYPE_EMAIL -> "EMAIL"
        Barcode.TYPE_WIFI -> "WIFI"
        Barcode.TYPE_SMS -> "SMS"
        Barcode.TYPE_GEO -> "GEO"
        Barcode.TYPE_CONTACT_INFO -> "CONTACT"
        Barcode.TYPE_PRODUCT, Barcode.TYPE_ISBN -> "PRODUCT"
        Barcode.TYPE_CALENDAR_EVENT -> "CALENDAR"
        else -> "TEXT"
    }

    fun typeLabel(valueType: Int): String = when (valueType) {
        Barcode.TYPE_URL -> "Web Sitesi"
        Barcode.TYPE_PHONE -> "Telefon"
        Barcode.TYPE_EMAIL -> "E-posta"
        Barcode.TYPE_WIFI -> "Wi-Fi"
        Barcode.TYPE_SMS -> "SMS"
        Barcode.TYPE_GEO -> "Konum"
        Barcode.TYPE_CONTACT_INFO -> "Kişi"
        Barcode.TYPE_PRODUCT, Barcode.TYPE_ISBN -> "Ürün Barkodu"
        Barcode.TYPE_CALENDAR_EVENT -> "Takvim"
        else -> "Tarama Sonucu"
    }
}
