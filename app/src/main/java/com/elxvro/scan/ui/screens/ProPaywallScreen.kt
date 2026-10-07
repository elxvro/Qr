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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ScanTokens.Space3)
            ) {
                Icon(
                    imageVector = Icons.Outlined.WorkspacePremium,
                    contentDescription = null,
                    tint = ScanTokens.Warning
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Ömür Boyu ELXVRO Scan PRO",
                        color = ScanTokens.Text,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Tek sefer ödeme yap, PRO özelliklerini kalıcı olarak aç. Aylık veya yıllık abonelik yok.",
                        color = ScanTokens.Muted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            FeatureRow("Tek seferlik ödeme • ömür boyu kullanım")
            FeatureRow("Reklamsız kullanım")
            FeatureRow("Kendi logonu ekle")
            FeatureRow("Premium QR Kart tasarımları")
            FeatureRow("1200 px ve 2048 px yüksek çözünürlüklü çıktı")
            FeatureRow("Gelişmiş renkler, arka plan ve premium stiller")

            when (entitlement) {
                ProEntitlement.Pro -> StatusCard("Ömür Boyu PRO aktif", ScanTokens.Success)
                ProEntitlement.Pending -> StatusCard("Satın alma beklemede", ScanTokens.Warning)
                ProEntitlement.Unknown -> StatusCard("Google Play durumu kontrol ediliyor", ScanTokens.Muted)
                is ProEntitlement.Error -> StatusCard("Google Play'e şu anda ulaşılamıyor", ScanTokens.Danger)
                ProEntitlement.Free -> Unit
            }

            if (canStartPurchase && offer != null) {
                LifetimePurchaseCard(
                    price = offer.formattedPrice,
                    onClick = { onPurchase(offer) }
                )
            }

            if (canStartPurchase && offer == null) {
                Text(
                    text = "Ömür Boyu PRO ürünü Google Play'den yüklenemedi. Ücretsiz özellikleri kullanmaya devam edebilirsin.",
                    color = ScanTokens.Muted,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            ReferencePrimaryButton(
                text = "Satın Alımı Geri Yükle",
                onClick = onRestore,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Satın alma Google Play hesabına bağlıdır. PRO ürünü tüketilmez; aynı hesapta yeniden kurulum sonrası geri yüklenebilir.",
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
            tint = ScanTokens.Blue
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScanTokens.Card, RoundedCornerShape(ScanTokens.RadiusCard))
            .border(ScanTokens.BorderWidth, ScanTokens.Warning.copy(alpha = 0.42f), RoundedCornerShape(ScanTokens.RadiusCard))
            .clickable(onClick = onClick)
            .padding(ScanTokens.Space4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = "Ömür Boyu PRO",
                color = ScanTokens.Text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Tek seferlik ödeme",
                color = ScanTokens.Muted,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = price,
                color = ScanTokens.Blue,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        Text(
            text = "Satın Al",
            color = ScanTokens.Blue,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
