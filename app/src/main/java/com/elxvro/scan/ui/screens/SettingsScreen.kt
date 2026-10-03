package com.elxvro.scan.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elxvro.scan.AppPrefs
import com.elxvro.scan.HistoryExport
import com.elxvro.scan.ScanSettings
import com.elxvro.scan.ScanStore
import com.elxvro.scan.billing.ProEntitlement
import com.elxvro.scan.pro.ProSettingsPresentation
import com.elxvro.scan.ui.components.DividerLine
import com.elxvro.scan.ui.components.ProBadge
import com.elxvro.scan.ui.components.ReferenceHeader
import com.elxvro.scan.ui.theme.ScanTokens

@Composable
fun SettingsScreen(
    store: ScanStore,
    prefs: AppPrefs,
    entitlement: ProEntitlement,
    onUpgradePro: () -> Unit,
    onManageSubscription: () -> Unit,
    onRestorePurchases: () -> Unit,
    onRetryBilling: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var settings by remember { mutableStateOf(prefs.snapshot()) }
    var clearConfirm by remember { mutableStateOf(false) }
    var privacyOpen by remember { mutableStateOf(false) }
    var supportOpen by remember { mutableStateOf(false) }
    var aboutOpen by remember { mutableStateOf(false) }
    val proState = ProSettingsPresentation.from(entitlement)

    fun update(block: (ScanSettings) -> ScanSettings) {
        settings = block(settings)
    }

    Column(Modifier.fillMaxSize().background(ScanTokens.Ink)) {
        ReferenceHeader(title = "Ayarlar", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScanTokens.Paper, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsGroup("ELXVRO Scan PRO") {
                ProStatusRow(proState.statusLabel, entitlement is ProEntitlement.Pro)
                if (proState.showUpgrade) {
                    DividerLine()
                    LinkSettingRow(
                        icon = Icons.Outlined.WorkspacePremium,
                        title = "PRO'ya Geç",
                        subtitle = "Kendi logo, QR Kart ve yüksek çözünürlük",
                        iconColor = ScanTokens.Warning,
                        onClick = onUpgradePro
                    )
                }
                if (proState.showManage) {
                    DividerLine()
                    LinkSettingRow(
                        icon = Icons.Outlined.ManageAccounts,
                        title = "Aboneliği Yönet",
                        subtitle = "Google Play abonelik ayarlarını aç",
                        onClick = onManageSubscription
                    )
                }
                if (proState.showRestore) {
                    DividerLine()
                    LinkSettingRow(
                        icon = Icons.Outlined.Restore,
                        title = "Satın Alımları Geri Yükle",
                        subtitle = "Google Play hesabındaki PRO erişimini yenile",
                        onClick = onRestorePurchases
                    )
                }
                if (proState.showRetry) {
                    DividerLine()
                    LinkSettingRow(
                        icon = Icons.Outlined.Refresh,
                        title = "Google Play'i Yeniden Dene",
                        subtitle = "Abonelik durumunu tekrar kontrol et",
                        onClick = onRetryBilling
                    )
                }
            }

            SettingsGroup("Tarama") {
                SwitchSettingRow(
                    icon = Icons.Outlined.NotificationsActive,
                    title = "Tarama Sesi",
                    subtitle = "Kod bulunduğunda kısa ses çal",
                    checked = settings.sound,
                    onChecked = {
                        prefs.setSound(it)
                        update { s -> s.copy(sound = it) }
                    }
                )
                DividerLine()
                SwitchSettingRow(
                    icon = Icons.Outlined.Vibration,
                    title = "Titreşim",
                    subtitle = "Başarılı taramada kısa titreşim",
                    checked = settings.vibrate,
                    onChecked = {
                        prefs.setVibrate(it)
                        update { s -> s.copy(vibrate = it) }
                    }
                )
                DividerLine()
                SwitchSettingRow(
                    icon = Icons.Outlined.FlashlightOn,
                    title = "Fener Varsayılanı",
                    subtitle = "Tarama açıldığında flaşı etkinleştir",
                    checked = settings.defaultTorch,
                    onChecked = {
                        prefs.setDefaultTorch(it)
                        update { s -> s.copy(defaultTorch = it) }
                    }
                )
                DividerLine()
                SwitchSettingRow(
                    icon = Icons.Outlined.Speed,
                    title = "Hızlı Açılış",
                    subtitle = "Uygulamayı doğrudan tarama ekranında aç",
                    checked = settings.quickStart,
                    onChecked = {
                        prefs.setQuickStart(it)
                        update { s -> s.copy(quickStart = it) }
                    }
                )
            }

            SettingsGroup("Otomasyon") {
                SwitchSettingRow(
                    icon = Icons.Outlined.ContentCopy,
                    title = "Otomatik Kopyala",
                    subtitle = "Taranan içeriği panoya kopyala",
                    checked = settings.autoCopy,
                    onChecked = {
                        prefs.setAutoCopy(it)
                        update { s -> s.copy(autoCopy = it) }
                    }
                )
                DividerLine()
                SwitchSettingRow(
                    icon = Icons.Outlined.OpenInNew,
                    title = "Güvenli Hızlı Aç",
                    subtitle = "Yalnız HTTP/HTTPS bağlantıları otomatik aç",
                    checked = settings.safeAutoOpen,
                    onChecked = {
                        prefs.setSafeAutoOpen(it)
                        update { s -> s.copy(safeAutoOpen = it) }
                    }
                )
                DividerLine()
                DelaySettingRow(
                    value = settings.duplicateDelayMs,
                    onValue = {
                        prefs.setDuplicateDelay(it)
                        update { s -> s.copy(duplicateDelayMs = it) }
                    }
                )
            }

            SettingsGroup("Veri ve Gizlilik") {
                LinkSettingRow(
                    icon = Icons.Outlined.FileUpload,
                    title = "Geçmişi Dışa Aktar",
                    subtitle = "CSV olarak paylaş",
                    onClick = { shareText(context, "ELXVRO Scan Geçmiş.csv", HistoryExport.toCsv(store.list())) }
                )
                DividerLine()
                LinkSettingRow(
                    icon = Icons.Outlined.DeleteSweep,
                    title = "Geçmişi Temizle",
                    subtitle = "Tüm taramalar ve favoriler",
                    iconColor = ScanTokens.Danger,
                    onClick = { clearConfirm = true }
                )
                DividerLine()
                LinkSettingRow(
                    icon = Icons.Outlined.PrivacyTip,
                    title = "Gizlilik ve Veri Kullanımı",
                    subtitle = "Yerel işleme ve saklama bilgisi",
                    onClick = { privacyOpen = true }
                )
            }

            SettingsGroup("Uygulama") {
                LinkSettingRow(
                    icon = Icons.Outlined.QrCode2,
                    title = "Desteklenen İçerikler",
                    subtitle = "QR, barkod ve akıllı sonuç türleri",
                    onClick = { supportOpen = true }
                )
                DividerLine()
                LinkSettingRow(
                    icon = Icons.Outlined.Info,
                    title = "ELXVRO Scan",
                    subtitle = "Sürüm 2.1.0",
                    onClick = { aboutOpen = true }
                )
            }

            Text(
                "Kamera görüntüleri ve geçmiş sunucuya yüklenmez. Tarama ve QR üretimi cihaz üzerinde yapılır.",
                color = ScanTokens.Muted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)
            )
            Spacer(Modifier.height(10.dp))
        }
    }

    if (clearConfirm) {
        InfoDialog(
            title = "Geçmişi temizle",
            text = "Tüm tarama geçmişi ve favoriler kalıcı olarak silinsin mi?",
            confirm = "Temizle",
            confirmColor = ScanTokens.Danger,
            onConfirm = {
                store.clear()
                clearConfirm = false
                Toast.makeText(context, "Geçmiş temizlendi", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { clearConfirm = false }
        )
    }
    if (privacyOpen) {
        InfoDialog(
            title = "Gizlilik ve Veri Kullanımı",
            text = "ELXVRO Scan kamera karelerini ve tarama geçmişini bir sunucuya göndermez. Geçmiş ve favoriler uygulamanın cihazdaki yerel verisinde tutulur. QR oluşturma da cihaz üzerinde yapılır. Web, harita, e-posta veya ürün arama gibi işlemler yalnız siz seçtiğinizde ilgili harici uygulamayı açar.",
            onDismiss = { privacyOpen = false }
        )
    }
    if (supportOpen) {
        InfoDialog(
            title = "Desteklenen İçerikler",
            text = "QR Kod • EAN-13 • EAN-8 • UPC-A • UPC-E • Code 39 • Code 93 • Code 128 • ITF • Codabar • PDF417 • Data Matrix • Aztec\n\nAkıllı sonuçlar: Web sitesi, telefon, e-posta, SMS, Wi-Fi, konum, kişi kartı, takvim ve ürün barkodu.",
            onDismiss = { supportOpen = false }
        )
    }
    if (aboutOpen) {
        InfoDialog(
            title = "ELXVRO Scan 2.1.0",
            text = "Hızlı, yerel ve kompakt QR / barkod tarayıcı. PRO ile özel logo, yüksek çözünürlük ve tasarımlı QR Kart özellikleri eklenmiştir.",
            onDismiss = { aboutOpen = false }
        )
    }
}

@Composable
private fun ProStatusRow(label: String, isPro: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(Icons.Outlined.WorkspacePremium, if (isPro) ScanTokens.Warning else ScanTokens.Blue)
        Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
            Text("Plan", color = ScanTokens.Text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(label, color = if (isPro) ScanTokens.Success else ScanTokens.Muted, style = MaterialTheme.typography.bodySmall)
        }
        if (isPro) ProBadge()
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            title,
            color = ScanTokens.Muted,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(ScanTokens.RadiusCard))
                .border(1.dp, ScanTokens.Divider, RoundedCornerShape(ScanTokens.RadiusCard))
        ) { content() }
    }
}

@Composable
private fun SwitchSettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(icon, ScanTokens.Blue)
        Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
            Text(title, color = ScanTokens.Text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, color = ScanTokens.Muted, style = MaterialTheme.typography.bodySmall)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChecked,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ScanTokens.Blue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = ScanTokens.Divider,
                uncheckedBorderColor = ScanTokens.Divider
            )
        )
    }
}

@Composable
private fun DelaySettingRow(value: Long, onValue: (Long) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(Icons.Outlined.AutoAwesome, ScanTokens.Blue)
        Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
            Text("Tekrar Tarama Bekleme", color = ScanTokens.Text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text("Aynı kodun peş peşe eklenmesini engeller", color = ScanTokens.Muted, style = MaterialTheme.typography.bodySmall)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            listOf(1000L, 1500L, 2500L).forEach { delay ->
                Box(
                    modifier = Modifier
                        .background(if (value == delay) ScanTokens.Blue else ScanTokens.Paper, RoundedCornerShape(9.dp))
                        .border(1.dp, if (value == delay) ScanTokens.Blue else ScanTokens.Divider, RoundedCornerShape(9.dp))
                        .clickable { onValue(delay) }
                        .padding(horizontal = 7.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (delay % 1000L == 0L) "${delay / 1000}s" else "1.5s",
                        color = if (value == delay) Color.White else ScanTokens.Text,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun LinkSettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = ScanTokens.Blue,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(icon, iconColor)
        Column(Modifier.weight(1f).padding(start = 10.dp)) {
            Text(title, color = ScanTokens.Text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, color = ScanTokens.Muted, style = MaterialTheme.typography.bodySmall)
        }
        Text("›", color = ScanTokens.Muted, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun SettingIcon(icon: ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .background(color.copy(alpha = 0.10f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(19.dp))
    }
}

@Composable
private fun InfoDialog(
    title: String,
    text: String,
    confirm: String = "Tamam",
    confirmColor: Color = ScanTokens.Blue,
    onConfirm: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ScanTokens.Card,
        shape = RoundedCornerShape(18.dp),
        title = { Text(title, color = ScanTokens.Text) },
        text = { Text(text, color = ScanTokens.Muted, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            TextButton(onClick = { onConfirm?.invoke() ?: onDismiss() }) {
                Text(confirm, color = confirmColor)
            }
        },
        dismissButton = if (onConfirm != null) {
            { TextButton(onClick = onDismiss) { Text("Vazgeç", color = ScanTokens.Muted) } }
        } else null
    )
}

private fun shareText(context: Context, title: String, data: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, data)
    }
    context.startActivity(Intent.createChooser(intent, title))
}
