package com.elxvro.scan.qrcard

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.elxvro.scan.R

object ElxvroBrandLogo {
    fun bitmap(context: Context, size: Int = 256): Bitmap {
        val safeSize = size.coerceIn(64, 1024)
        val drawable = requireNotNull(ContextCompat.getDrawable(context, R.drawable.ic_launcher)) {
            "ELXVRO logo drawable missing"
        }
        return drawable.toBitmap(safeSize, safeSize, Bitmap.Config.ARGB_8888)
    }
}
