package com.elxvro.scan

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultSheetPolicyTest {
    @Test
    fun hidesDuplicatePrimaryButtonForPlainShareText() {
        assertFalse(ResultSheetPolicy.showPrimaryAction(SmartActionType.SHARE_TEXT))
    }

    @Test
    fun keepsPrimaryButtonForPurposefulActions() {
        assertTrue(ResultSheetPolicy.showPrimaryAction(SmartActionType.OPEN_URL))
        assertTrue(ResultSheetPolicy.showPrimaryAction(SmartActionType.SEARCH_PRODUCT))
        assertTrue(ResultSheetPolicy.showPrimaryAction(SmartActionType.WIFI))
    }
}
