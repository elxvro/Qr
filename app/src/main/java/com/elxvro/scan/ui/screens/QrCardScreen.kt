package com.elxvro.scan.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elxvro.scan.billing.ProEntitlement
import com.elxvro.scan.pro.LogoMode
import com.elxvro.scan.pro.ProAccessPolicy
import com.elxvro.scan.pro.ProFeature
import com.elxvro.scan.qrcard.QrCardExport
import com.elxvro.scan.qrcard.QrCardModel
import com.elxvro.scan.qrcard.QrCardRenderer
import com.elxvro.scan.qrcard.QrCardTemplate
import com.elxvro.scan.qrcard.QrPosition
import com.elxvro.scan.ui.components.ReferenceChip
import com.elxvro.scan.ui.components.ReferenceHeader
import com.elxvro.scan.ui.components.ReferencePrimaryButton
import com.elxvro.scan.ui.theme.ScanTokens

@Composable
fun QrCardScreen(
    entitlement: ProEntitlement,
    onBack: () -> Unit,
    onRequirePro: () -> Unit
) {
    if (!ProAccessPolicy.canUse(ProFeature.QR_CARD, entitlement)) {
        onRequirePro()
        return
    }

    val context = LocalContext.current
    var template by remember { mutableStateOf(QrCardTemplate.MINIMAL) }
    var title by remember { mutableStateOf("ELXVRO") }
    var subtitle by remember { mutableStateOf("Dijital QR Kart") }
    var payload by remember { mutableStateOf("https://elxvro.com") }
    var detail1 by remember { mutableStateOf("") }
    var detail2 by remember { mutableStateOf("") }
    var position by remember { mutableStateOf(QrPosition.CENTER) }
    var logoMode by remember { mutableStateOf(LogoMode.ELXVRO) }
    var logoScale by remember { mutableFloatStateOf(0.18f) }
    var customLogo by remember { mutableStateOf<Bitmap?>(null) }
    var preview by remember { mutableStateOf<Bitmap?>(null) }
    var accent by remember { mutableStateOf(android.graphics.Color.rgb(0, 104, 248)) }

    fun toast(text: String) = Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    fun model() = QrCardModel(
        payload = payload,
        template = template,
        title = title,
        subtitle = subtitle,
        detail1 = detail1,
        detail2 = detail2,
        accentColor = accent,
        qrPosition = position,
        logoScale = logoScale
    )
    fun render() {
        preview = runCatching {
            QrCardRenderer.render(
                model = model(),
                customLogo = if (logoMode == LogoMode.CUSTOM) customLogo else null,
                showElxvroMark = logoMode == LogoMode.ELXVRO
            )
        }.onFailure { toast(it.message ?: "Kart oluşturulamadı") }.getOrNull()
    }

    val logoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        customLogo = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri)) { decoder, _, _ -> decoder.isMutableRequired = false }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        }.onFailure { toast("Logo okunamadı") }.getOrNull()
        if (customLogo != null) logoMode = LogoMode.CUSTOM
        preview = null
    }

    Column(Modifier.fillMaxSize().background(ScanTokens.Ink)) {
        ReferenceHeader(
            title = "QR Kart Oluştur",
            onBack = onBack,
            trailing = {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 10.dp)) {
                    Icon(Icons.Outlined.WorkspacePremium, null, tint = ScanTokens.Warning, modifier = Modifier.size(17.dp))
                    Text("PRO", color = ScanTokens.Warning, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScanTokens.Paper, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            SectionTitle("Kart Şablonu")
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                QrCardTemplate.entries.forEach { item ->
                    ReferenceChip(templateName(item), template == item) {
                        template = item
                        subtitle = templateSubtitle(item)
                        preview = null
                    }
                }
            }

            CardField(title, { title = it; preview = null }, "Başlık")
            CardField(subtitle, { subtitle = it; preview = null }, "Alt metin")
            CardField(payload, { payload = it; preview = null }, "QR bağlantısı / içeriği")
            CardField(detail1, { detail1 = it; preview = null }, detailLabel1(template))
            CardField(detail2, { detail2 = it; preview = null }, detailLabel2(template))

            SectionTitle("Logo")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf("ELXVRO" to LogoMode.ELXVRO, "Kendi Logom" to LogoMode.CUSTOM, "Logosuz" to LogoMode.NONE).forEach { (label, mode) ->
                    LightChoice(label, logoMode == mode, Modifier.weight(1f)) {
                        if (mode == LogoMode.CUSTOM && customLogo == null) logoPicker.launch("image/*") else logoMode = mode
                        preview = null
                    }
                }
            }
            if (logoMode == LogoMode.CUSTOM) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, ScanTokens.Divider, RoundedCornerShape(12.dp))
                        .clickable { logoPicker.launch("image/*") }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Image, null, tint = ScanTokens.Blue)
                    Text(if (customLogo == null) "Logo seç" else "Logo seçildi • değiştirmek için dokun", color = ScanTokens.Text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f).padding(start = 9.dp))
                    Text("Seç", color = ScanTokens.Blue, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(8.dp))
                }
            }

            SectionTitle("Logo Boyutu • ${(logoScale * 100).toInt()}%")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf(0.14f, 0.18f, 0.22f).forEach { scale ->
                    LightChoice("${(scale * 100).toInt()}%", logoScale == scale, Modifier.weight(1f)) {
                        logoScale = scale
                        preview = null
                    }
                }
            }

            SectionTitle("QR Konumu")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf("Üst" to QrPosition.TOP, "Orta" to QrPosition.CENTER, "Alt" to QrPosition.BOTTOM).forEach { (label, pos) ->
                    LightChoice(label, position == pos, Modifier.weight(1f)) { position = pos; preview = null }
                }
            }

            SectionTitle("Vurgu Rengi")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                listOf(0xFF0068F8, 0xFF6D28D9, 0xFF16A34A, 0xFFEA580C, 0xFFDC2626).forEach { value ->
                    val androidColor = value.toInt()
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(androidColor), RoundedCornerShape(11.dp))
                            .border(if (accent == androidColor) 3.dp else 1.dp, if (accent == androidColor) ScanTokens.Text else ScanTokens.Divider, RoundedCornerShape(11.dp))
                            .clickable { accent = androidColor; preview = null }
                            .padding(2.dp)
                    )
                }
            }

            preview?.let { card ->
                Box(
                    Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(14.dp)).border(1.dp, ScanTokens.Divider, RoundedCornerShape(14.dp)).padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(card.asImageBitmap(), "QR Kart önizleme", modifier = Modifier.fillMaxWidth(0.78f).aspectRatio(0.75f))
                }
            }

            ReferencePrimaryButton("Önizlemeyi Oluştur", ::render, Modifier.fillMaxWidth())
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                CardAction(Icons.Outlined.Download, "Kaydet", preview != null, Modifier.weight(1f)) {
                    preview?.let { runCatching { QrCardExport.save(context, it) }.onSuccess(::toast).onFailure { toast("Kaydedilemedi") } }
                }
                CardAction(Icons.Outlined.Share, "Paylaş", preview != null, Modifier.weight(1f)) {
                    preview?.let { runCatching { QrCardExport.share(context, it) }.onFailure { toast("Paylaşılamadı") } }
                }
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = ScanTokens.Muted, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 3.dp, top = 2.dp))
}

@Composable
private fun CardField(value: String, onValue: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = ScanTokens.Blue,
            unfocusedBorderColor = ScanTokens.Divider,
            focusedTextColor = ScanTokens.Text,
            unfocusedTextColor = ScanTokens.Text,
            cursorColor = ScanTokens.Blue,
            focusedLabelColor = ScanTokens.Blue,
            unfocusedLabelColor = ScanTokens.Muted
        )
    )
}

@Composable
private fun LightChoice(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(42.dp)
            .background(if (selected) ScanTokens.Blue.copy(alpha = 0.10f) else Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, if (selected) ScanTokens.Blue else ScanTokens.Divider, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (selected) ScanTokens.Blue else ScanTokens.Text, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun CardAction(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, enabled: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .height(52.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, ScanTokens.Divider, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, null, tint = if (enabled) ScanTokens.Blue else ScanTokens.Muted, modifier = Modifier.size(19.dp))
        Text(text, color = if (enabled) ScanTokens.Text else ScanTokens.Muted, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 7.dp))
    }
}

private fun templateName(template: QrCardTemplate): String = when (template) {
    QrCardTemplate.MINIMAL -> "Minimal"
    QrCardTemplate.CORPORATE -> "Kurumsal"
    QrCardTemplate.WIFI -> "Wi-Fi"
    QrCardTemplate.SOCIAL -> "Sosyal"
    QrCardTemplate.EVENT -> "Etkinlik"
}

private fun templateSubtitle(template: QrCardTemplate): String = when (template) {
    QrCardTemplate.MINIMAL -> "Dijital QR Kart"
    QrCardTemplate.CORPORATE -> "Kurumsal Çözümler"
    QrCardTemplate.WIFI -> "Ağ bilgilerini paylaş"
    QrCardTemplate.SOCIAL -> "Tüm hesaplar tek yerde"
    QrCardTemplate.EVENT -> "Etkinlik bilgileri"
}

private fun detailLabel1(template: QrCardTemplate): String = when (template) {
    QrCardTemplate.WIFI -> "Ağ adı (SSID)"
    QrCardTemplate.EVENT -> "Tarih"
    QrCardTemplate.SOCIAL -> "Kullanıcı adı"
    else -> "Web sitesi / telefon"
}

private fun detailLabel2(template: QrCardTemplate): String = when (template) {
    QrCardTemplate.WIFI -> "Açıklama"
    QrCardTemplate.EVENT -> "Konum"
    QrCardTemplate.SOCIAL -> "Sosyal hesaplar"
    else -> "E-posta / açıklama"
}
