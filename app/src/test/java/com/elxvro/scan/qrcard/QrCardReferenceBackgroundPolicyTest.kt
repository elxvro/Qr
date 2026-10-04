package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCardReferenceBackgroundPolicyTest {
    @Test
    fun fixedModeNeverUsesUserPhotoAsBackground() {
        val policy = QrCardReferenceBackgroundPolicy.resolve(QrCardBackgroundMode.FIXED_BACKGROUND)
        assertFalse(policy.useUserPhoto)
        assertTrue(policy.useFixedDarkGold)
        assertEquals(0.0f, policy.photoOverlayAlpha)
    }

    @Test
    fun changingModeUsesUserPhotoAsFullBackground() {
        val policy = QrCardReferenceBackgroundPolicy.resolve(QrCardBackgroundMode.FULL_BACKGROUND)
        assertTrue(policy.useUserPhoto)
        assertFalse(policy.useFixedDarkGold)
        assertTrue(policy.photoOverlayAlpha > 0f)
    }
}
