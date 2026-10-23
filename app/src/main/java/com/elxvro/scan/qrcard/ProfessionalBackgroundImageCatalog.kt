package com.elxvro.scan.qrcard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.LruCache

/**
 * Loads packaged fixed-background photography for professional cards.
 * FREE presets keep the sector demo backgrounds.
 * PRO presets expose 50 unique generated photo backgrounds stored in one
 * offline sprite asset to keep the application package compact.
 */
object ProfessionalBackgroundImageCatalog {
    private const val FREE_ASSET_DIR = "sector_backgrounds"
    private const val PRO_ASSET_DIR = "premium_backgrounds"
    private const val PRO_ASSET_PARTS = 8
    private const val PRO_COLUMNS = 5
    private const val PRO_ROWS = 10

    private val cache = object : LruCache<String, Bitmap>(32 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int =
            (value.byteCount / 1024).coerceAtLeast(1)
    }

    fun load(context: Context, preset: ProfessionalCardPreset): Bitmap? {
        return if (preset.tier == ProfessionalCardTier.PRO) {
            loadPro(context, preset)
        } else {
            loadFree(context, preset)
        }
    }

    private fun loadPro(context: Context, preset: ProfessionalCardPreset): Bitmap? {
        val index = ProfessionalCardCatalog.pro.indexOfFirst { it.id == preset.id }
        if (index !in 0 until 50) return null
        val key = "pro_$index"
        cache.get(key)?.let { return it }

        val sprite = cache.get("pro_sprite") ?: decodeProSprite(context)?.also {
            cache.put("pro_sprite", it)
        } ?: return null

        val cellWidth = sprite.width / PRO_COLUMNS
        val cellHeight = sprite.height / PRO_ROWS
        if (cellWidth <= 0 || cellHeight <= 0) return null
        val column = index % PRO_COLUMNS
        val row = index / PRO_COLUMNS
        val left = column * cellWidth
        val top = row * cellHeight
        return runCatching {
            Bitmap.createBitmap(sprite, left, top, cellWidth, cellHeight)
        }.getOrNull()?.also { cache.put(key, it) }
    }

    private fun decodeProSprite(context: Context): Bitmap? {
        val encoded = buildString {
            for (part in 1..PRO_ASSET_PARTS) {
                val name = "pro_background_sprite_%02d.b64".format(part)
                val content = runCatching {
                    context.assets.open("$PRO_ASSET_DIR/$name")
                        .bufferedReader(Charsets.US_ASCII)
                        .use { it.readText() }
                }.getOrNull() ?: return null
                append(content)
            }
        }
        val bytes = runCatching { Base64.decode(encoded, Base64.DEFAULT) }.getOrNull() ?: return null
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun loadFree(context: Context, preset: ProfessionalCardPreset): Bitmap? {
        val name = freeAssetNameFor(preset.scene)
        val key = "free_$name"
        cache.get(key)?.let { return it }
        return decodeAsset(context, "$FREE_ASSET_DIR/$name")?.also { cache.put(key, it) }
    }

    private fun decodeAsset(context: Context, path: String): Bitmap? {
        val encoded = runCatching {
            context.assets.open(path)
                .bufferedReader(Charsets.US_ASCII)
                .use { it.readText() }
        }.getOrNull() ?: return null
        val bytes = runCatching { Base64.decode(encoded, Base64.DEFAULT) }.getOrNull() ?: return null
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun freeAssetNameFor(scene: ProfessionalCardScene): String = when (scene) {
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
