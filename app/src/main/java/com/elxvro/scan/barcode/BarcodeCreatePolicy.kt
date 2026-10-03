package com.elxvro.scan.barcode

enum class BarcodeCreateType(
    val label: String,
    val historyFormat: String
) {
    CODE_128("CODE 128", "CODE_128"),
    EAN_13("EAN-13", "EAN_13"),
    UPC_A("UPC-A", "UPC_A")
}

sealed interface BarcodeCreateResult {
    data class Valid(val normalizedValue: String) : BarcodeCreateResult
    data class Invalid(val message: String) : BarcodeCreateResult
}

object BarcodeCreatePolicy {
    private const val MAX_CODE128_LENGTH = 80

    fun validate(type: BarcodeCreateType, rawValue: String): BarcodeCreateResult {
        val value = rawValue.trim()
        if (value.isBlank()) {
            return BarcodeCreateResult.Invalid("Barkod değeri boş bırakılamaz")
        }

        return when (type) {
            BarcodeCreateType.CODE_128 -> validateCode128(value)
            BarcodeCreateType.EAN_13 -> validateRetail(value, dataLength = 12, label = "EAN-13")
            BarcodeCreateType.UPC_A -> validateRetail(value, dataLength = 11, label = "UPC-A")
        }
    }

    private fun validateCode128(value: String): BarcodeCreateResult {
        if (value.length > MAX_CODE128_LENGTH) {
            return BarcodeCreateResult.Invalid("CODE 128 en fazla " + MAX_CODE128_LENGTH + " karakter olabilir")
        }
        if (value.any { it.code !in 32..126 }) {
            return BarcodeCreateResult.Invalid("CODE 128 yalnızca yazdırılabilir ASCII karakterlerini destekler")
        }
        return BarcodeCreateResult.Valid(value)
    }

    private fun validateRetail(
        value: String,
        dataLength: Int,
        label: String
    ): BarcodeCreateResult {
        if (value.any { !it.isDigit() }) {
            return BarcodeCreateResult.Invalid(label + " yalnızca rakam içerebilir")
        }

        return when (value.length) {
            dataLength -> BarcodeCreateResult.Valid(value + calculateCheckDigit(value))
            dataLength + 1 -> {
                val data = value.dropLast(1)
                val expected = calculateCheckDigit(data)
                if (value.last() == expected) {
                    BarcodeCreateResult.Valid(value)
                } else {
                    BarcodeCreateResult.Invalid(label + " kontrol hanesi geçersiz")
                }
            }
            else -> BarcodeCreateResult.Invalid(
                label + " " + dataLength + " veya " + (dataLength + 1) + " haneli olmalıdır"
            )
        }
    }

    private fun calculateCheckDigit(data: String): Char {
        var sum = 0
        var multiplier = 3
        for (index in data.indices.reversed()) {
            sum += data[index].digitToInt() * multiplier
            multiplier = if (multiplier == 3) 1 else 3
        }
        return ((10 - (sum % 10)) % 10).digitToChar()
    }
}
