package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement
import org.junit.Assert.assertEquals
import org.junit.Test

class QrPremiumExportV27Test {
    @Test
    fun proSupportsUpTo4096AndNewExportChoices() {
        assertEquals(4096, QrPremiumPolicy.maxQrCardExportSize(ProEntitlement.Pro))
        assertEquals(
            listOf(512, 900, 1200, 2048, 3072, 4096),
            QrPremiumPolicy.allowedQrCardExportSizes(ProEntitlement.Pro)
        )
    }

    @Test
    fun freeExportChoicesRemainBounded() {
        assertEquals(listOf(512, 900), QrPremiumPolicy.allowedQrCardExportSizes(ProEntitlement.Free))
    }
}
