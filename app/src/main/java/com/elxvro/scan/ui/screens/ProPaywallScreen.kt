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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elxvro.scan.billing.BillingProducts
import com.elxvro.scan.billing.ProEntitlement
import com.elxvro.scan.billing.SubscriptionOffer
import com.elxvro.scan.ui.components.ReferenceHeader
import com.elxvro.scan.ui.components.ReferencePrimaryButton
import com.elxvro.scan.ui.theme.ScanTokens

@Composable
fun ProPaywallScreen(
    entitlement: ProEntitlement,
    offers: List<SubscriptionOffer>,
    onPurchase: (SubscriptionOffer) -> Unit,
    onRestore: () -> Unit,
    onManageSubscription: () -> Unit,
    onClose: () -> Unit
) {
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
                        text = "QR'larını markana dönüştür",
                        color = ScanTokens.Text,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Kendi logon, QR Kart tasarımları ve yüksek çözünürlüklü dışa aktarma PRO ile açılır.",
                        color = ScanTokens.Muted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            FeatureRow("Kendi logonu ekle")
            FeatureRow("Minimal, Kurumsal, Wi-Fi, Sosyal ve Etkinlik QR Kartları")
            FeatureRow("1200 px ve 2048 px yüksek çözünürlüklü çıktı")
            FeatureRow("Gelişmiş renkler, arka plan ve premium stiller")

            when (entitlement) {
                ProEntitlement.Pro -> StatusCard("PRO aktif", ScanTokens.Success)
                ProEntitlement.Pending -> StatusCard("Satın alma beklemede", ScanTokens.Warning)
                ProEntitlement.Unknown -> StatusCard("Google Play durumu kontrol ediliyor", ScanTokens.Muted)
                is ProEntitlement.Error -> StatusCard("Google Play'e şu anda ulaşılamıyor", ScanTokens.Danger)
                ProEntitlement.Free -> Unit
            }

            offers.forEach { offer ->
                val title = when (offer.basePlanId) {
                    BillingProducts.MONTHLY_BASE_PLAN_ID -> "Aylık PRO"
                    BillingProducts.YEARLY_BASE_PLAN_ID -> "Yıllık PRO"
                    else -> return@forEach
                }
                PlanCard(
                    title = title,
                    price = offer.formattedPrice,
                    onClick = { onPurchase(offer) }
                )
            }

            if (offers.isEmpty() && entitlement !is ProEntitlement.Pro) {
                Text(
                    text = "Abonelik seçenekleri Google Play'den yüklenemedi. Tarama ve ücretsiz QR özellikleri kullanılmaya devam eder.",
                    color = ScanTokens.Muted,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            ReferencePrimaryButton(
                text = "Satın alımları geri yükle",
                onClick = onRestore,
                modifier = Modifier.fillMaxWidth()
            )

            if (entitlement is ProEntitlement.Pro) {
                TextButton(
                    onClick = onManageSubscription,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Aboneliği yönet", color = ScanTokens.Blue)
                }
            }

            Text(
                text = "Fiyat ve dönem bilgileri Google Play tarafından gösterilir. Abonelik Google Play hesabından yönetilebilir.",
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
private fun PlanCard(title: String, price: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScanTokens.Card, RoundedCornerShape(ScanTokens.RadiusCard))
            .border(ScanTokens.BorderWidth, ScanTokens.Divider, RoundedCornerShape(ScanTokens.RadiusCard))
            .clickable(onClick = onClick)
            .padding(ScanTokens.Space4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = ScanTokens.Text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = price,
                color = ScanTokens.Blue,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        Text(
            text = "Seç",
            color = ScanTokens.Blue,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
