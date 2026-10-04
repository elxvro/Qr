package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Test

class QrCardTextColorPolicyTest {
    @Test
    fun everyVisibleTextColorCanBeOverriddenIndependently() {
        val theme = QrCardDesignPreset.CLASSIC_EXECUTIVE.theme()

        val colors = QrCardTextColorPolicy.resolve(
            theme = theme,
            brandTextArgb = 0xFFFF0000.toInt(),
            titleTextArgb = 0xFF00FF00.toInt(),
            bodyTextArgb = 0xFF0000FF.toInt(),
            ctaTextArgb = 0xFFFFFF00.toInt()
        )

        assertEquals(0xFFFF0000.toInt(), colors.brandArgb)
        assertEquals(0xFF00FF00.toInt(), colors.titleArgb)
        assertEquals(0xFF0000FF.toInt(), colors.bodyArgb)
        assertEquals(0xFFFFFF00.toInt(), colors.ctaArgb)
    }

    @Test
    fun unsetOptionalColorsFallBackToTheme() {
        val theme = QrCardDesignPreset.CLASSIC_EXECUTIVE.theme()

        val colors = QrCardTextColorPolicy.resolve(
            theme = theme,
            brandTextArgb = null,
            titleTextArgb = theme.titleArgb,
            bodyTextArgb = null,
            ctaTextArgb = null
        )

        assertEquals(theme.accentArgb, colors.brandArgb)
        assertEquals(theme.titleArgb, colors.titleArgb)
        assertEquals(theme.bodyArgb, colors.bodyArgb)
        assertEquals(QrCardTextColorPolicy.readableOn(theme.accentArgb), colors.ctaArgb)
    }
}
