package com.elxvro.scan.qrcard

data class QrCardCopy(
    val brand: String,
    val title: String,
    val description: String,
    val cta: String
)

object QrCardCopyPolicy {
    fun resolve(model: QrCardModel): QrCardCopy = QrCardCopy(
        brand = model.brandText.trim(),
        title = model.title.trim(),
        description = model.descriptionText.trim(),
        cta = model.ctaText.trim()
    )
}
