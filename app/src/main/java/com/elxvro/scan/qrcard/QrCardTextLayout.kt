package com.elxvro.scan.qrcard

object QrCardTextLayout {
    fun wrap(
        text: String,
        maxWidth: Float,
        maxLines: Int,
        measure: (String) -> Float
    ): List<String> {
        require(maxWidth > 0f)
        require(maxLines > 0)

        val normalized = text.trim().replace(Regex("\\s+"), " ")
        if (normalized.isBlank()) return emptyList()
        if (measure(normalized) <= maxWidth) return listOf(normalized)

        val words = normalized.split(" ")
        val lines = mutableListOf<String>()
        var current = ""

        fun pushCurrent() {
            if (current.isNotBlank()) {
                lines += current
                current = ""
            }
        }

        for (word in words) {
            val candidate = if (current.isBlank()) word else "$current $word"
            if (measure(candidate) <= maxWidth) {
                current = candidate
                continue
            }

            pushCurrent()
            if (lines.size == maxLines) break

            if (measure(word) <= maxWidth) {
                current = word
            } else {
                current = fitToWidth(word, maxWidth, measure, ellipsis = false)
            }
        }

        if (lines.size < maxLines) pushCurrent()
        if (lines.isEmpty()) lines += fitToWidth(normalized, maxWidth, measure, ellipsis = true)

        val consumed = lines.joinToString(" ")
        val truncated = consumed.length < normalized.length
        if (truncated) {
            val lastIndex = (lines.size - 1).coerceAtLeast(0)
            lines[lastIndex] = fitToWidth(lines[lastIndex], maxWidth, measure, ellipsis = true)
        }

        return lines.take(maxLines)
    }

    private fun fitToWidth(
        value: String,
        maxWidth: Float,
        measure: (String) -> Float,
        ellipsis: Boolean
    ): String {
        val suffix = if (ellipsis) "…" else ""
        var fitted = value
        while (fitted.length > 1 && measure(fitted + suffix) > maxWidth) {
            fitted = fitted.dropLast(1)
        }
        return (fitted + suffix).ifBlank { suffix }
    }
}
