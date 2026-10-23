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
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Tune
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
import com.elxvro.scan.qrnew.QrV2Align
import com.elxvro.scan.qrnew.QrV2Aspect
import com.elxvro.scan.qrnew.QrV2Element
import com.elxvro.scan.qrnew.QrV2Font
import com.elxvro.scan.qrnew.QrV2Layout
import com.elxvro.scan.qrnew.QrV2Renderer
import com.elxvro.scan.qrnew.QrV2State
import com.elxvro.scan.ui.components.ProBadge
import com.elxvro.scan.ui.components.ReferenceHeader
import com.elxvro.scan.ui.components.ReferencePrimaryButton
import com.elxvro.scan.ui.theme.ScanTokens
import kotlin.math.roundToInt

private val EditorBg = Color(0xFF06111F)
private val EditorPanel = Color(0xFF0B1B2D)
private val EditorPanelRaised = Color(0xFF10243A)
private val EditorStroke = Color(0xFF1D3E5F)
private val EditorText = Color(0xFFF5F8FC)
private val EditorMuted = Color(0xFF91A9C0)
private val EditorBlue = Color(0xFF1598FF)
private val EditorPurple = Color(0xFF8A2EFF)
private val EditorGreen = Color(0xFF1ED760)

private enum class QrV3Tab(val label: String) {
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
    var tab by remember { mutableStateOf(QrV3Tab.BACKGROUND) }
    var payload by remember { mutableStateOf(initialPayload.ifBlank { "https://elxvro.com" }) }
    var background by remember { mutableStateOf<Bitmap?>(null) }
    var card by remember {
        mutableStateOf(
            QrV2State(
                brand = "ELXVRO",
                title = "BİZİ ZİYARET EDİN",
                body = "QR kodu tara ve bağlantıyı aç",
                cta = "HEMEN TARA"
            )
        )
    }
    var selected by remember { mutableStateOf(QrV2Element.QR) }
    var exportWidth by remember { mutableStateOf(2048) }

    fun toast(text: String) = Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

    val backgroundPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null && isPro) {
            runCatching { decodeQrV3Background(context, uri) }
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

    fun selectedText(): String = when (selected) {
        QrV2Element.BRAND -> card.brand
        QrV2Element.TITLE -> card.title
        QrV2Element.BODY -> card.body
        QrV2Element.CTA -> card.cta
        QrV2Element.QR -> payload
    }

    fun setSelectedText(value: String) {
        when (selected) {
            QrV2Element.BRAND -> card = card.copy(brand = value)
            QrV2Element.TITLE -> card = card.copy(title = value)
            QrV2Element.BODY -> card = card.copy(body = value)
            QrV2Element.CTA -> card = card.copy(cta = value)
            QrV2Element.QR -> payload = value
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
            size = 1400,
            margin = 4,
            foreground = card.qrColor,
            background = AndroidColor.WHITE,
            highErrorCorrection = true
        )
        return QrV2Renderer.renderPro(qr, background, card, width)
    }

    val preview = remember(payload, background, card) {
        runCatching { render(1100) }.getOrNull()
    }

    Column(modifier = Modifier.fillMaxSize().background(EditorBg)) {
        ReferenceHeader(title = "PRO QR Tasarım", onBack = onBack) { ProBadge() }

        if (!isPro) {
            Column(
                modifier = Modifier.fillMaxSize().background(EditorBg).padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Profesyonel QR düzenleyici PRO özelliğidir.",
                    color = EditorText,
                    fontWeight = FontWeight.SemiBold
                )
                ReferencePrimaryButton(
                    text = "PRO'ya Geç",
                    onClick = onOpenPaywall,
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp)
                )
            }
            return
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(EditorBg)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            QrV3EditablePreview(
                preview = preview,
                state = card,
                selected = selected,
                onSelect = { selected = it },
                onMove = ::moveElement,
                modifier = Modifier.fillMaxWidth().heightIn(max = 390.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 9.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QrV3Tab.entries.forEach { item ->
                    EditorTabButton(
                        text = item.label,
                        selected = tab == item,
                        icon = when (item) {
                            QrV3Tab.BACKGROUND -> Icons.Outlined.Image
                            QrV3Tab.TEXT -> Icons.Outlined.Tune
                            QrV3Tab.LAYOUT -> Icons.Outlined.Tune
                        },
                        modifier = Modifier.weight(1f)
                    ) { tab = item }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 7.dp, bottom = 10.dp)
            ) {
                when (tab) {
                    QrV3Tab.BACKGROUND -> {
                        EditorSectionTitle("Kart Boyutu")
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            QrV2Aspect.entries.forEach { aspect ->
                                EditorChip(aspect.label, card.aspect == aspect) {
                                    card = card.copy(aspect = aspect)
                                }
                            }
                        }

                        EditorSectionTitle("Arka Plan")
                        EditorMainButton(
                            text = if (background == null) "Galeriden Arka Plan Seç" else "Arka Planı Değiştir",
                            onClick = { backgroundPicker.launch("image/*") }
                        )
                        if (background != null) {
                            EditorSecondaryButton("Arka Planı Kaldır") { background = null }
                        }
                        Text(
                            "Görsel doğrudan kullanılır; bulanıklık veya karartma uygulanmaz.",
                            color = EditorMuted,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        EditorSectionTitle("Çıktı Kalitesi")
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            listOf(1200, 2048, 3072, 4096).forEach { px ->
                                EditorChip(px.toString() + "px", exportWidth == px) { exportWidth = px }
                            }
                        }
                    }

                    QrV3Tab.TEXT -> {
                        EditorSectionTitle("Düzenlenecek Yazı")
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            listOf(
                                QrV2Element.BRAND,
                                QrV2Element.TITLE,
                                QrV2Element.BODY,
                                QrV2Element.CTA
                            ).forEach { element ->
                                EditorChip(element.label, selected == element) { selected = element }
                            }
                        }

                        EditorTextField(
                            label = selected.label,
                            value = selectedText(),
                            onChange = ::setSelectedText
                        )

                        EditorSectionTitle("Yazı Tipi")
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            QrV2Font.entries.forEach { font ->
                                EditorChip(font.label, card.font == font) { card = card.copy(font = font) }
                            }
                        }

                        EditorSectionTitle("Stil")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StyleToggle("B", card.bold) { card = card.copy(bold = !card.bold) }
                            StyleToggle("I", card.italic) { card = card.copy(italic = !card.italic) }
                            StyleToggle("U", card.underline) { card = card.copy(underline = !card.underline) }
                        }

                        EditorSectionTitle("Hizalama")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            QrV2Align.entries.forEach { align ->
                                EditorChip(align.label, card.textAlign == align) {
                                    card = card.copy(textAlign = align)
                                }
                            }
                        }

                        EditorSectionTitle("Yazı Rengi")
                        ColorSwatchesV3(
                            selected = card.textColor,
                            colors = listOf(
                                AndroidColor.WHITE,
                                AndroidColor.BLACK,
                                0xFF1598FF.toInt(),
                                0xFFFFC857.toInt(),
                                0xFFFF4D67.toInt(),
                                0xFF9B5CFF.toInt()
                            )
                        ) { card = card.copy(textColor = it) }

                        EditorSectionTitle("Boyut")
                        EditorSlider(
                            value = elementScale(),
                            range = 0.70f..1.70f,
                            valueText = (elementScale() * 100).roundToInt().toString() + "%"
                        ) { setElementScale(it) }
                    }

                    QrV3Tab.LAYOUT -> {
                        Text(
                            "Önizlemede öğeye dokun ve sürükle. Seçtiğin öğenin boyutunu aşağıdan ayarla.",
                            color = EditorMuted,
                            style = MaterialTheme.typography.bodySmall
                        )

                        EditorSectionTitle("Öğe")
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            QrV2Element.entries.forEach { element ->
                                EditorChip(element.label, selected == element) { selected = element }
                            }
                        }

                        EditorSectionTitle("Öğe Boyutu")
                        EditorSlider(
                            value = elementScale(),
                            range = 0.70f..1.70f,
                            valueText = (elementScale() * 100).roundToInt().toString() + "%"
                        ) { setElementScale(it) }

                        if (selected == QrV2Element.QR) {
                            EditorSectionTitle("QR Rengi")
                            ColorSwatchesV3(
                                selected = card.qrColor,
                                colors = listOf(
                                    AndroidColor.BLACK,
                                    0xFF0A2440.toInt(),
                                    0xFF0057D8.toInt(),
                                    0xFF7B1FA2.toInt(),
                                    0xFFD81B60.toInt()
                                )
                            ) { card = card.copy(qrColor = it) }

                            EditorSectionTitle("QR Kenarlık")
                            EditorSlider(
                                value = card.qrBorderScale,
                                range = 0.40f..2.20f,
                                valueText = (card.qrBorderScale * 100).roundToInt().toString() + "%"
                            ) { card = card.copy(qrBorderScale = it) }

                            ColorSwatchesV3(
                                selected = card.qrBorderColor,
                                colors = listOf(
                                    AndroidColor.WHITE,
                                    AndroidColor.BLACK,
                                    0xFF1598FF.toInt(),
                                    0xFFFFC857.toInt()
                                )
                            ) { card = card.copy(qrBorderColor = it) }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                EditorBottomAction(
                    icon = Icons.Outlined.Download,
                    text = "Kaydet",
                    color = EditorGreen,
                    modifier = Modifier.weight(1f)
                ) {
                    runCatching { QrImageActions.save(context, render(exportWidth)) }
                        .onSuccess { toast(it) }
                        .onFailure { toast("Kart kaydedilemedi") }
                }
                EditorBottomAction(
                    icon = Icons.Outlined.Share,
                    text = "Paylaş",
                    color = EditorBlue,
                    modifier = Modifier.weight(1f)
                ) {
                    runCatching { QrImageActions.share(context, render(exportWidth)) }
                        .onFailure { toast("Kart paylaşılamadı") }
                }
            }
        }
    }
}

@Composable
private fun QrV3EditablePreview(
    preview: Bitmap?,
    state: QrV2State,
    selected: QrV2Element,
    onSelect: (QrV2Element) -> Unit,
    onMove: (QrV2Element, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(state.aspect.ratio)
            .background(EditorPanel, RoundedCornerShape(18.dp))
            .padding(4.dp)
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
                    contentDescription = "PRO QR önizleme",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }

            if (layout != null) {
                val rects = layout.asElementRectsV3()
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
                    val rect = rects.firstOrNull { it.first == selected }?.second
                    if (rect != null) {
                        drawRect(
                            color = EditorBlue,
                            topLeft = Offset(rect.left, rect.top),
                            size = androidx.compose.ui.geometry.Size(rect.width(), rect.height()),
                            style = Stroke(width = 2.dp.toPx())
                        )
                        listOf(
                            Offset(rect.left, rect.top),
                            Offset(rect.right, rect.top),
                            Offset(rect.left, rect.bottom),
                            Offset(rect.right, rect.bottom)
                        ).forEach {
                            drawCircle(EditorBlue, radius = 4.dp.toPx(), center = it)
                        }
                    }
                }
            }
        }
    }
}

private fun QrV2Layout.asElementRectsV3(): List<Pair<QrV2Element, android.graphics.RectF>> =
    listOf(
        QrV2Element.BRAND to brand,
        QrV2Element.TITLE to title,
        QrV2Element.BODY to body,
        QrV2Element.QR to qr,
        QrV2Element.CTA to cta
    )

@Composable
private fun EditorTabButton(
    text: String,
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(
                if (selected) EditorBlue else EditorPanel,
                RoundedCornerShape(13.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(21.dp))
        Text(text, color = Color.White, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun EditorSectionTitle(text: String) {
    Text(
        text,
        color = EditorText,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 13.dp, bottom = 7.dp)
    )
}

@Composable
private fun EditorChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (selected) EditorBlue else EditorPanelRaised,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 9.dp)
    ) {
        Text(text, color = EditorText, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun StyleToggle(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(
                if (selected) EditorBlue else EditorPanelRaised,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = EditorText, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EditorTextField(label: String, value: String, onChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
        Text(label, color = EditorMuted, style = MaterialTheme.typography.labelSmall)
        TextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            shape = RoundedCornerShape(11.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = EditorPanelRaised,
                unfocusedContainerColor = EditorPanelRaised,
                focusedTextColor = EditorText,
                unfocusedTextColor = EditorText,
                focusedIndicatorColor = EditorBlue,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = EditorBlue
            )
        )
    }
}

@Composable
private fun EditorMainButton(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(EditorBlue, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 13.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Image, contentDescription = null, tint = Color.White)
        Text(text, color = Color.White, modifier = Modifier.padding(start = 8.dp), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun EditorSecondaryButton(text: String, onClick: () -> Unit) {
    Text(
        text,
        color = EditorBlue,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = 8.dp).clickable(onClick = onClick)
    )
}

@Composable
private fun ColorSwatchesV3(
    selected: Int,
    colors: List<Int>,
    onSelect: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(if (selected == color) 38.dp else 34.dp)
                    .background(Color(color), CircleShape)
                    .clickable { onSelect(color) }
            )
        }
    }
}

@Composable
private fun EditorSlider(
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    valueText: String,
    onChange: (Float) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = range,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .background(EditorPanelRaised, RoundedCornerShape(9.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Text(valueText, color = EditorText, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun EditorBottomAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(52.dp)
            .background(color, RoundedCornerShape(13.dp))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White)
        Text(text, color = Color.White, modifier = Modifier.padding(start = 8.dp), fontWeight = FontWeight.SemiBold)
    }
}

private fun decodeQrV3Background(context: Context, uri: Uri): Bitmap {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, bounds)
    }
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
