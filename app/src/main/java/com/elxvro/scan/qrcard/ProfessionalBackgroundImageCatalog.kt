package com.elxvro.scan.qrcard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.LruCache

/**
 * Loads packaged fixed-background photography for professional cards.
 * FREE presets keep the sector demo backgrounds.
 * PRO presets use 50 unique generated image assets bundled offline.
 */
object ProfessionalBackgroundImageCatalog {
    private const val FREE_ASSET_DIR = "sector_backgrounds"
    private const val PRO_ASSET_DIR = "premium_backgrounds"

    private val cache = object : LruCache<String, Bitmap>(32 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int =
            (value.byteCount / 1024).coerceAtLeast(1)
    }

    fun load(context: Context, preset: ProfessionalCardPreset): Bitmap? {
        val name = assetNameFor(preset) ?: return null
        cache.get(name)?.let { return it }

        val assetDir = if (preset.tier == ProfessionalCardTier.PRO) PRO_ASSET_DIR else FREE_ASSET_DIR
        val encoded = runCatching {
            context.assets.open("$assetDir/$name")
                .bufferedReader(Charsets.US_ASCII)
                .use { it.readText() }
        }.getOrNull() ?: return null

        val bytes = runCatching { Base64.decode(encoded, Base64.DEFAULT) }.getOrNull() ?: return null
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.also { bitmap ->
            cache.put(name, bitmap)
        }
    }

    private fun assetNameFor(preset: ProfessionalCardPreset): String? {
        if (preset.tier == ProfessionalCardTier.PRO) {
            val index = ProfessionalCardCatalog.pro.indexOfFirst { it.id == preset.id }
            if (index >= 0) {
                return "pro_bg_%02d.webp.b64".format(index + 1)
            }
        }
        return when (preset.scene) {
            ProfessionalCardScene.FREE_ALPINE,
            ProfessionalCardScene.PRO_COAST -> "bg_barber_shop.webp.b64"

            ProfessionalCardScene.FREE_COFFEE,
            ProfessionalCardScene.PRO_DINING -> "bg_cafe.webp.b64"

            ProfessionalCardScene.FREE_NEON_TECH,
            ProfessionalCardScene.PRO_ARCHITECTURE -> "bg_restaurant.webp.b64"

            ProfessionalCardScene.FREE_METRO,
            ProfessionalCardScene.PRO_FASHION -> "bg_bakery.webp.b64"

            ProfessionalCardScene.FREE_BOTANICAL,
            ProfessionalCardScene.PRO_NIGHT -> "bg_spa.webp.b64"

            ProfessionalCardScene.FREE_STUDIO,
            ProfessionalCardScene.PRO_WELLNESS -> "bg_boutique.webp.b64"

            ProfessionalCardScene.FREE_STAGE,
            ProfessionalCardScene.PRO_RESORT -> "bg_gym.webp.b64"

            ProfessionalCardScene.FREE_INTERIOR,
            ProfessionalCardScene.PRO_TECH_LAUNCH -> "bg_clinic.webp.b64"

            ProfessionalCardScene.FREE_CREATIVE,
            ProfessionalCardScene.PRO_PROPERTY -> "bg_real_estate.webp.b64"

            ProfessionalCardScene.FREE_FLOW,
            ProfessionalCardScene.PRO_BEAUTY -> "bg_automotive.webp.b64"
        }
    }
}
