package com.elxvro.scan.qrcard

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import com.elxvro.scan.QrCodeUtil
import kotlin.math.min

object QrCardRenderer {
    fun render(
        model: QrCardModel,
        width: Int = 1200,
        height: Int = 1600,
        customLogo: Bitmap? = null,
        showElxvroMark: Boolean = true
    ): Bitmap {
        require(QrCardValidator.validate(model).isValid) { QrCardValidator.validate(model).reason.orEmpty() }
        val safeWidth = width.coerceIn(720, 2048)
        val safeHeight = height.coerceIn(960, 2732)
        val bitmap = Bitmap.createBitmap(safeWidth, safeHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val background = when (model.template) {
            QrCardTemplate.MINIMAL -> Color.rgb(247, 248, 249)
            QrCardTemplate.CORPORATE -> Color.rgb(9, 14, 21)
            QrCardTemplate.WIFI -> Color.rgb(5, 24, 48)
            QrCardTemplate.SOCIAL -> Color.rgb(31, 12, 72)
            QrCardTemplate.EVENT -> Color.rgb(20, 10, 45)
        }
        canvas.drawColor(if (model.backgroundColor == Color.WHITE && model.template != QrCardTemplate.MINIMAL) background else model.backgroundColor)

        paint.color = model.accentColor
        canvas.drawRoundRect(RectF(0f, 0f, safeWidth.toFloat(), safeHeight * 0.055f), 0f, 0f, paint)

        val darkText = model.template == QrCardTemplate.MINIMAL && model.backgroundColor == Color.WHITE
        val primaryText = if (darkText) Color.rgb(17, 24, 32) else Color.WHITE
        val secondaryText = if (darkText) Color.rgb(90, 102, 116) else Color.argb(210, 240, 245, 250)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        paint.color = primaryText
        paint.textSize = safeWidth * 0.065f
        canvas.drawText(model.title.ifBlank { "ELXVRO" }, safeWidth / 2f, safeHeight * 0.13f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = secondaryText
        paint.textSize = safeWidth * 0.032f
        canvas.drawText(model.subtitle.ifBlank { templateSubtitle(model.template) }, safeWidth / 2f, safeHeight * 0.18f, paint)

        val qrSide = (min(safeWidth, safeHeight) * 0.58f).toInt()
        val qr = QrCodeUtil.create(
            text = model.payload,
            size = qrSide,
            margin = 3,
            foreground = Color.rgb(8, 18, 32),
            background = Color.WHITE,
            centerLabel = null
        )
        val qrLeft = (safeWidth - qrSide) / 2f
        val qrTop = when (model.qrPosition) {
            QrPosition.TOP -> safeHeight * 0.23f
            QrPosition.CENTER -> safeHeight * 0.29f
            QrPosition.BOTTOM -> safeHeight * 0.36f
        }
        paint.color = Color.WHITE
        val pad = safeWidth * 0.03f
        canvas.drawRoundRect(RectF(qrLeft - pad, qrTop - pad, qrLeft + qrSide + pad, qrTop + qrSide + pad), safeWidth * 0.035f, safeWidth * 0.035f, paint)
        canvas.drawBitmap(qr, qrLeft, qrTop, paint)

        val logo = customLogo
        if (logo != null) {
            val logoSide = qrSide * model.logoScale
            paint.color = Color.WHITE
            val cx = safeWidth / 2f
            val cy = qrTop + qrSide / 2f
            val bgPad = logoSide * 0.16f
            canvas.drawRoundRect(RectF(cx - logoSide / 2 - bgPad, cy - logoSide / 2 - bgPad, cx + logoSide / 2 + bgPad, cy + logoSide / 2 + bgPad), logoSide * 0.18f, logoSide * 0.18f, paint)
            canvas.drawBitmap(logo, null, RectF(cx - logoSide / 2, cy - logoSide / 2, cx + logoSide / 2, cy + logoSide / 2), paint)
        } else if (showElxvroMark) {
            val markSide = qrSide * model.logoScale
            val cx = safeWidth / 2f
            val cy = qrTop + qrSide / 2f
            paint.color = Color.WHITE
            canvas.drawRoundRect(RectF(cx - markSide * 0.64f, cy - markSide * 0.64f, cx + markSide * 0.64f, cy + markSide * 0.64f), markSide * 0.22f, markSide * 0.22f, paint)
            paint.color = Color.rgb(9, 14, 21)
            paint.textSize = markSide * 0.82f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("E", cx, cy - (paint.ascent() + paint.descent()) / 2f, paint)
        }

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textAlign = Paint.Align.CENTER
        paint.color = primaryText
        paint.textSize = safeWidth * 0.031f
        var lineY = qrTop + qrSide + safeHeight * 0.085f
        if (model.detail1.isNotBlank()) {
            canvas.drawText(model.detail1.take(80), safeWidth / 2f, lineY, paint)
            lineY += safeHeight * 0.045f
        }
        if (model.detail2.isNotBlank()) canvas.drawText(model.detail2.take(80), safeWidth / 2f, lineY, paint)

        paint.color = secondaryText
        paint.textSize = safeWidth * 0.025f
        canvas.drawText("ELXVRO Scan • QR Kart", safeWidth / 2f, safeHeight * 0.955f, paint)
        return bitmap
    }

    private fun templateSubtitle(template: QrCardTemplate): String = when (template) {
        QrCardTemplate.MINIMAL -> "Dijital QR Kart"
        QrCardTemplate.CORPORATE -> "Kurumsal Bağlantı"
        QrCardTemplate.WIFI -> "Bağlanmak için okut"
        QrCardTemplate.SOCIAL -> "Tüm hesaplar tek yerde"
        QrCardTemplate.EVENT -> "Etkinlik bilgilerini aç"
    }
}
