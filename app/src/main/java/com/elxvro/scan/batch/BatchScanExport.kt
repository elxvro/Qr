package com.elxvro.scan.batch

object BatchScanExport {
    fun toCsv(entries: List<BatchScanEntry>): String = buildString {
        append("id,value,format,kind,time\n")
        entries.forEach { entry ->
            append(csv(entry.id)).append(',')
            append(csv(entry.value)).append(',')
            append(csv(entry.format)).append(',')
            append(csv(entry.kind)).append(',')
            append(entry.time).append('\n')
        }
    }

    fun toJson(entries: List<BatchScanEntry>): String =
        entries.joinToString(prefix = "[", postfix = "]", separator = ",") { entry ->
            buildString {
                append("{\"id\":\"").append(json(entry.id)).append("\",")
                append("\"value\":\"").append(json(entry.value)).append("\",")
                append("\"format\":\"").append(json(entry.format)).append("\",")
                append("\"kind\":\"").append(json(entry.kind)).append("\",")
                append("\"time\":").append(entry.time)
                append('}')
            }
        }

    private fun csv(value: String): String {
        val needsQuotes = value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        if (!needsQuotes) return value
        return "\"" + value.replace("\"", "\"\"") + "\""
    }

    private fun json(value: String): String = buildString {
        value.forEach { c ->
            when (c) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(c)
            }
        }
    }
}
