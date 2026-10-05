package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Test

class FixedCardCategoryPolicyTest {
    @Test
    fun categoriesFilterToFiveCards() {
        listOf(FixedCardTier.FREE, FixedCardTier.PRO).forEach { tier ->
            FixedCardLibrary.categories(tier).forEach { category ->
                assertEquals(5, FixedCardLibrary.byCategory(tier, category).size)
            }
        }
    }
}
