package com.elxvro.scan.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elxvro.scan.QrCodeUtil
import com.elxvro.scan.billing.ProEntitlement
import com.elxvro.scan.pro.QrGenerationPolicy
import com.elxvro.scan.pro.QrPremiumPolicy
import com.elxvro.scan.qrcard.ElxvroBrandLogo
import com.elxvro.scan.qrcard.LogoMode
import com.elxvro.scan.qrcard.QrCardAspectPreset
import com.elxvro.scan.qrcard.QrCardExport
import com.elxvro.scan.qrcard.QrCardModel
import com.elxvro.scan.qrcard.QrCardPreviewPolicy
import com.elxvro.scan.qrcard.QrCardRenderer
import com.elxvro.scan.qrcard.QrCardTemplate
import com.elxvro.scan.qrcard.QrCardValidator
import com.elxvro.scan.qrcard.QrLogoComposer
import com.elxvro.scan.qrcard.QrPosition
import com.elxvro.scan.qrcard.ValidationResult
import com.elxvro.scan.ui.components.ProBadge
import com.elxvro.scan.ui.components.ReferenceChip
import com.elxvro.scan.ui.components.ReferenceHeader
import com.elxvro.scan.ui.components.ReferencePrimaryButton
import com.elxvro.scan.ui.theme.ScanTokens

private enum class QrCardEditorTab(val label: String) {
    DESIGN("Tasarım"),
    CONTENT("İçerik"),
    STYLE("Stil"),
    ADVANCED("Gelişmiş")
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
    var tab by remember { mutableStateOf(QrCardEditorTab.DESIGN) }
    var template by remember { mutableStateOf(QrCardTemplate.MINIMAL) }
    var title by remember { mutableStateOf("ELXVRO") }
    var subtitle by remember { mutableStateOf("QR ile hızlı erişim") }
    var contactLine by remember { mutableStateOf("") }
    var payload by remember { mutableStateOf(initialPayload.ifBlank { "https://elxvro.com" }) }
    var wifiSsid by remember { mutableStateOf("ELXVRO Wi-Fi") }
    var socialHandle by remember { mutableStateOf("@elxvro") }
    var eventDate by remember { mutableStateOf("Etkinlik tarihi") }
    var eventLocation by remember { mutableStateOf("Etkinlik konumu") }
    var logoMode by remember { mutableStateOf(LogoMode.ELXVRO) }
    var customLogo by remember { mutableStateOf<Bitmap?>(null) }
    var logoScale by remember { mutableFloatStateOf(0.18f) }
    var exportSize by remember { mutableIntStateOf(1200) }
    var aspectPreset by remember { mutableStateOf(QrCardAspectPreset.DEFAULT) }
    var qrPosition by remember { mutableStateOf(QrPosition.CENTER) }
    var cardBackground by remember { mutableIntStateOf(QrCardTemplate.MINIMAL.defaults().cardBackgroundArgb) }
    var accent by remember { mutableIntStateOf(QrCardTemplate.MINIMAL.defaults().accentArgb) }
    var textColor by remember { mutableIntStateOf(QrCardTemplate.MINIMAL.defaults().textArgb) }
    var qrForeground by remember { mutableIntStateOf(QrCardTemplate.MINIMAL.defaults().qrForegroundArgb) }
    var qrBackground by remember { mutableIntStateOf(QrCardTemplate.MINIMAL.defaults().qrBackgroundArgb) }

    fun toast(text: String) = Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

    val logoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use(BitmapFactory::decodeStream)
                    ?: error("Logo okunamadı")
            }.onSuccess {
                customLogo = it
                logoMode = LogoMode.CUSTOM
            }.onFailure {
                toast("Logo açılamadı")
            }
        }
    }

    fun applyTemplate(next: QrCardTemplate) {
        template = next
        val defaults = next.defaults()
        cardBackground = defaults.cardBackgroundArgb
        accent = defaults.accentArgb
        textColor = defaults.textArgb
        qrForeground = defaults.qrForegroundArgb
        qrBackground = defaults.qrBackgroundArgb
        qrPosition = defaults.qrPosition
        logoScale = defaults.logoScaleFraction
    }

    fun currentModel(): QrCardModel = QrCardModel(
        template = template,
        payload = payload.trim(),
        title = title.trim(),
        subtitle = subtitle.trim(),
        contactLine = contactLine.trim(),
        wifiSsid = wifiSsid.trim(),
        socialHandle = socialHandle.trim(),
        eventDate = eventDate.trim(),
        eventLocation = eventLocation.trim(),
        cardBackgroundArgb = cardBackground,
        accentArgb = accent,
        textArgb = textColor,
        qrForegroundArgb = qrForeground,
        qrBackgroundArgb = qrBackground,
        logoMode = logoMode,
        logoScaleFraction = logoScale,
        quietZoneModules = 4,
        qrPosition = qrPosition,
        cardAspectRatio = aspectPreset.resolve(template)
    )

    fun logoBitmap(mode: LogoMode): Bitmap? = when (mode) {
        LogoMode.ELXVRO -> ElxvroBrandLogo.bitmap(context)
        LogoMode.CUSTOM -> customLogo
        LogoMode.NONE -> null
    }

    fun renderCard(size: Int, model: QrCardModel): Bitmap {
        val options = QrGenerationPolicy.resolve(
            requestedSize = minOf(size, 2048),
            requestedMargin = 4,
            logoMode = model.logoMode,
            entitlement = entitlement
        )
        val rawQr = QrCodeUtil.create(
            text = model.payload,
            size = options.size,
            margin = options.margin,
            foreground = model.qrForegroundArgb,
            background = model.qrBackgroundArgb,
            highErrorCorrection = options.highErrorCorrection
        )
        val logo = logoBitmap(model.logoMode)
        val composedQr = if (logo != null) {
            QrLogoComposer.compose(rawQr, logo, model.logoScaleFraction)
        } else {
            rawQr
        }
        return QrCardRenderer.render(model, composedQr, logoBitmap = null, outputWidth = size)
    }

    val model = currentModel()
    val previewModel = remember(model) { QrCardPreviewPolicy.sanitize(model) }
    val preview = remember(previewModel, customLogo, entitlement) {
        runCatching {
            renderCard(700, previewModel)
        }.getOrNull()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScanTokens.Ink)
    ) {
        ReferenceHeader(title = "QR Kart", onBack = onBack) {
            ProBadge()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            QrCardEditorTab.entries.forEach { item ->
                ReferenceChip(item.label, selected = tab == item) { tab = item }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScanTokens.Paper, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(ScanTokens.RadiusCard))
                    .border(1.dp, ScanTokens.Divider, RoundedCornerShape(ScanTokens.RadiusCard))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                preview?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "QR Kart önizlemesi",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(previewModel.cardAspectRatio)
                    )
                } ?: Text("Önizleme hazırlanamadı", color = ScanTokens.Muted)
            }

            if (!isPro) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .background(ScanTokens.Warning.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .border(1.dp, ScanTokens.Warning.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenPaywall)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Outlined.WorkspacePremium, contentDescription = null, tint = ScanTokens.Warning)
                    Column(Modifier.weight(1f)) {
                        Text("QR Kart PRO özelliğidir", color = ScanTokens.Text, fontWeight = FontWeight.SemiBold)
                        Text("Şablonları inceleyebilirsin. Düzenleme ve dışa aktarma PRO ile açılır.", color = ScanTokens.Muted, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            when (tab) {
                QrCardEditorTab.DESIGN -> {
                    SectionTitle("Kart şablonu")
                    TemplateSelector(template, ::applyTemplate)
                    SectionTitle("QR konumu")
                    LightChoiceRow(
                        items = listOf("Üst" to QrPosition.TOP, "Orta" to QrPosition.CENTER, "Alt" to QrPosition.BOTTOM),
                        selected = qrPosition,
                        enabled = isPro,
                        onLocked = onOpenPaywall
                    ) { qrPosition = it }
                    SectionTitle("Kart oranı")
                    AspectRatioSelector(
                        selected = aspectPreset,
                        enabled = isPro,
                        onLocked = onOpenPaywall
                    ) { aspectPreset = it }
                }
                QrCardEditorTab.CONTENT -> {
                    EditorField("QR içeriği", payload, isPro, onOpenPaywall) { payload = it }
                    EditorField("Başlık", title, isPro, onOpenPaywall) { title = it }
                    EditorField("Alt açıklama", subtitle, isPro, onOpenPaywall) { subtitle = it }
                    when (template) {
                        QrCardTemplate.MINIMAL,
                        QrCardTemplate.CORPORATE,
                        QrCardTemplate.BUSINESS,
                        QrCardTemplate.PROMO -> EditorField("İletişim / kısa bilgi", contactLine, isPro, onOpenPaywall) { contactLine = it }
                        QrCardTemplate.WIFI -> EditorField("Wi-Fi adı (SSID)", wifiSsid, isPro, onOpenPaywall) { wifiSsid = it }
                        QrCardTemplate.SOCIAL -> EditorField("Sosyal medya hesabı", socialHandle, isPro, onOpenPaywall) { socialHandle = it }
                        QrCardTemplate.EVENT, QrCardTemplate.TICKET -> {
                            EditorField("Tarih", eventDate, isPro, onOpenPaywall) { eventDate = it }
                            EditorField("Konum", eventLocation, isPro, onOpenPaywall) { eventLocation = it }
                        }
                    }
                }
                QrCardEditorTab.STYLE -> {
                    SectionTitle("Kart rengi")
                    CardColorSwatches(cardBackground, isPro, onOpenPaywall) { cardBackground = it }
                    SectionTitle("Vurgu rengi")
                    CardColorSwatches(accent, isPro, onOpenPaywall) { accent = it }
                    SectionTitle("QR rengi")
                    CardColorSwatches(qrForeground, isPro, onOpenPaywall) { qrForeground = it }
                }
                QrCardEditorTab.ADVANCED -> {
                    SectionTitle("Logo")
                    LogoModeRow(logoMode, isPro, onOpenPaywall) { logoMode = it }
                    if (isPro && logoMode == LogoMode.CUSTOM) {
                        ReferencePrimaryButton(
                            text = if (customLogo == null) "Logo Seç" else "Logoyu Değiştir",
                            onClick = { logoPicker.launch("image/*") },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                    }
                    SectionTitle("Logo boyutu • ${(logoScale * 100).toInt()}%")
                    Slider(
                        value = logoScale,
                        onValueChange = {
                            if (isPro) logoScale = it else onOpenPaywall()
                        },
                        valueRange = 0.12f..0.20f,
                        enabled = isPro && logoMode != LogoMode.NONE
                    )
                    SectionTitle("Çözünürlük")
                    ResolutionSelector(
                        selected = exportSize,
                        sizes = QrPremiumPolicy.allowedQrExportSizes(entitlement),
                        enabled = isPro,
                        onLocked = onOpenPaywall
                    ) { exportSize = it }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                EditorAction(Icons.Outlined.Download, "Kaydet", Modifier.weight(1f), isPro) {
                    if (!isPro) {
                        onOpenPaywall()
                    } else {
                        when (val validation = QrCardValidator.validate(currentModel())) {
                            is ValidationResult.Invalid -> toast(validationMessage(validation))
                            is ValidationResult.Valid -> runCatching {
                                val safeSize = exportSize.coerceIn(512, QrPremiumPolicy.maxQrExportSize(entitlement))
                                QrCardExport.save(context, renderCard(safeSize, validation.model))
                            }.onSuccess(::toast).onFailure { toast("QR Kart kaydedilemedi") }
                        }
                    }
                }
                EditorAction(Icons.Outlined.Share, "Paylaş", Modifier.weight(1f), isPro) {
                    if (!isPro) {
                        onOpenPaywall()
                    } else {
                        when (val validation = QrCardValidator.validate(currentModel())) {
                            is ValidationResult.Invalid -> toast(validationMessage(validation))
                            is ValidationResult.Valid -> runCatching {
                                QrCardExport.share(context, renderCard(exportSize.coerceIn(512, QrPremiumPolicy.maxQrExportSize(entitlement)), validation.model))
                            }.onFailure { toast("QR Kart paylaşılamadı") }
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun TemplateSelector(selected: QrCardTemplate, onSelect: (QrCardTemplate) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QrCardTemplate.entries.forEach { template ->
            val label = when (template) {
                QrCardTemplate.MINIMAL -> "Minimal"
                QrCardTemplate.CORPORATE -> "Kurumsal"
                QrCardTemplate.WIFI -> "Wi-Fi"
                QrCardTemplate.SOCIAL -> "Sosyal"
                QrCardTemplate.EVENT -> "Etkinlik"
                QrCardTemplate.BUSINESS -> "Business"
                QrCardTemplate.PROMO -> "Promo"
                QrCardTemplate.TICKET -> "Ticket"
            }
            ReferenceChip(label, selected == template) { onSelect(template) }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = ScanTokens.Muted,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, bottom = 7.dp)
    )
}

@Composable
private fun EditorField(
    label: String,
    value: String,
    enabled: Boolean,
    onLocked: () -> Unit,
    onChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            enabled = enabled,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedBorderColor = ScanTokens.Blue,
                unfocusedBorderColor = ScanTokens.Divider,
                disabledBorderColor = ScanTokens.Divider,
                focusedTextColor = ScanTokens.Text,
                unfocusedTextColor = ScanTokens.Text,
                disabledTextColor = ScanTokens.Muted
            )
        )
        if (!enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(onClick = onLocked)
            )
        }
    }
}

@Composable
private fun CardColorSwatches(selected: Int, enabled: Boolean, onLocked: () -> Unit, onSelect: (Int) -> Unit) {
    val colors = listOf(
        Color.White,
        Color(0xFFF7F8F9),
        Color(0xFF090E15),
        Color(0xFF0068F8),
        Color(0xFF7539E8),
        Color(0xFF0D9F55),
        Color(0xFFCF24E8),
        Color(0xFF1F4B99),
        Color(0xFFFF3D7F),
        Color(0xFFFFD166)
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        colors.forEach { color ->
            val argb = color.toArgb()
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .border(if (selected == argb) 2.dp else 1.dp, if (selected == argb) ScanTokens.Blue else ScanTokens.Divider, CircleShape)
                    .padding(4.dp)
                    .background(color, CircleShape)
                    .clickable {
                        if (enabled) onSelect(argb) else onLocked()
                    }
            )
        }
    }
}

@Composable
private fun LogoModeRow(selected: LogoMode, enabled: Boolean, onLocked: () -> Unit, onSelect: (LogoMode) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            "ELXVRO" to LogoMode.ELXVRO,
            "Kendi Logom" to LogoMode.CUSTOM,
            "Logosuz" to LogoMode.NONE
        ).forEach { (label, mode) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .background(if (selected == mode) ScanTokens.Blue.copy(alpha = 0.10f) else Color.White, RoundedCornerShape(10.dp))
                    .border(1.dp, if (selected == mode) ScanTokens.Blue else ScanTokens.Divider, RoundedCornerShape(10.dp))
                    .clickable {
                        if (enabled || mode == LogoMode.ELXVRO) onSelect(mode) else onLocked()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(label, color = if (selected == mode) ScanTokens.Blue else ScanTokens.Text, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ResolutionSelector(
    selected: Int,
    sizes: List<Int>,
    enabled: Boolean,
    onLocked: () -> Unit,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sizes.forEach { size ->
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .background(if (selected == size) ScanTokens.Blue.copy(alpha = 0.10f) else Color.White, RoundedCornerShape(10.dp))
                    .border(1.dp, if (selected == size) ScanTokens.Blue else ScanTokens.Divider, RoundedCornerShape(10.dp))
                    .clickable {
                        if (enabled) onSelect(size) else onLocked()
                    }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("$size px", color = if (selected == size) ScanTokens.Blue else ScanTokens.Text, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun AspectRatioSelector(
    selected: QrCardAspectPreset,
    enabled: Boolean,
    onLocked: () -> Unit,
    onSelect: (QrCardAspectPreset) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QrCardAspectPreset.entries.forEach { preset ->
            ReferenceChip(
                text = preset.label,
                selected = selected == preset,
                onClick = {
                    if (enabled) onSelect(preset) else onLocked()
                }
            )
        }
    }
}

@Composable
private fun <T> LightChoiceRow(
    items: List<Pair<String, T>>,
    selected: T,
    enabled: Boolean,
    onLocked: () -> Unit,
    onSelect: (T) -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { (label, value) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .background(if (selected == value) ScanTokens.Blue.copy(alpha = 0.10f) else Color.White, RoundedCornerShape(10.dp))
                    .border(1.dp, if (selected == value) ScanTokens.Blue else ScanTokens.Divider, RoundedCornerShape(10.dp))
                    .clickable {
                        if (enabled) onSelect(value) else onLocked()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(label, color = if (selected == value) ScanTokens.Blue else ScanTokens.Text, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun EditorAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .height(64.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, ScanTokens.Divider, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = if (enabled) ScanTokens.Blue else ScanTokens.Warning)
        Text(text, color = ScanTokens.Text, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 3.dp))
    }
}

private fun validationMessage(validation: ValidationResult.Invalid): String {
    val first = validation.errors.firstOrNull() ?: return "Kart bilgilerini kontrol et"
    return when (first) {
        com.elxvro.scan.qrcard.ValidationError.PAYLOAD_REQUIRED -> "QR içeriği boş bırakılamaz"
        com.elxvro.scan.qrcard.ValidationError.LOGO_TOO_LARGE -> "Logo boyutu çok büyük"
        com.elxvro.scan.qrcard.ValidationError.LOGO_SIZE_INVALID -> "Logo boyutu geçersiz"
        com.elxvro.scan.qrcard.ValidationError.QUIET_ZONE_TOO_SMALL -> "QR kenar boşluğu yetersiz"
        com.elxvro.scan.qrcard.ValidationError.QR_CONTRAST_TOO_LOW -> "QR renk kontrastı yetersiz"
        com.elxvro.scan.qrcard.ValidationError.CARD_ASPECT_RATIO_INVALID -> "Kart oranı geçersiz"
        com.elxvro.scan.qrcard.ValidationError.TITLE_REQUIRED -> "Başlık gerekli"
        com.elxvro.scan.qrcard.ValidationError.WIFI_SSID_REQUIRED -> "Wi-Fi adı gerekli"
        com.elxvro.scan.qrcard.ValidationError.SOCIAL_HANDLE_REQUIRED -> "Sosyal medya hesabı gerekli"
        com.elxvro.scan.qrcard.ValidationError.EVENT_DATE_REQUIRED -> "Etkinlik tarihi gerekli"
        com.elxvro.scan.qrcard.ValidationError.EVENT_LOCATION_REQUIRED -> "Etkinlik konumu gerekli"
    }
}
