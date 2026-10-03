package com.elxvro.scan.batch

import org.junit.Assert.assertTrue
import org.junit.Test

class BatchScanExportTest {
    private val entries = listOf(
        BatchScanEntry(
            value = "https://elxvro.com?a=1,2",
            format = "QR Code",
            kind = "QR",
            time = 100L
        ),
        BatchScanEntry(
            value = "5901234123457",
            format = "EAN-13",
            kind = "Barkod",
            time = 200L
        )
    )

    @Test
    fun csvEscapesValuesAndContainsBatchFields() {
        val csv = BatchScanExport.toCsv(entries)

        assertTrue(csv.startsWith("id,value,format,kind,time\n"))
        assertTrue(csv.contains("\"https://elxvro.com?a=1,2\""))
        assertTrue(csv.contains("EAN-13"))
    }

    @Test
    fun jsonContainsEveryEntry() {
        val json = BatchScanExport.toJson(entries)

        assertTrue(json.startsWith("["))
        assertTrue(json.contains("5901234123457"))
        assertTrue(json.contains("\"kind\":\"QR\""))
        assertTrue(json.endsWith("]"))
    }
}
