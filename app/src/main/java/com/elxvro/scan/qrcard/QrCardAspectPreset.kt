package com.elxvro.scan.qrcard

enum class QrCardAspectPreset(val label: String) {
    DEFAULT("Şablon"),
    SQUARE("1:1"),
    WIDE("16:9"),
    CARD("Kart"),
    PORTRAIT("4:5"),
    STORY("9:16");

    fun resolve(template: QrCardTemplate): Float = when (this) {
        DEFAULT -> template.defaults().cardAspectRatio
        SQUARE -> 1.0f
        WIDE -> 16f / 9f
        CARD -> 1.586f
        PORTRAIT -> 0.8f
        STORY -> 9f / 16f
    }
}
