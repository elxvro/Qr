package com.elxvro.scan

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Settings

object SmartActionExecutor {
    fun execute(context: Context, action: SmartAction) {
        when (action.type) {
            SmartActionType.OPEN_URL -> context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(action.value)).newTask(context))
            SmartActionType.DIAL -> context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(action.value)).newTask(context))
            SmartActionType.EMAIL -> context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(action.value)).newTask(context))
            SmartActionType.SMS -> context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(action.value)).newTask(context))
            SmartActionType.MAP -> context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(action.value)).newTask(context))
            SmartActionType.WIFI -> context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).newTask(context))
            SmartActionType.CONTACT -> insertContact(context, action.value)
            SmartActionType.SEARCH_PRODUCT -> {
                val uri = Uri.parse("https://www.google.com/search?q=${Uri.encode(action.value)}")
                context.startActivity(Intent(Intent.ACTION_VIEW, uri).newTask(context))
            }
            SmartActionType.SHARE_TEXT -> share(context, action.value)
        }
    }

    fun copy(context: Context, value: String) {
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.setPrimaryClip(ClipData.newPlainText("ELXVRO Scan", value))
    }

    fun share(context: Context, value: String) {
        val share = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, value)
        }
        context.startActivity(Intent.createChooser(share, "Paylaş").newTask(context))
    }

    private fun insertContact(context: Context, raw: String) {
        val name = contactField(raw, "FN") ?: contactField(raw, "N")
        val phone = contactField(raw, "TEL")
        val email = contactField(raw, "EMAIL")
        if (name.isNullOrBlank() && phone.isNullOrBlank() && email.isNullOrBlank()) {
            share(context, raw)
            return
        }
        val intent = Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI).apply {
            name?.takeIf { it.isNotBlank() }?.let { putExtra(ContactsContract.Intents.Insert.NAME, it) }
            phone?.takeIf { it.isNotBlank() }?.let { putExtra(ContactsContract.Intents.Insert.PHONE, it) }
            email?.takeIf { it.isNotBlank() }?.let { putExtra(ContactsContract.Intents.Insert.EMAIL, it) }
        }
        context.startActivity(intent.newTask(context))
    }

    private fun contactField(raw: String, key: String): String? {
        raw.lineSequence().firstOrNull { it.startsWith("$key:", true) }
            ?.substringAfter(':')?.trim()?.takeIf { it.isNotBlank() }?.let { return it }
        val pattern = Regex("(?:^|;)${Regex.escape(key)}:([^;]+)", RegexOption.IGNORE_CASE)
        return pattern.find(raw)?.groupValues?.getOrNull(1)?.trim()?.takeIf { it.isNotBlank() }
    }

    private fun Intent.newTask(context: Context): Intent = apply {
        if (context !is android.app.Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}
