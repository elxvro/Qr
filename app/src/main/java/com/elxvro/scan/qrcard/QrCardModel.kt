package com.elxvro.scan.qrcard

import android.graphics.Color

enum class QrCardTemplate { MINIMAL, CORPORATE, WIFI, SOCIAL, EVENT }
enum class QrPosition { TOP, CENTER, BOTTOM }

data class QrCardModel(
    val payload: String,
    val template: QrCardTemplate = QrCardTemplate.MINIMAL,
    val title: String = "",
    val subtitle: String = "",
    val detail1: String = "",
    val detail2: String = "",
    val accentColor: Int = Color.rgb(0, 104, 248),
    val backgroundColor: Int = Color.WHITE,
    val qrPosition: QrPosition = QrPosition.CENTER,
    val logoScale: Float = 0.18f
)

data class QrCardValidation(val isValid: Boolean, val reason: String? = null)

object QrCardValidator {
    fun validate(model: QrCardModel): QrCardValidation = when {
        model.payload.isBlank() -> QrCardValidation(false, "QR içeriği boş olamaz")
        model.logoScale !in 0f..0.22f -> QrCardValidation(false, "Logo QR genişliğinin %22'sini geçemez")
        else -> QrCardValidation(true)
    }
}
