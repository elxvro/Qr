package com.elxvro.scan.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elxvro.scan.QrCodeUtil
import com.elxvro.scan.billing.ProEntitlement
import com.elxvro.scan.pro.QrGenerationPolicy
import com.elxvro.scan.pro.QrPremiumPolicy
import com.elxvro.scan.qrcard.CardColorPickerPolicy
import com.elxvro.scan.qrcard.ElxvroBrandLogo
import com.elxvro.scan.qrcard.ProfessionalCardCatalog
import com.elxvro.scan.qrcard.ProfessionalCardPreset
import com.elxvro.scan.qrcard.LogoMode
import com.elxvro.scan.qrcard.QrCardAspectPreset
import com.elxvro.scan.qrcard.QrCardBackgroundMode
import com.elxvro.scan.qrcard.QrCardDesignCatalog
import com.elxvro.scan.qrcard.QrCardDesignPreset
import com.elxvro.scan.qrcard.QrCardExport
import com.elxvro.scan.qrcard.QrCardImageSamplePolicy
import com.elxvro.scan.qrcard.QrCardModel
import com.elxvro.scan.qrcard.QrCardPreviewPolicy
import com.elxvro.scan.qrcard.QrCardRenderer
import com.elxvro.scan.qrcard.QrCardTemplate
import com.elxvro.scan.qrcard.QrCardTextColorPolicy
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

private enum class CardColorTarget(val label: String) {
    BACKGROUND("Arka plan"),
    ACCENT("Buton / vurgu"),
    BRAND("Marka"),
    TITLE("Başlık"),
    BODY("Açıklama"),
    CTA_TEXT("Buton yazısı"),
    QR("QR"),
    QR_BACKGROUND("QR zemini")
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
    var brandText by remember { mutableStateOf("ELXVRO") }
    var title by remember { mutableStateOf("Bizi Keşfet") }
    var descriptionText by remember { mutableStateOf("Kartını tara ve keşfet") }
    var ctaText by remember { mutableStateOf("TARA") }
    var subtitle by remember { mutableStateOf("") }
    var contactLine by remember { mutableStateOf("") }
    var payload by remember { mutableStateOf(initialPayload.ifBlank { "https://elxvro.com" }) }
    var wifiSsid by remember { mutableStateOf("ELXVRO Wi-Fi") }
    var socialHandle by remember { mutableStateOf("@elxvro") }
    var eventDate by remember { mutableStateOf("Etkinlik tarihi") }
    var eventLocation by remember { mutableStateOf("Etkinlik konumu") }
    var logoMode by remember { mutableStateOf(LogoMode.NONE) }
    var customLogo by remember { mutableStateOf<Bitmap?>(null) }
    var heroImage by remember { mutableStateOf<Bitmap?>(null) }
    var logoScale by remember { mutableFloatStateOf(0.18f) }
    var exportSize by remember { mutableIntStateOf(1200) }
    var backgroundMode by remember { mutableStateOf(QrCardBackgroundMode.FIXED_BACKGROUND) }
    var backgroundPresetId by remember { mutableStateOf(ProfessionalCardCatalog.defaultPro.id) }
    var aspectPreset by remember { mutableStateOf(QrCardAspectPreset.CARD) }
    var designPreset by remember { mutableStateOf(QrCardDesignPreset.CLASSIC_EXECUTIVE) }
    var qrPosition by remember { mutableStateOf(QrPosition.CENTER) }
    var cardBackground by remember { mutableIntStateOf(QrCardDesignPreset.CLASSIC_EXECUTIVE.theme().backgroundArgb) }
    var accent by remember { mutableIntStateOf(QrCardDesignPreset.CLASSIC_EXECUTIVE.theme().accentArgb) }
    var brandTextColor by remember { mutableIntStateOf(QrCardDesignPreset.CLASSIC_EXECUTIVE.theme().accentArgb) }
    var textColor by remember { mutableIntStateOf(QrCardDesignPreset.CLASSIC_EXECUTIVE.theme().titleArgb) }
    var bodyTextColor by remember { mutableIntStateOf(QrCardDesignPreset.CLASSIC_EXECUTIVE.theme().bodyArgb) }
    var ctaTextColor by remember {
        mutableIntStateOf(QrCardTextColorPolicy.readableOn(QrCardDesignPreset.CLASSIC_EXECUTIVE.theme().accentArgb))
    }
    var qrForeground by remember { mutableIntStateOf(QrCardDesignPreset.CLASSIC_EXECUTIVE.theme().qrForegroundArgb) }
    var qrBackground by remember { mutableIntStateOf(QrCardDesignPreset.CLASSIC_EXECUTIVE.theme().qrBackgroundArgb) }
    var colorTarget by remember { mutableStateOf(CardColorTarget.BACKGROUND) }

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

    val heroPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            runCatching {
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream, null, bounds)
                }
                val options = BitmapFactory.Options().apply {
                    inSampleSize = QrCardImageSamplePolicy.inSampleSize(bounds.outWidth, bounds.outHeight)
                }
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream, null, options)
                } ?: error("Görsel okunamadı")
            }.onSuccess {
                heroImage = it
            }.onFailure {
                toast("Kart görseli açılamadı")
            }
        }
    }

    fun applyTemplate(next: QrCardTemplate) {
        template = next
        val defaults = next.defaults()
        qrPosition = defaults.qrPosition
        logoScale = defaults.logoScaleFraction
    }

    fun applyDesign(next: QrCardDesignPreset) {
        designPreset = next
        aspectPreset = next.aspectPreset
        backgroundMode = next.backgroundMode
        val theme = next.theme()
        cardBackground = theme.backgroundArgb
        accent = theme.accentArgb
        brandTextColor = theme.accentArgb
        textColor = theme.titleArgb
        bodyTextColor = theme.bodyArgb
        ctaTextColor = QrCardTextColorPolicy.readableOn(theme.accentArgb)
        qrForeground = theme.qrForegroundArgb
        qrBackground = theme.qrBackgroundArgb
    }

    fun currentModel(): QrCardModel = QrCardModel(
        template = QrCardTemplate.MINIMAL,
        designPreset = designPreset,
        backgroundPresetId = backgroundPresetId,
        payload = payload.trim(),
        brandText = brandText,
        title = title,
        descriptionText = descriptionText,
        ctaText = ctaText,
        cardBackgroundArgb = cardBackground,
        accentArgb = accent,
        textArgb = textColor,
        brandTextArgb = brandTextColor,
        bodyTextArgb = bodyTextColor,
        ctaTextArgb = ctaTextColor,
        qrForegroundArgb = qrForeground,
        qrBackgroundArgb = qrBackground,
        logoMode = logoMode,
        logoScaleFraction = logoScale,
        quietZoneModules = 4,
        qrPosition = qrPosition,
        cardAspectRatio = aspectPreset.resolve(QrCardTemplate.MINIMAL)
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
        return QrCardRenderer.render(
            model = model,
            qrBitmap = composedQr,
            logoBitmap = null,
            heroBitmap = heroImage,
            outputWidth = size
        )
    }

    val model = currentModel()
    val previewModel = remember(model) { QrCardPreviewPolicy.sanitize(model) }
    val preview = remember(previewModel, customLogo, heroImage, entitlement) {
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
                            .fillMaxWidth(
                                when {
                                    previewModel.cardAspectRatio < 0.65f -> 0.62f
                                    previewModel.cardAspectRatio < 0.90f -> 0.82f
                                    else -> 1f
                                }
                            )
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
                    SectionTitle("Kart tipi")
                    BackgroundModeSelector(
                        selected = backgroundMode,
                        enabled = isPro,
                        onLocked = onOpenPaywall
                    ) { selectedMode ->
                        backgroundMode = selectedMode
                        if (
                            selectedMode == QrCardBackgroundMode.FIXED_BACKGROUND &&
                            ProfessionalCardCatalog.pro.none { it.id == backgroundPresetId }
                        ) {
                            backgroundPresetId = ProfessionalCardCatalog.defaultPro.id
                        }
                        applyDesign(QrCardDesignCatalog.defaultFor(aspectPreset, selectedMode))
                    }

                    Text(
                        text = if (backgroundMode == QrCardBackgroundMode.FIXED_BACKGROUND) {
                            "Hazır görsel kart kütüphanesi kullanılır. Arka plan; sahne, kompozisyon ve dekoruyla birlikte tasarlanmıştır; sen metinleri ve logoyu değiştirirsin."
                        } else {
                            "Seçtiğin görsel kartın tamamını kaplar. Aynı sade kart düzeni tüm oranlarda korunur."
                        },
                        color = ScanTokens.Muted,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    )

                    SectionTitle("Kart oranı")
                    AspectRatioSelector(
                        selected = aspectPreset,
                        enabled = isPro,
                        onLocked = onOpenPaywall
                    ) { selectedAspect ->
                        aspectPreset = selectedAspect
                        applyDesign(QrCardDesignCatalog.defaultFor(selectedAspect, backgroundMode))
                    }

                    if (backgroundMode == QrCardBackgroundMode.FIXED_BACKGROUND) {
                        SectionTitle("PRO profesyonel kartlar • 50 tasarım")
                        ProfessionalCardPresetSelector(
                            selectedId = backgroundPresetId,
                            enabled = isPro,
                            onLocked = onOpenPaywall,
                            previewKey = previewModel.copy(backgroundPresetId = ""),
                            previewFor = { preset ->
                                runCatching {
                                    renderCard(
                                        320,
                                        previewModel.copy(backgroundPresetId = preset.id)
                                    )
                                }.getOrNull()
                            },
                            onSelect = { preset ->
                                backgroundPresetId = preset.id
                            }
                        )
                    }

                    if (backgroundMode == QrCardBackgroundMode.FULL_BACKGROUND) {
                        SectionTitle("Tam arka plan görseli")
                        ReferencePrimaryButton(
                            text = if (heroImage == null) "Arka Plan Görseli Seç" else "Arka Planı Değiştir",
                            onClick = {
                                if (isPro) heroPicker.launch("image/*") else onOpenPaywall()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (heroImage != null) {
                            Text(
                                text = "Arka planı kaldır",
                                color = ScanTokens.Blue,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clickable { heroImage = null }
                            )
                        }
                    }

                    Text(
                        text = "Kart görünümü ortaktır; marka, yazılar, renkler ve görseller İçerik/Stil sekmelerinden tamamen değiştirilebilir.",
                        color = ScanTokens.Muted,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                    )
                }
                QrCardEditorTab.CONTENT -> {
                    EditorField("QR içeriği", payload, isPro, onOpenPaywall) { payload = it }
                    EditorField("Marka / isim", brandText, isPro, onOpenPaywall) { brandText = it }
                    EditorField("Başlık", title, isPro, onOpenPaywall) { title = it }
                    EditorField("Açıklama", descriptionText, isPro, onOpenPaywall) { descriptionText = it }
                    EditorField("Buton metni", ctaText, isPro, onOpenPaywall) { ctaText = it }
                    Text(
                        text = "Marka / isim alanını boş bırakırsan kartta marka yazısı gösterilmez.",
                        color = ScanTokens.Muted,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
                QrCardEditorTab.STYLE -> {
                    if (backgroundMode == QrCardBackgroundMode.FIXED_BACKGROUND) {
                        Text(
                            text = "Sabit PRO kartlarda tasarımın kendi premium renkleri korunur. Sen yalnız metinleri ve logoyu değiştirirsin.",
                            color = ScanTokens.Muted,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .border(1.dp, ScanTokens.Divider, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        )
                    } else {
                        SectionTitle("Rengini değiştireceğin bölüm")
                        CardColorTargetSelector(
                            selected = colorTarget,
                            onSelect = { colorTarget = it }
                        )
                        val selectedColor = when (colorTarget) {
                            CardColorTarget.BACKGROUND -> cardBackground
                            CardColorTarget.ACCENT -> accent
                            CardColorTarget.BRAND -> brandTextColor
                            CardColorTarget.TITLE -> textColor
                            CardColorTarget.BODY -> bodyTextColor
                            CardColorTarget.CTA_TEXT -> ctaTextColor
                            CardColorTarget.QR -> qrForeground
                            CardColorTarget.QR_BACKGROUND -> qrBackground
                        }
                        SectionTitle("${colorTarget.label} rengi")
                        ContinuousColorPicker(
                            selectedArgb = selectedColor,
                            enabled = isPro,
                            onLocked = onOpenPaywall
                        ) { color ->
                            when (colorTarget) {
                                CardColorTarget.BACKGROUND -> cardBackground = color
                                CardColorTarget.ACCENT -> accent = color
                                CardColorTarget.BRAND -> brandTextColor = color
                                CardColorTarget.TITLE -> textColor = color
                                CardColorTarget.BODY -> bodyTextColor = color
                                CardColorTarget.CTA_TEXT -> ctaTextColor = color
                                CardColorTarget.QR -> qrForeground = color
                                CardColorTarget.QR_BACKGROUND -> qrBackground = color
                            }
                        }
                    }
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
                        sizes = QrPremiumPolicy.allowedQrCardExportSizes(entitlement),
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
                                val safeSize = exportSize.coerceIn(512, QrPremiumPolicy.maxQrCardExportSize(entitlement))
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
                                QrCardExport.share(context, renderCard(exportSize.coerceIn(512, QrPremiumPolicy.maxQrCardExportSize(entitlement)), validation.model))
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
private fun CardColorTargetSelector(
    selected: CardColorTarget,
    onSelect: (CardColorTarget) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        CardColorTarget.entries.forEach { target ->
            Box(
                modifier = Modifier
                    .height(38.dp)
                    .background(
                        if (selected == target) ScanTokens.Blue.copy(alpha = 0.10f) else Color.White,
                        RoundedCornerShape(10.dp)
                    )
                    .border(
                        1.dp,
                        if (selected == target) ScanTokens.Blue else ScanTokens.Divider,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelect(target) }
                    .padding(horizontal = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    target.label,
                    color = if (selected == target) ScanTokens.Blue else ScanTokens.Text,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ContinuousColorPicker(
    selectedArgb: Int,
    enabled: Boolean,
    onLocked: () -> Unit,
    onSelect: (Int) -> Unit
) {
    val hsv = CardColorPickerPolicy.fromArgb(selectedArgb)
    var hue by remember(selectedArgb) { mutableFloatStateOf(hsv.hue) }
    var saturation by remember(selectedArgb) { mutableFloatStateOf(hsv.saturation) }
    var value by remember(selectedArgb) { mutableFloatStateOf(hsv.value) }

    fun emit(nextHue: Float = hue, nextSaturation: Float = saturation, nextValue: Float = value) {
        hue = nextHue
        saturation = nextSaturation
        value = nextValue
        onSelect(CardColorPickerPolicy.toArgb(hue, saturation, value))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, ScanTokens.Divider, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(Color(selectedArgb), CircleShape)
                    .border(1.dp, ScanTokens.Divider, CircleShape)
            )
            Text(
                "#%08X".format(selectedArgb),
                color = ScanTokens.Text,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        val hueColor = Color(CardColorPickerPolicy.toArgb(hue, 1f, 1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.horizontalGradient(listOf(Color.White, hueColor)),
                    RoundedCornerShape(10.dp)
                )
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black)),
                    RoundedCornerShape(10.dp)
                )
                .border(1.dp, ScanTokens.Divider, RoundedCornerShape(10.dp))
                .pointerInput(enabled, hue) {
                    detectTapGestures { offset ->
                        if (!enabled) {
                            onLocked()
                        } else {
                            val s = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                            val v = (1f - offset.y / size.height.toFloat()).coerceIn(0f, 1f)
                            emit(nextSaturation = s, nextValue = v)
                        }
                    }
                }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Red,
                            Color.Yellow,
                            Color.Green,
                            Color.Cyan,
                            Color.Blue,
                            Color.Magenta,
                            Color.Red
                        )
                    ),
                    RoundedCornerShape(99.dp)
                )
                .border(1.dp, ScanTokens.Divider, RoundedCornerShape(99.dp))
                .pointerInput(enabled) {
                    detectTapGestures { offset ->
                        if (!enabled) {
                            onLocked()
                        } else {
                            val h = (offset.x / size.width.toFloat()).coerceIn(0f, 1f) * 360f
                            emit(nextHue = h)
                        }
                    }
                }
        )

        Text(
            "Renk kartına dokunarak doygunluk/parlaklığı, alt şeritten tonu seç.",
            color = ScanTokens.Muted,
            style = MaterialTheme.typography.bodySmall
        )
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
private fun ProfessionalCardPresetSelector(
    selectedId: String,
    enabled: Boolean,
    onLocked: () -> Unit,
    previewKey: Any,
    previewFor: (ProfessionalCardPreset) -> Bitmap?,
    onSelect: (ProfessionalCardPreset) -> Unit
) {
    val categories = ProfessionalCardCatalog.categories(com.elxvro.scan.qrcard.ProfessionalCardTier.PRO)
    val selectedPreset = ProfessionalCardCatalog.pro.firstOrNull { it.id == selectedId }
    var category by remember(selectedPreset?.category) {
        mutableStateOf(selectedPreset?.category ?: categories.first())
    }
    val categoryCards = ProfessionalCardCatalog.byCategory(
        com.elxvro.scan.qrcard.ProfessionalCardTier.PRO,
        category
    )
    val globalIndex = ProfessionalCardCatalog.pro.indexOfFirst { it.id == selectedId }

    Text(
        text = if (globalIndex >= 0) {
            "Seçili: ${globalIndex + 1}/50 • ${ProfessionalCardCatalog.pro[globalIndex].label}"
        } else {
            "50 PRO kart"
        },
        color = ScanTokens.Text,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth().padding(bottom = 7.dp)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        categories.forEach { item ->
            ReferenceChip(
                text = item,
                selected = category == item,
                onClick = { category = item }
            )
        }
    }

    Text(
        text = "$category • 5 tasarım",
        color = ScanTokens.Muted,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 6.dp)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(166.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = categoryCards,
            key = { it.id }
        ) { preset ->
            val selected = preset.id == selectedId
            val thumbnail = remember(preset.id, previewKey) {
                previewFor(preset)
            }
            Column(
                modifier = Modifier
                    .width(174.dp)
                    .clickable {
                        if (enabled) onSelect(preset) else onLocked()
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(118.dp)
                        .background(Color(0xFFF2F4F7), RoundedCornerShape(13.dp))
                        .border(
                            if (selected) 2.dp else 1.dp,
                            if (selected) ScanTokens.Blue else ScanTokens.Divider,
                            RoundedCornerShape(13.dp)
                        )
                        .padding(5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    thumbnail?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = preset.label,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } ?: Text(
                        "QR",
                        color = ScanTokens.Muted,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Text(
                    text = preset.label,
                    color = if (selected) ScanTokens.Blue else ScanTokens.Text,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun BackgroundModeSelector(
    selected: QrCardBackgroundMode,
    enabled: Boolean,
    onLocked: () -> Unit,
    onSelect: (QrCardBackgroundMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QrCardBackgroundMode.entries.forEach { mode ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .background(
                        if (selected == mode) ScanTokens.Blue.copy(alpha = 0.10f) else Color.White,
                        RoundedCornerShape(10.dp)
                    )
                    .border(
                        1.dp,
                        if (selected == mode) ScanTokens.Blue else ScanTokens.Divider,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable {
                        if (enabled) onSelect(mode) else onLocked()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.label,
                    color = if (selected == mode) ScanTokens.Blue else ScanTokens.Text,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
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
        listOf(
            QrCardAspectPreset.SQUARE,
            QrCardAspectPreset.WIDE,
            QrCardAspectPreset.CARD,
            QrCardAspectPreset.PORTRAIT,
            QrCardAspectPreset.STORY
        ).forEach { preset ->
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
