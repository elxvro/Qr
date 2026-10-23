package com.elxvro.scan.qrcard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

object FreePromoBackgroundCatalog {
    private const val ASSET = "free_promo/elxvro_promo.webp.b64"
    @Volatile private var cached: Bitmap? = null

    fun load(context: Context): Bitmap? {
        cached?.let { return it }
        val encoded = runCatching {
            context.assets.open(ASSET)
                .bufferedReader(Charsets.US_ASCII)
                .use { it.readText() }
        }.getOrNull() ?: return null
        val bytes = runCatching { Base64.decode(encoded, Base64.DEFAULT) }.getOrNull() ?: return null
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.also { cached = it }
    }
}
