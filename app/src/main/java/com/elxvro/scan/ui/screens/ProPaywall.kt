package com.elxvro.scan.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elxvro.scan.billing.BillingProducts
import com.elxvro.scan.billing.BillingRepository
import com.elxvro.scan.billing.ProEntitlement
import com.elxvro.scan.billing.SubscriptionOffer
import com.elxvro.scan.ui.components.ReferencePrimaryButton
import com.elxvro.scan.ui.theme.ScanTokens

@Composable
fun ProPaywall(repository: BillingRepository, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val entitlement by repository.entitlement.collectAsState()
    val offers by repository.offers.collectAsState()
    var selectedBasePlan by remember(offers) {
        mutableStateOf(offers.firstOrNull { it.basePlanId == BillingProducts.YEARLY_BASE_PLAN_ID }?.basePlanId ?: offers.firstOrNull()?.basePlanId)
    }
    val selected = offers.firstOrNull { it.basePlanId == selectedBasePlan }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScanTokens.Ink)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onDismiss) {
                Icon(Icons.Outlined.Close, contentDescription = "Kapat", tint = ScanTokens.TextOnDark)
            }
        }
        Box(
            modifier = Modifier
                .size(74.dp)
                .background(ScanTokens.Warning.copy(alpha = 0.13f), CircleShape)
                .border(1.dp, ScanTokens.Warning.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.WorkspacePremium, contentDescription = null, tint = ScanTokens.Warning, modifier = Modifier.size(38.dp))
        }
        Text(
            "ELXVRO Scan PRO",
            color = ScanTokens.TextOnDark,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 14.dp)
        )
        Text(
            "Kendi markanla profesyonel QR kodlar ve QR Kartlar oluştur.",
            color = ScanTokens.Muted,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)
        )

        Benefit("Kendi logonu ekle veya logosuz kullan")
        Benefit("Minimal, Kurumsal, Wi-Fi, Sosyal ve Etkinlik QR Kartları")
        Benefit("2048 px yüksek çözünürlüklü dışa aktarma")
        Benefit("Gelişmiş renk, stil ve kart özelleştirme")

        if (offers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp)
                    .background(ScanTokens.InkRaised, RoundedCornerShape(ScanTokens.RadiusCard))
                    .border(1.dp, ScanTokens.TextOnDark.copy(alpha = 0.08f), RoundedCornerShape(ScanTokens.RadiusCard))
                    .padding(16.dp)
            ) {
                Text(
                    "Google Play abonelik fiyatları yükleniyor. Ürün henüz Play Console'da etkin değilse fiyatlar burada görünmez.",
                    color = ScanTokens.TextOnDark.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                offers.sortedBy { if (it.basePlanId == BillingProducts.MONTHLY_BASE_PLAN_ID) 0 else 1 }.forEach { offer ->
                    OfferCard(
                        offer = offer,
                        selected = selectedBasePlan == offer.basePlanId,
                        onClick = { selectedBasePlan = offer.basePlanId }
                    )
                }
            }
        }

        ReferencePrimaryButton(
            text = when (entitlement) {
                is ProEntitlement.Pro -> "PRO Aktif"
                is ProEntitlement.Pending -> "Satın Alma Beklemede"
                else -> "PRO'ya Geç"
            },
            enabled = selected != null && entitlement !is ProEntitlement.Pro && entitlement !is ProEntitlement.Pending,
            onClick = {
                val activity = context.findActivity()
                if (activity == null || selected == null) {
                    Toast.makeText(context, "Google Play satın alma ekranı açılamadı", Toast.LENGTH_SHORT).show()
                } else {
                    repository.launchPurchase(activity, selected)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .clickable { repository.restorePurchases() }
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Restore, contentDescription = null, tint = ScanTokens.Blue, modifier = Modifier.size(19.dp))
            Text("Satın alımları geri yükle", color = ScanTokens.BlueBright, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 7.dp))
        }

        if (entitlement is ProEntitlement.Pro) {
            Text(
                "Aboneliği Google Play'de yönet",
                color = ScanTokens.TextOnDark.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .clickable { openManageSubscription(context) }
                    .padding(10.dp)
            )
        }
        Text(
            "Ödeme ve yenileme Google Play tarafından yönetilir. İptal edildiğinde PRO erişimi mevcut ücretli dönem sona erene kadar devam eder.",
            color = ScanTokens.Muted,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 14.dp, bottom = 20.dp)
        )
    }
}

@Composable
private fun Benefit(text: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = ScanTokens.Success, modifier = Modifier.size(20.dp))
        Text(text, color = ScanTokens.TextOnDark, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 10.dp))
    }
}

@Composable
private fun OfferCard(offer: SubscriptionOffer, selected: Boolean, onClick: () -> Unit) {
    val yearly = offer.basePlanId == BillingProducts.YEARLY_BASE_PLAN_ID
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selected) ScanTokens.Blue.copy(alpha = 0.16f) else ScanTokens.InkRaised, RoundedCornerShape(ScanTokens.RadiusCard))
            .border(1.dp, if (selected) ScanTokens.Blue else Color.White.copy(alpha = 0.08f), RoundedCornerShape(ScanTokens.RadiusCard))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(20.dp).border(2.dp, if (selected) ScanTokens.Blue else ScanTokens.Muted, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (selected) Box(Modifier.size(10.dp).background(ScanTokens.Blue, CircleShape))
        }
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(if (yearly) "Yıllık PRO" else "Aylık PRO", color = ScanTokens.TextOnDark, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(if (yearly) "12 aylık abonelik" else "Her ay yenilenir", color = ScanTokens.Muted, style = MaterialTheme.typography.bodySmall)
        }
        Text(offer.formattedPrice, color = ScanTokens.TextOnDark, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun openManageSubscription(context: Context) {
    val uri = Uri.parse("https://play.google.com/store/account/subscriptions?sku=${BillingProducts.PRODUCT_ID}&package=${context.packageName}")
    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
}
