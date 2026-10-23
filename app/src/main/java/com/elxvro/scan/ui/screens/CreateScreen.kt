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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import com.elxvro.scan.ui.theme.ScanTokens

private val FreeBg = Color(0xFF06111F)
private val FreePanel = Color(0xFF0B1B2D)
private val FreePanelRaised = Color(0xFF10243A)
private val FreeStroke = Color(0xFF1D3E5F)
private val FreeText = Color(0xFFF5F8FC)
private val FreeMuted = Color(0xFF91A9C0)
private val FreeBlue = Color(0xFF1598FF)
private val FreePurple = Color(0xFF8A2EFF)
private val FreeGreen = Color(0xFF1ED760)

private val qrCreateTypes = listOf(
    "URL", "Metin", "Wi-Fi", "Telefon", "E-posta", "SMS", "Konum", "Kişi", "Takvim"
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
                size = 1100,
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

    val preview = generatedCard ?: remember {
        runCatching {
            val qr = QrCodeUtil.create(
                text = "https://elxvro.com",
                size = 900,
                margin = 4,
                foreground = AndroidColor.BLACK,
                background = AndroidColor.WHITE,
                highErrorCorrection = true
            )
            QrV2Renderer.renderFree(qr, 900)
        }.getOrNull()
    }

    Column(modifier = Modifier.fillMaxSize().background(FreeBg)) {
        ReferenceHeader(title = "Ücretsiz QR", onBack = onBack) {
            if (isPro) ProBadge() else FreeBadge()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("URL", "Metin", "Wi-Fi", "Telefon").forEach { item ->
                    FreeTypeCard(
                        label = item,
                        selected = type == item,
                        icon = typeIcon(item),
                        modifier = Modifier.weight(1f)
                    ) {
                        type = item
                        primary = ""
                        secondary = ""
                        resetOutput()
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                qrCreateTypes.drop(4).forEach { item ->
                    ReferenceChip(item, selected = type == item) {
                        type = item
                        primary = ""
                        secondary = ""
                        resetOutput()
                    }
                }
            }

            FreeInput(
                label = primaryLabelV4(type),
                value = primary,
                password = false,
                onChange = {
                    primary = it
                    resetOutput()
                },
                modifier = Modifier.padding(top = 12.dp)
            )

            if (needsSecondaryV4(type) && !(type == "Wi-Fi" && wifiSecurity == WifiSecurity.OPEN)) {
                FreeInput(
                    label = secondaryLabelV4(type),
                    value = secondary,
                    password = type == "Wi-Fi",
                    onChange = {
                        secondary = it
                        resetOutput()
                    },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (type == "Wi-Fi") {
                Text(
                    "Güvenlik",
                    color = FreeMuted,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 11.dp, bottom = 6.dp)
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
                        FreePill(label, wifiSecurity == security) {
                            wifiSecurity = security
                            if (security == WifiSecurity.OPEN) secondary = ""
                            resetOutput()
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .background(FreePanel, RoundedCornerShape(18.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                preview?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "ELXVRO sabit QR kartı",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(FreePanelRaised, RoundedCornerShape(13.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(FreeBlue.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("i", color = FreeBlue, fontWeight = FontWeight.Bold)
                }
                Text(
                    "Ücretsiz sürümde kart tasarımı sabittir ve ELXVRO tanıtımı içerir.",
                    color = FreeMuted,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            GradientButton(
                text = "QR Oluştur",
                icon = Icons.Outlined.QrCode2,
                colors = listOf(FreeBlue, FreePurple),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                onClick = ::buildCard
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 9.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                DarkAction(
                    icon = Icons.Outlined.Download,
                    text = "Kaydet",
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
                DarkAction(
                    icon = Icons.Outlined.Share,
                    text = "Paylaş",
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
                    .padding(top = 13.dp)
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF30106D), FreePurple, Color(0xFF1A4FB4))),
                        RoundedCornerShape(16.dp)
                    )
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Tune,
                    contentDescription = null,
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(30.dp)
                )
                Column(Modifier.weight(1f).padding(start = 11.dp)) {
                    Text(
                        "PRO QR Tasarım",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Arka plan, yazı, QR konumu, font, boyut ve renkleri özgürce düzenle.",
                        color = Color.White.copy(alpha = 0.82f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (!isPro) ProBadge()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 9.dp)
                    .background(FreePanel, RoundedCornerShape(14.dp))
                    .clickable(onClick = onOpenBarcodeCreate)
                    .padding(13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.QrCode2, contentDescription = null, tint = FreeBlue)
                Column(Modifier.padding(start = 10.dp)) {
                    Text("Barkod Oluşturucu", color = FreeText, fontWeight = FontWeight.SemiBold)
                    Text("CODE 128 • EAN-13 • UPC-A", color = FreeMuted, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FreeBadge() {
    Box(
        modifier = Modifier
            .background(FreeBlue, RoundedCornerShape(20.dp))
            .padding(horizontal = 13.dp, vertical = 6.dp)
    ) {
        Text("FREE", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FreeTypeCard(
    label: String,
    selected: Boolean,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(
                if (selected) Color(0xFF0B65A6) else FreePanel,
                RoundedCornerShape(13.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = if (selected) Color.White else FreeBlue, modifier = Modifier.size(25.dp))
        Text(label, color = FreeText, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 5.dp))
    }
}

@Composable
private fun FreeInput(
    label: String,
    value: String,
    password: Boolean,
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(label, color = FreeMuted, style = MaterialTheme.typography.bodySmall)
        TextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            visualTransformation = if (password) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = FreePanelRaised,
                unfocusedContainerColor = FreePanelRaised,
                focusedTextColor = FreeText,
                unfocusedTextColor = FreeText,
                focusedIndicatorColor = FreeBlue,
                unfocusedIndicatorColor = FreeStroke,
                cursorColor = FreeBlue
            )
        )
    }
}

@Composable
private fun FreePill(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (selected) FreeBlue else FreePanelRaised, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text, color = FreeText, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun GradientButton(
    text: String,
    icon: ImageVector,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(54.dp)
            .background(Brush.horizontalGradient(colors), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White)
        Text(text, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun DarkAction(
    icon: ImageVector,
    text: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(50.dp)
            .background(if (enabled) FreePanelRaised else FreePanel, RoundedCornerShape(13.dp))
            .clickable(enabled = enabled, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = if (enabled) FreeText else FreeMuted)
        Text(
            text,
            color = if (enabled) FreeText else FreeMuted,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 7.dp)
        )
    }
}

private fun typeIcon(type: String): ImageVector = when (type) {
    "URL" -> Icons.Outlined.Link
    "Metin" -> Icons.Outlined.TextFields
    "Wi-Fi" -> Icons.Outlined.Wifi
    "Telefon" -> Icons.Outlined.Phone
    "E-posta" -> Icons.Outlined.Email
    "SMS" -> Icons.Outlined.Message
    "Konum" -> Icons.Outlined.LocationOn
    else -> Icons.Outlined.QrCode2
}

private fun needsSecondaryV4(type: String): Boolean = when (type) {
    "Wi-Fi", "Kişi", "SMS", "Takvim" -> true
    else -> false
}

private fun primaryLabelV4(type: String): String = when (type) {
    "URL" -> "Bağlantı (URL)"
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

private fun secondaryLabelV4(type: String): String = when (type) {
    "Wi-Fi" -> "Şifre"
    "Kişi" -> "Telefon"
    "SMS" -> "Mesaj"
    "Takvim" -> "Başlangıç"
    else -> "Ek bilgi"
}
