package com.elxvro.scan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class V2BehaviorTest {
    @Test
    fun safe_auto_open_allows_only_http_and_https() {
        assertTrue(V2Policy.isSafeAutoOpen("https://elxvro.com"))
        assertTrue(V2Policy.isSafeAutoOpen("http://example.com/path"))
        assertFalse(V2Policy.isSafeAutoOpen("intent://scan/#Intent;scheme=zxing;end"))
        assertFalse(V2Policy.isSafeAutoOpen("javascript:alert(1)"))
        assertFalse(V2Policy.isSafeAutoOpen("tel:+905551234567"))
    }

    @Test
    fun cooldown_is_clamped_to_user_safe_range() {
        assertEquals(500L, V2Policy.normalizeCooldownMs(0))
        assertEquals(1500L, V2Policy.normalizeCooldownMs(1500))
        assertEquals(5000L, V2Policy.normalizeCooldownMs(9000))
    }
}
