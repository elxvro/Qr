package com.elxvro.scan

object HistoryExport {
    fun toCsv(items: List<ScanItem>): String = buildString {
        append("id,value,format,kind,time,favorite\n")
        items.forEach { item ->
            append(csv(item.id)).append(',')
            append(csv(item.value)).append(',')
            append(csv(item.format)).append(',')
            append(csv(item.kind)).append(',')
            append(item.time).append(',')
            append(item.favorite).append('\n')
        }
    }

    fun toJson(items: List<ScanItem>): String = items.joinToString(prefix = "[", postfix = "]", separator = ",") { item ->
        buildString {
            append("{\"id\":\"").append(json(item.id)).append("\",")
            append("\"value\":\"").append(json(item.value)).append("\",")
            append("\"format\":\"").append(json(item.format)).append("\",")
            append("\"kind\":\"").append(json(item.kind)).append("\",")
            append("\"time\":").append(item.time).append(',')
            append("\"favorite\":").append(item.favorite)
            append('}')
        }
    }

    private fun csv(value: String): String {
        val needsQuotes = value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        if (!needsQuotes) return value
        return "\"${value.replace("\"", "\"\"")}\""
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
