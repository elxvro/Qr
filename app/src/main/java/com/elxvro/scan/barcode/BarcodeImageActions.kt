package com.elxvro.scan.barcode

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BarcodeImageActions {
    fun share(context: Context, bitmap: Bitmap) {
        val dir = File(context.cacheDir, "shared").apply { mkdirs() }
        val file = File(dir, "elxvro-barcode.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }, "Barkodu paylaş"))
    }

    fun save(context: Context, bitmap: Bitmap): String {
        val stamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        val name = "ELXVRO-BARCODE-" + stamp + ".png"
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ELXVRO Scan")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: error("Görsel oluşturulamadı")
            resolver.openOutputStream(uri)?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
                ?: error("Görsel yazılamadı")
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            "Galeriye kaydedildi"
        } else {
            val base = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir
            val dir = File(base, "ELXVRO Scan").apply { mkdirs() }
            val file = File(dir, name)
            FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            "Kaydedildi: " + file.absolutePath
        }
    }
}
