package com.elxvro.scan.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elxvro.scan.AppPrefs
import com.elxvro.scan.ScanStore
import com.elxvro.scan.ads.AdBanner
import com.elxvro.scan.ads.InterstitialAdController
import com.elxvro.scan.billing.BillingRepository
import com.elxvro.scan.billing.ProEntitlement
import com.elxvro.scan.pro.ProTestMode
import com.elxvro.scan.ui.screens.BarcodeCreateScreen
import com.elxvro.scan.ui.screens.BatchScanScreen
import com.elxvro.scan.ui.screens.CreateScreen
import com.elxvro.scan.ui.screens.HistoryScreen
import com.elxvro.scan.ui.screens.ProPaywallScreen
import com.elxvro.scan.ui.screens.QrCardEditorScreen
import com.elxvro.scan.ui.screens.ScanScreen
import com.elxvro.scan.ui.screens.SettingsScreen
import com.elxvro.scan.ui.theme.ScanTokens

enum class AppTab(val label: String, val icon: ImageVector) {
    SCAN("Tara", Icons.Outlined.QrCodeScanner),
    CREATE("Oluştur", Icons.Outlined.QrCode2),
    HISTORY("Geçmiş", Icons.Outlined.History),
    SETTINGS("Ayarlar", Icons.Outlined.Settings)
}

@Composable
fun ElxvroScanApp() {
    val context = LocalContext.current
    val store = remember {
        ScanStore(context.getSharedPreferences("elxvro_scan", Context.MODE_PRIVATE))
    }
    val prefs = remember { AppPrefs(context.applicationContext) }
    val billing = remember { BillingRepository(context.applicationContext) }
    val entitlement by billing.entitlement.collectAsStateWithLifecycle()
    val offer by billing.offer.collectAsStateWithLifecycle()
    val interstitialAds = remember(context) { InterstitialAdController(context.applicationContext) }
    val proTestMode = remember(context) {
        context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }
    val effectiveEntitlement = ProTestMode.resolve(entitlement, proTestMode)
    val adsEnabled = if (proTestMode) true else entitlement is ProEntitlement.Free || entitlement is ProEntitlement.Error

    var tab by remember { mutableStateOf(AppTab.SCAN) }
    var showPaywall by remember { mutableStateOf(false) }
    var showBarcodeCreate by remember { mutableStateOf(false) }
    var showBatchScan by remember { mutableStateOf(false) }
    var showQrCard by remember { mutableStateOf(false) }
    var qrCardPayload by remember { mutableStateOf("https://elxvro.com") }

    DisposableEffect(billing) {
        billing.start()
        onDispose { billing.close() }
    }

    DisposableEffect(interstitialAds, adsEnabled) {
        if (adsEnabled) interstitialAds.preload() else interstitialAds.clear()
        onDispose { }
    }

    fun onAdEligibleAction() {
        if (!adsEnabled) return
        context.findActivity()?.let(interstitialAds::onEligibleAction)
    }

    val childScreenOpen = showPaywall || showQrCard || showBarcodeCreate || showBatchScan
    val backState = AppBackState(
        tab = tab,
        showPaywall = showPaywall,
        showQrCard = showQrCard,
        showBarcodeCreate = showBarcodeCreate,
        showBatchScan = showBatchScan
    )
    val backAction = AppBackNavigationPolicy.resolve(backState)

    BackHandler(enabled = backAction != AppBackAction.ALLOW_SYSTEM_EXIT) {
        when (backAction) {
            AppBackAction.CLOSE_PAYWALL -> showPaywall = false
            AppBackAction.CLOSE_QR_CARD -> showQrCard = false
            AppBackAction.CLOSE_BARCODE_CREATE -> showBarcodeCreate = false
            AppBackAction.CLOSE_BATCH_SCAN -> showBatchScan = false
            AppBackAction.GO_TO_SCAN -> tab = AppTab.SCAN
            AppBackAction.ALLOW_SYSTEM_EXIT -> Unit
        }
    }

    Scaffold(
        containerColor = ScanTokens.Ink,
        bottomBar = {
            if (!childScreenOpen) {
                Column {
                    if (adsEnabled && tab != AppTab.SCAN) {
                        AdBanner()
                    }
                    ReferenceBottomBar(selected = tab, onSelect = { tab = it })
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (childScreenOpen) 0.dp else padding.calculateBottomPadding())
        ) {
            when {
                showPaywall -> ProPaywallScreen(
                    entitlement = effectiveEntitlement,
                    offer = offer,
                    onPurchase = { offer ->
                        context.findActivity()?.let { activity ->
                            billing.launchPurchase(activity, offer)
                        }
                    },
                    onRestore = billing::restorePurchases,
                    onClose = { showPaywall = false }
                )
                showBarcodeCreate -> BarcodeCreateScreen(
                    store = store,
                    onBack = { showBarcodeCreate = false }
                )
                showBatchScan -> BatchScanScreen(
                    store = store,
                    prefs = prefs,
                    onBack = { showBatchScan = false }
                )
                showQrCard -> QrCardEditorScreen(
                    entitlement = effectiveEntitlement,
                    initialPayload = qrCardPayload,
                    onOpenPaywall = { showPaywall = true },
                    onBack = { showQrCard = false }
                )
                else -> when (tab) {
                    AppTab.SCAN -> ScanScreen(
                        store = store,
                        prefs = prefs,
                        onOpenSettings = { tab = AppTab.SETTINGS },
                        onOpenBatchScan = { showBatchScan = true }
                    )
                    AppTab.CREATE -> CreateScreen(
                        entitlement = effectiveEntitlement,
                        onOpenPaywall = { showPaywall = true },
                        onOpenQrCard = { payload ->
                            qrCardPayload = payload
                            showQrCard = true
                        },
                        onOpenBarcodeCreate = { showBarcodeCreate = true },
                        onAdEligibleAction = ::onAdEligibleAction,
                        onBack = { tab = AppTab.SCAN }
                    )
                    AppTab.HISTORY -> HistoryScreen(store = store, onBack = { tab = AppTab.SCAN })
                    AppTab.SETTINGS -> SettingsScreen(
                        store = store,
                        prefs = prefs,
                        entitlement = effectiveEntitlement,
                        proTestMode = proTestMode,
                        onUpgradePro = { showPaywall = true },
                        onRestorePurchases = billing::restorePurchases,
                        onRetryBilling = billing::refreshPurchases,
                        onBack = { tab = AppTab.SCAN }
                    )
                }
            }
        }
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

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
