package com.elxvro.scan.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elxvro.scan.AppPrefs
import com.elxvro.scan.ScanStore
import com.elxvro.scan.billing.BillingRepository
import com.elxvro.scan.billing.ProEntitlement
import com.elxvro.scan.pro.ProAccessPolicy
import com.elxvro.scan.pro.ProFeature
import com.elxvro.scan.ui.screens.CreateScreen
import com.elxvro.scan.ui.screens.HistoryScreen
import com.elxvro.scan.ui.screens.ProPaywall
import com.elxvro.scan.ui.screens.QrCardScreen
import com.elxvro.scan.ui.screens.ScanScreen
import com.elxvro.scan.ui.screens.SettingsScreen
import com.elxvro.scan.ui.theme.ScanTokens

enum class AppTab(val label: String, val icon: ImageVector) {
    SCAN("Tara", Icons.Outlined.QrCodeScanner),
    CREATE("Oluştur", Icons.Outlined.QrCode2),
    HISTORY("Geçmiş", Icons.Outlined.History),
    SETTINGS("Ayarlar", Icons.Outlined.Settings)
}

private enum class ExtraScreen { QR_CARD, PAYWALL }

@Composable
fun ElxvroScanApp() {
    val context = LocalContext.current
    val store = remember {
        ScanStore(context.getSharedPreferences("elxvro_scan", Context.MODE_PRIVATE))
    }
    val prefs = remember { AppPrefs(context.applicationContext) }
    val billing = remember { BillingRepository(context.applicationContext) }
    val entitlement by billing.entitlement.collectAsState()
    var tab by remember { mutableStateOf(AppTab.SCAN) }
    var extraScreen by remember { mutableStateOf<ExtraScreen?>(null) }

    DisposableEffect(billing) {
        onDispose { billing.close() }
    }

    fun selectTab(value: AppTab) {
        extraScreen = null
        tab = value
    }

    Scaffold(
        containerColor = ScanTokens.Ink,
        bottomBar = {
            ReferenceBottomBar(selected = tab, onSelect = ::selectTab)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            when (extraScreen) {
                ExtraScreen.PAYWALL -> ProPaywall(
                    repository = billing,
                    onDismiss = { extraScreen = null }
                )
                ExtraScreen.QR_CARD -> QrCardScreen(
                    entitlement = entitlement,
                    onBack = { extraScreen = null },
                    onRequirePro = { extraScreen = ExtraScreen.PAYWALL }
                )
                null -> when (tab) {
                    AppTab.SCAN -> ScanScreen(
                        store = store,
                        prefs = prefs,
                        onOpenSettings = { selectTab(AppTab.SETTINGS) }
                    )
                    AppTab.CREATE -> {
                        CreateScreen(onBack = { selectTab(AppTab.SCAN) })
                        ProEntryPill(
                            isPro = ProAccessPolicy.canUse(ProFeature.QR_CARD, entitlement),
                            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 14.dp, bottom = 14.dp),
                            onClick = {
                                extraScreen = if (ProAccessPolicy.canUse(ProFeature.QR_CARD, entitlement)) {
                                    ExtraScreen.QR_CARD
                                } else {
                                    ExtraScreen.PAYWALL
                                }
                            }
                        )
                    }
                    AppTab.HISTORY -> HistoryScreen(store = store, onBack = { selectTab(AppTab.SCAN) })
                    AppTab.SETTINGS -> {
                        SettingsScreen(store = store, prefs = prefs, onBack = { selectTab(AppTab.SCAN) })
                        ProStatusPill(
                            entitlement = entitlement,
                            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 14.dp, bottom = 14.dp),
                            onClick = { extraScreen = ExtraScreen.PAYWALL }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProEntryPill(isPro: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .background(ScanTokens.Ink, RoundedCornerShape(16.dp))
            .border(1.dp, if (isPro) ScanTokens.Blue else ScanTokens.Warning, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.WorkspacePremium, contentDescription = null, tint = if (isPro) ScanTokens.Blue else ScanTokens.Warning, modifier = Modifier.size(19.dp))
        Column(Modifier.padding(start = 8.dp)) {
            Text("QR Kart", color = ScanTokens.TextOnDark, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(if (isPro) "PRO aktif" else "PRO özelliği", color = ScanTokens.Muted, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun ProStatusPill(entitlement: ProEntitlement, modifier: Modifier, onClick: () -> Unit) {
    val active = entitlement is ProEntitlement.Pro
    Row(
        modifier = modifier
            .background(ScanTokens.Ink, RoundedCornerShape(16.dp))
            .border(1.dp, if (active) ScanTokens.Success else ScanTokens.Warning, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.WorkspacePremium, null, tint = if (active) ScanTokens.Success else ScanTokens.Warning, modifier = Modifier.size(18.dp))
        Text(if (active) "PRO Aktif" else "PRO'ya Geç", color = ScanTokens.TextOnDark, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 7.dp))
    }
}

@Composable
fun ReferenceBottomBar(selected: AppTab, onSelect: (AppTab) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScanTokens.Ink)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(color = ScanTokens.InkRaised, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ScanTokens.BottomBarHeight),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTab.entries.forEach { item ->
                val active = item == selected
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelect(item) }
                        .padding(vertical = 7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (active) ScanTokens.Blue else ScanTokens.TextOnDark.copy(alpha = 0.66f)
                    )
                    Text(
                        text = item.label,
                        color = if (active) ScanTokens.Blue else ScanTokens.TextOnDark.copy(alpha = 0.66f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
