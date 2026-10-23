package com.elxvro.scan.ui.screens

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.elxvro.scan.QrCodeUtil
import com.elxvro.scan.QrImageActions
import com.elxvro.scan.QrPayloadBuilder
import com.elxvro.scan.WifiSecurity
import com.elxvro.scan.billing.ProEntitlement
import com.elxvro.scan.qrnew.QrV2Renderer
import com.elxvro.scan.ui.components.ProBadge
import com.elxvro.scan.ui.components.ReferenceChip
import com.elxvro.scan.ui.components.ReferenceHeader
import com.elxvro.scan.ui.components.ReferencePrimaryButton
import com.elxvro.scan.ui.theme.ScanTokens

private val qrCreateTypes = listOf(
    "URL", "Metin", "Wi-Fi", "Kişi", "Telefon", "E-posta", "SMS", "Konum", "Takvim"
)

@Composable
fun CreateScreen(
    entitlement: ProEntitlement,
    onOpenPaywall: () -> Unit,
    onOpenQrCard: (String) -> Unit,
    onOpenBarcodeCreate: () -> Unit,
    onAdEligibleAction: () -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isPro = entitlement is ProEntitlement.Pro
    var type by remember { mutableStateOf("URL") }
    var primary by remember { mutableStateOf("") }
    var secondary by remember { mutableStateOf("") }
    var wifiSecurity by remember { mutableStateOf(WifiSecurity.WPA) }
    var generatedPayload by remember { mutableStateOf("") }
    var generatedCard by remember { mutableStateOf<Bitmap?>(null) }

    fun toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    fun resetOutput() {
        generatedPayload = ""
        generatedCard = null
    }

    fun buildPayload(): String {
        return when (type) {
            "Wi-Fi" -> QrPayloadBuilder.buildWifi(primary, secondary, wifiSecurity)
            else -> QrPayloadBuilder.build(type, primary, secondary)
        }
    }

    fun buildCard() {
        if (primary.trim().isBlank()) {
            toast("QR içeriği boş bırakılamaz")
            return
        }
        val payload = buildPayload()
        runCatching {
            val qr = QrCodeUtil.create(
                text = payload,
                size = 1000,
                margin = 4,
                foreground = AndroidColor.BLACK,
                background = AndroidColor.WHITE,
                highErrorCorrection = true
            )
            QrV2Renderer.renderFree(qr, 1080)
        }.onSuccess {
            generatedPayload = payload
            generatedCard = it
        }.onFailure {
            toast("QR oluşturulamadı")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScanTokens.Ink)
    ) {
        ReferenceHeader(title = "QR Oluştur", onBack = onBack) {
            if (isPro) ProBadge()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            qrCreateTypes.forEach { item ->
                ReferenceChip(item, selected = type == item) {
                    type = item
                    primary = ""
                    secondary = ""
                    resetOutput()
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    ScanTokens.Paper,
                    RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            QrV2Input(
                label = primaryLabelV2(type),
                value = primary,
                password = false,
                onChange = {
                    primary = it
                    resetOutput()
                }
            )

            if (needsSecondaryV2(type) && !(type == "Wi-Fi" && wifiSecurity == WifiSecurity.OPEN)) {
                Spacer(Modifier.height(10.dp))
                QrV2Input(
                    label = secondaryLabelV2(type),
                    value = secondary,
                    password = type == "Wi-Fi",
                    onChange = {
                        secondary = it
                        resetOutput()
                    }
                )
            }

            if (type == "Wi-Fi") {
                Text(
                    "Güvenlik",
                    color = ScanTokens.Muted,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 6.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    listOf(
                        "WPA/WPA2" to WifiSecurity.WPA,
                        "WEP" to WifiSecurity.WEP,
                        "Şifresiz" to WifiSecurity.OPEN
                    ).forEach { (label, security) ->
                        ReferenceChip(label, selected = wifiSecurity == security) {
                            wifiSecurity = security
                            if (security == WifiSecurity.OPEN) secondary = ""
                            resetOutput()
                        }
                    }
                }
            }

            Text(
                "Ücretsiz paylaşım kartı",
                color = ScanTokens.Text,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 4.dp)
            )
            Text(
                "Ücretsiz QR; ELXVRO ve www.elxvro.com bulunan sabit kartla kaydedilir ve paylaşılır.",
                color = ScanTokens.Muted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .background(Color(0xFFF0F3F7), RoundedCornerShape(16.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                val preview = generatedCard ?: remember {
                    runCatching {
                        val qr = QrCodeUtil.create(
                            text = "https://elxvro.com",
                            size = 800,
                            margin = 4,
                            foreground = AndroidColor.BLACK,
                            background = AndroidColor.WHITE,
                            highErrorCorrection = true
                        )
                        QrV2Renderer.renderFree(qr, 900)
                    }.getOrNull()
                }
                preview?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "ELXVRO QR kartı",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            ReferencePrimaryButton(
                text = "QR Oluştur",
                onClick = ::buildCard,
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QrV2Action(
                    icon = Icons.Outlined.Download,
                    label = "Kaydet",
                    enabled = generatedCard != null,
                    modifier = Modifier.weight(1f)
                ) {
                    generatedCard?.let { bitmap ->
                        runCatching { QrImageActions.save(context, bitmap) }
                            .onSuccess {
                                toast(it)
                                onAdEligibleAction()
                            }
                            .onFailure { toast("QR kaydedilemedi") }
                    }
                }
                QrV2Action(
                    icon = Icons.Outlined.Share,
                    label = "Paylaş",
                    enabled = generatedCard != null,
                    modifier = Modifier.weight(1f)
                ) {
                    generatedCard?.let { bitmap ->
                        runCatching { QrImageActions.share(context, bitmap) }
                            .onFailure { toast("QR paylaşılamadı") }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .background(Color.White, RoundedCornerShape(14.dp))
                    .clickable {
                        if (isPro) {
                            onOpenQrCard(
                                generatedPayload.ifBlank {
                                    if (primary.isNotBlank()) buildPayload() else "https://elxvro.com"
                                }
                            )
                        } else {
                            onOpenPaywall()
                        }
                    }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Outlined.Tune,
                    contentDescription = null,
                    tint = ScanTokens.Blue,
                    modifier = Modifier.size(28.dp)
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        "PRO QR Tasarım",
                        color = ScanTokens.Text,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Arka plan seç, QR ve yazıları parmağınla taşı, font ve boyutları değiştir.",
                        color = ScanTokens.Muted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (!isPro) ProBadge()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(Color.White, RoundedCornerShape(14.dp))
                    .clickable(onClick = onOpenBarcodeCreate)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Outlined.QrCode2,
                    contentDescription = null,
                    tint = ScanTokens.Blue,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        "Barkod Oluşturucu",
                        color = ScanTokens.Text,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "CODE 128 • EAN-13 • UPC-A",
                        color = ScanTokens.Muted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QrV2Input(
    label: String,
    value: String,
    password: Boolean,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (password) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun QrV2Action(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .background(
                if (enabled) Color.White else Color(0xFFF2F3F5),
                RoundedCornerShape(12.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (enabled) ScanTokens.Blue else ScanTokens.Muted,
            modifier = Modifier.size(20.dp)
        )
        Text(
            label,
            color = if (enabled) ScanTokens.Text else ScanTokens.Muted,
            modifier = Modifier.padding(start = 7.dp)
        )
    }
}

private fun needsSecondaryV2(type: String): Boolean = when (type) {
    "Wi-Fi", "Kişi", "SMS", "Takvim" -> true
    else -> false
}

private fun primaryLabelV2(type: String): String = when (type) {
    "URL" -> "Web adresi"
    "Metin" -> "Metin"
    "Wi-Fi" -> "Wi-Fi adı"
    "Kişi" -> "Ad soyad"
    "Telefon" -> "Telefon"
    "E-posta" -> "E-posta"
    "SMS" -> "Telefon"
    "Konum" -> "Enlem,boylam"
    "Takvim" -> "Etkinlik adı"
    else -> "İçerik"
}

private fun secondaryLabelV2(type: String): String = when (type) {
    "Wi-Fi" -> "Şifre"
    "Kişi" -> "Telefon"
    "SMS" -> "Mesaj"
    "Takvim" -> "Başlangıç"
    else -> "Ek bilgi"
}
