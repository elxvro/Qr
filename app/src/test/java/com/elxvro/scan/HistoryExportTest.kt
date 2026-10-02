package com.elxvro.scan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryExportTest {
    private val items = listOf(
        ScanItem("1", "https://elxvro.com?a=1,2", "QR Code", "QR", 123L, true),
        ScanItem("2", "Merhaba \"Dünya\"", "QR Code", "QR", 456L, false)
    )

    @Test
    fun csv_export_escapes_commas_and_quotes() {
        val csv = HistoryExport.toCsv(items)
        assertTrue(csv.startsWith("id,value,format,kind,time,favorite\n"))
        assertTrue(csv.contains("\"https://elxvro.com?a=1,2\""))
        assertTrue(csv.contains("\"Merhaba \"\"Dünya\"\"\""))
    }

    @Test
    fun json_export_is_deterministic_and_contains_favorite_state() {
        assertEquals(
            "[{\"id\":\"1\",\"value\":\"https://elxvro.com?a=1,2\",\"format\":\"QR Code\",\"kind\":\"QR\",\"time\":123,\"favorite\":true},{\"id\":\"2\",\"value\":\"Merhaba \\\"Dünya\\\"\",\"format\":\"QR Code\",\"kind\":\"QR\",\"time\":456,\"favorite\":false}]",
            HistoryExport.toJson(items)
        )
    }
}
