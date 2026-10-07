package com.elxvro.scan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elxvro.scan.billing.OneTimePurchaseOffer
import com.elxvro.scan.billing.ProEntitlement
import com.elxvro.scan.pro.ProPaywallPolicy
import com.elxvro.scan.ui.components.ReferenceHeader
import com.elxvro.scan.ui.components.ReferencePrimaryButton
import com.elxvro.scan.ui.theme.ScanTokens

@Composable
fun ProPaywallScreen(
    entitlement: ProEntitlement,
    offer: OneTimePurchaseOffer?,
    onPurchase: (OneTimePurchaseOffer) -> Unit,
    onRestore: () -> Unit,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    val canStartPurchase = ProPaywallPolicy.canStartPurchase(entitlement)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScanTokens.Paper)
    ) {
        ReferenceHeader(title = "ELXVRO Scan PRO", onBack = onClose)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(ScanTokens.Space4),
            verticalArrangement = Arrangement.spacedBy(ScanTokens.Space4)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ScanTokens.Ink, RoundedCornerShape(20.dp))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ScanTokens.Space3)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WorkspacePremium,
                        contentDescription = null,
                        tint = ScanTokens.Warning
                    )
                    Text(
                        text = "Ömür Boyu PRO",
                        color = ScanTokens.TextOnDark,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Bir kez satın al, PRO özelliklerini kalıcı olarak aç. Abonelik ve otomatik yenileme yok.",
                    color = ScanTokens.TextOnDark.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodyMedium
                )
                if (offer != null && canStartPurchase) {
                    Text(
                        text = offer.formattedPrice,
                        color = ScanTokens.Warning,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(ScanTokens.RadiusCard))
                    .border(1.dp, ScanTokens.Divider, RoundedCornerShape(ScanTokens.RadiusCard))
                    .padding(ScanTokens.Space4),
                verticalArrangement = Arrangement.spacedBy(ScanTokens.Space3)
            ) {
                FeatureRow("Reklamsız kullanım")
                FeatureRow("50 PRO profesyonel QR kart tasarımı")
                FeatureRow("Kendi logonu ve arka planını kullanma")
                FeatureRow("1200 px ve 2048 px yüksek çözünürlüklü çıktı")
                FeatureRow("Gelişmiş renk, stil ve marka seçenekleri")
                FeatureRow("Aynı Google Play hesabında satın alımı geri yükleme")
            }

            when (entitlement) {
                ProEntitlement.Pro ->
                    StatusCard("Ömür Boyu PRO aktif • Reklamlar kapalı", ScanTokens.Success)
                ProEntitlement.Pending ->
                    StatusCard("Satın alma Google Play onayı bekliyor", ScanTokens.Warning)
                ProEntitlement.Unknown ->
                    StatusCard("Google Play satın alma durumu kontrol ediliyor", ScanTokens.Muted)
                is ProEntitlement.Error ->
                    StatusCard("Google Play'e şu anda ulaşılamıyor", ScanTokens.Danger)
                ProEntitlement.Free -> Unit
            }

            if (canStartPurchase && offer != null) {
                LifetimePurchaseCard(
                    price = offer.formattedPrice,
                    onClick = { onPurchase(offer) }
                )
            }

            if (entitlement is ProEntitlement.Free && offer == null) {
                StatusCard(
                    "PRO fiyatı Google Play'den alınamadı. Bağlantını kontrol edip yeniden dene.",
                    ScanTokens.Muted
                )
                ReferencePrimaryButton(
                    text = "Google Play'i Yeniden Dene",
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (entitlement is ProEntitlement.Unknown || entitlement is ProEntitlement.Error) {
                ReferencePrimaryButton(
                    text = "Google Play'i Yeniden Dene",
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (entitlement !is ProEntitlement.Pro || offer == null) {
                ReferencePrimaryButton(
                    text = "Satın Alımı Geri Yükle",
                    onClick = onRestore,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                ReferencePrimaryButton(
                    text = "Satın Alımı Kontrol Et",
                    onClick = onRestore,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                text = "Satın alma Google Play üzerinden tek seferlik ürün olarak yapılır. PRO ürünü tüketilmez ve Google Play hesabına bağlıdır. Yeniden kurulumda aynı hesapla geri yüklenebilir. İade veya satın alma sahipliğinin kaldırılması durumunda erişim Google Play durumuna göre güncellenir.",
                color = ScanTokens.Muted,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(ScanTokens.Space4))
        }
    }
}

@Composable
private fun FeatureRow(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ScanTokens.Space2)
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = ScanTokens.Success
        )
        Text(
            text = text,
            color = ScanTokens.Text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun StatusCard(text: String, accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(accent.copy(alpha = 0.08f), RoundedCornerShape(ScanTokens.RadiusCard))
            .border(ScanTokens.BorderWidth, accent.copy(alpha = 0.22f), RoundedCornerShape(ScanTokens.RadiusCard))
            .padding(ScanTokens.Space3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = accent,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun LifetimePurchaseCard(price: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScanTokens.Warning.copy(alpha = 0.08f), RoundedCornerShape(ScanTokens.RadiusCard))
            .border(
                ScanTokens.BorderWidth,
                ScanTokens.Warning.copy(alpha = 0.42f),
                RoundedCornerShape(ScanTokens.RadiusCard)
            )
            .clickable(onClick = onClick)
            .padding(ScanTokens.Space4),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Ömür Boyu PRO'yu Aç",
                    color = ScanTokens.Text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tek seferlik ödeme • Otomatik yenileme yok",
                    color = ScanTokens.Muted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = price,
                color = ScanTokens.Blue,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "Google Play ile Satın Al",
            color = ScanTokens.Blue,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
