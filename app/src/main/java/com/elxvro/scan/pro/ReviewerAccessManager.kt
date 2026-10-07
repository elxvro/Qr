package com.elxvro.scan.pro

import android.content.Context
import java.security.MessageDigest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReviewerAccessManager(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("elxvro_scan_review_access", Context.MODE_PRIVATE)

    private val _enabled = MutableStateFlow(prefs.getBoolean(KEY_ENABLED, false))
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    fun verifyAndEnable(code: String): Boolean {
        val normalized = code.trim().uppercase()
        val hash = sha256(normalized)
        val valid = hash == REVIEW_CODE_SHA256
        if (valid) {
            prefs.edit().putBoolean(KEY_ENABLED, true).apply()
            _enabled.value = true
        }
        return valid
    }

    private fun sha256(value: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(value.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private companion object {
        const val KEY_ENABLED = "enabled"
        const val REVIEW_CODE_SHA256 =
            "e28a49290343bcbf12faf9e270fab328f7f67321d8c2198780e6faa3e17e060d"
    }
}
