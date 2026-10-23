package com.elxvro.scan.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elxvro.scan.QrCodeUtil
import com.elxvro.scan.QrImageActions
import com.elxvro.scan.billing.ProEntitlement
import com.elxvro.scan.qrnew.QrV2Aspect
import com.elxvro.scan.qrnew.QrV2Element
import com.elxvro.scan.qrnew.QrV2Font
import com.elxvro.scan.qrnew.QrV2Layout
import com.elxvro.scan.qrnew.QrV2Renderer
import com.elxvro.scan.qrnew.QrV2State
import com.elxvro.scan.ui.components.ProBadge
import com.elxvro.scan.ui.components.ReferenceChip
import com.elxvro.scan.ui.components.ReferenceHeader
import com.elxvro.scan.ui.components.ReferencePrimaryButton
import com.elxvro.scan.ui.theme.ScanTokens
import kotlin.math.roundToInt

private enum class QrV2Tab(val label: String) {
    BACKGROUND("Arka Plan"),
    TEXT("Yazılar"),
    LAYOUT("Düzen")
}

@Composable
fun QrCardEditorScreen(
    entitlement: ProEntitlement,
    initialPayload: String,
    onOpenPaywall: () -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isPro = entitlement is ProEntitlement.Pro
    var tab by remember { mutableStateOf(QrV2Tab.BACKGROUND) }
    var payload by remember { mutableStateOf(initialPayload.ifBlank { "https://elxvro.com" }) }
    var background by remember { mutableStateOf<Bitmap?>(null) }
    var card by remember { mutableStateOf(QrV2State()) }
    var selected by remember { mutableStateOf(QrV2Element.QR) }
    var exportWidth by remember { mutableStateOf(2048) }

    fun toast(text: String) = Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

    val backgroundPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null && isPro) {
            runCatching { decodeQrV2Background(context, uri) }
                .onSuccess { background = it }
                .onFailure { toast("Arka plan açılamadı") }
        }
    }

    fun elementScale(): Float = when (selected) {
        QrV2Element.BRAND -> card.brandScale
        QrV2Element.TITLE -> card.titleScale
        QrV2Element.BODY -> card.bodyScale
        QrV2Element.QR -> card.qrScale
        QrV2Element.CTA -> card.ctaScale
    }

    fun setElementScale(value: Float) {
        card = when (selected) {
            QrV2Element.BRAND -> card.copy(brandScale = value)
            QrV2Element.TITLE -> card.copy(titleScale = value)
            QrV2Element.BODY -> card.copy(bodyScale = value)
            QrV2Element.QR -> card.copy(qrScale = value)
            QrV2Element.CTA -> card.copy(ctaScale = value)
        }
    }

    fun moveElement(element: QrV2Element, dx: Float, dy: Float) {
        fun x(value: Float) = (value + dx).coerceIn(0.06f, 0.94f)
        fun y(value: Float) = (value + dy).coerceIn(0.06f, 0.94f)
        card = when (element) {
            QrV2Element.BRAND -> card.copy(brandX = x(card.brandX), brandY = y(card.brandY))
            QrV2Element.TITLE -> card.copy(titleX = x(card.titleX), titleY = y(card.titleY))
            QrV2Element.BODY -> card.copy(bodyX = x(card.bodyX), bodyY = y(card.bodyY))
            QrV2Element.QR -> card.copy(qrX = x(card.qrX), qrY = y(card.qrY))
            QrV2Element.CTA -> card.copy(ctaX = x(card.ctaX), ctaY = y(card.ctaY))
        }
    }

    fun render(width: Int): Bitmap {
        val qr = QrCodeUtil.create(
            text = payload.ifBlank { "https://elxvro.com" },
            size = 1200,
            margin = 4,
            foreground = AndroidColor.BLACK,
            background = AndroidColor.WHITE,
            highErrorCorrection = true
        )
        return QrV2Renderer.renderPro(qr, background, card, width)
    }

    val preview = remember(payload, background, card) {
        runCatching { render(1000) }.getOrNull()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(ScanTokens.Ink)
    ) {
        ReferenceHeader(title = "PRO QR Tasarım", onBack = onBack) { ProBadge() }

        if (!isPro) {
            Column(
                modifier = Modifier.fillMaxSize().background(ScanTokens.Paper).padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Bu düzenleyici PRO özelliğidir.", color = ScanTokens.Text, fontWeight = FontWeight.SemiBold)
                ReferencePrimaryButton(
                    text = "PRO'yu Aç",
                    onClick = onOpenPaywall,
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp)
                )
            }
            return
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScanTokens.Paper, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            QrV2EditablePreview(
                preview = preview,
                state = card,
                selected = selected,
                onSelect = { selected = it },
                onMove = ::moveElement,
                modifier = Modifier.fillMaxWidth().heightIn(max = 360.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                QrV2Tab.entries.forEach { item ->
                    ReferenceChip(item.label, selected = tab == item) { tab = item }
                }
            }

            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(bottom = 8.dp)
            ) {
                when (tab) {
                    QrV2Tab.BACKGROUND -> {
                        SectionLabelV2("Kart oranı")
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            QrV2Aspect.entries.forEach { aspect ->
                                ReferenceChip(aspect.label, selected = card.aspect == aspect) {
                                    card = card.copy(aspect = aspect)
                                }
                            }
                        }

                        ReferencePrimaryButton(
                            text = if (background == null) "Arka Plan Görseli Seç" else "Arka Planı Değiştir",
                            onClick = { backgroundPicker.launch("image/*") },
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                        )
                        if (background != null) {
                            Text(
                                "Arka planı kaldır",
                                color = ScanTokens.Blue,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp).clickable { background = null }
                            )
                        }
                        Text(
                            "Görsel karartılmaz veya bulanıklaştırılmaz; seçtiğin fotoğraf doğrudan kullanılır.",
                            color = ScanTokens.Muted,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 10.dp)
                        )

                        SectionLabelV2("Çıktı çözünürlüğü")
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            listOf(1200, 2048, 3072, 4096).forEach { px ->
                                ReferenceChip(px.toString() + "px", selected = exportWidth == px) { exportWidth = px }
                            }
                        }
                    }

                    QrV2Tab.TEXT -> {
                        QrV2TextField("QR içeriği", payload) { payload = it }
                        QrV2TextField("Marka", card.brand) { card = card.copy(brand = it) }
                        QrV2TextField("Başlık", card.title) { card = card.copy(title = it) }
                        QrV2TextField("Açıklama", card.body) { card = card.copy(body = it) }
                        QrV2TextField("Buton", card.cta) { card = card.copy(cta = it) }

                        SectionLabelV2("Font")
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            QrV2Font.entries.forEach { font ->
                                ReferenceChip(font.label, selected = card.font == font) {
                                    card = card.copy(font = font)
                                }
                            }
                        }

                        SectionLabelV2("Yazı rengi")
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf(
                                AndroidColor.WHITE,
                                AndroidColor.BLACK,
                                0xFF168CFF.toInt(),
                                0xFFFFC857.toInt()
                            ).forEach { color ->
                                Box(
                                    modifier = Modifier.size(34.dp).background(Color(color), CircleShape)
                                        .clickable { card = card.copy(textColor = color) }
                                )
                            }
                        }
                    }

                    QrV2Tab.LAYOUT -> {
                        Text(
                            "Önizlemede bir öğeye dokunup doğrudan sürükleyebilirsin.",
                            color = ScanTokens.Muted,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        SectionLabelV2("Seçili öğe")
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            QrV2Element.entries.forEach { element ->
                                ReferenceChip(element.label, selected = selected == element) { selected = element }
                            }
                        }

                        Text(
                            "Boyut • " + (elementScale() * 100).roundToInt().toString() + "%",
                            color = ScanTokens.Muted,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 14.dp)
                        )
                        Slider(
                            value = elementScale(),
                            onValueChange = ::setElementScale,
                            valueRange = 0.70f..1.70f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QrV2EditorAction(Icons.Outlined.Download, "Kaydet", Modifier.weight(1f)) {
                    runCatching { QrImageActions.save(context, render(exportWidth)) }
                        .onSuccess { toast(it) }
                        .onFailure { toast("Kart kaydedilemedi") }
                }
                QrV2EditorAction(Icons.Outlined.Share, "Paylaş", Modifier.weight(1f)) {
                    runCatching { QrImageActions.share(context, render(exportWidth)) }
                        .onFailure { toast("Kart paylaşılamadı") }
                }
            }
        }
    }
}

@Composable
private fun SectionLabelV2(text: String) {
    Text(
        text,
        color = ScanTokens.Muted,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
    )
}

@Composable
private fun QrV2EditablePreview(
    preview: Bitmap?,
    state: QrV2State,
    selected: QrV2Element,
    onSelect: (QrV2Element) -> Unit,
    onMove: (QrV2Element, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.aspectRatio(state.aspect.ratio).background(Color.Black, RoundedCornerShape(14.dp)).padding(3.dp)
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        val layout = remember(widthPx, heightPx, state) {
            if (widthPx > 1f && heightPx > 1f) {
                QrV2Renderer.layout(widthPx.roundToInt(), heightPx.roundToInt(), state)
            } else null
        }

        Box(Modifier.fillMaxSize()) {
            preview?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "QR kart önizlemesi",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }

            if (layout != null) {
                val rects = layout.asElementRects()
                Canvas(
                    modifier = Modifier.fillMaxSize().pointerInput(layout, selected) {
                        var active = selected
                        detectDragGestures(
                            onDragStart = { start ->
                                active = rects.asReversed().firstOrNull { (_, rect) ->
                                    start.x >= rect.left && start.x <= rect.right &&
                                        start.y >= rect.top && start.y <= rect.bottom
                                }?.first ?: selected
                                onSelect(active)
                            }
                        ) { change, amount ->
                            change.consume()
                            val dx = if (size.width == 0) 0f else amount.x / size.width.toFloat()
                            val dy = if (size.height == 0) 0f else amount.y / size.height.toFloat()
                            onMove(active, dx, dy)
                        }
                    }
                ) {
                    val selectedRect = rects.firstOrNull { it.first == selected }?.second
                    if (selectedRect != null) {
                        if (selected == QrV2Element.QR) {
                            drawRect(
                                color = ScanTokens.Blue,
                                topLeft = Offset(selectedRect.left, selectedRect.top),
                                size = androidx.compose.ui.geometry.Size(selectedRect.width(), selectedRect.height()),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        } else {
                            drawCircle(
                                color = ScanTokens.Blue,
                                radius = 4.dp.toPx(),
                                center = Offset(selectedRect.left, selectedRect.top)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun QrV2Layout.asElementRects(): List<Pair<QrV2Element, android.graphics.RectF>> =
    listOf(
        QrV2Element.BRAND to brand,
        QrV2Element.TITLE to title,
        QrV2Element.BODY to body,
        QrV2Element.QR to qr,
        QrV2Element.CTA to cta
    )

@Composable
private fun QrV2TextField(label: String, value: String, onChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 7.dp)) {
        Text(label, color = ScanTokens.Muted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 3.dp))
        TextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F3F6),
                unfocusedContainerColor = Color(0xFFF1F3F6),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = ScanTokens.Blue
            )
        )
    }
}

@Composable
private fun QrV2EditorAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.height(48.dp).background(Color.White, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = ScanTokens.Blue, modifier = Modifier.size(20.dp))
        Text(label, color = ScanTokens.Text, modifier = Modifier.padding(start = 7.dp))
    }
}

private fun decodeQrV2Background(context: Context, uri: Uri): Bitmap {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    var sample = 1
    val longest = maxOf(bounds.outWidth, bounds.outHeight)
    while (longest / sample > 4096) sample *= 2
    val options = BitmapFactory.Options().apply {
        inSampleSize = sample.coerceAtLeast(1)
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
    return context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, options)
    } ?: error("Görsel okunamadı")
}
