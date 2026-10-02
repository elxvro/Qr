package com.elxvro.scan.ui

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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.elxvro.scan.ui.theme.ScanTokens

enum class AppTab(val label: String, val icon: ImageVector) {
    SCAN("Tara", Icons.Outlined.QrCodeScanner),
    CREATE("Oluştur", Icons.Outlined.QrCode2),
    HISTORY("Geçmiş", Icons.Outlined.History),
    SETTINGS("Ayarlar", Icons.Outlined.Settings)
}

@Composable
fun ElxvroScanApp() {
    var tab by remember { mutableStateOf(AppTab.SCAN) }
    Scaffold(
        containerColor = ScanTokens.Ink,
        bottomBar = {
            ReferenceBottomBar(selected = tab, onSelect = { tab = it })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            PlaceholderRoot(tab)
        }
    }
}

@Composable
private fun PlaceholderRoot(tab: AppTab) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (tab == AppTab.SCAN) ScanTokens.Ink else ScanTokens.Paper),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tab.label,
            color = if (tab == AppTab.SCAN) ScanTokens.TextOnDark else ScanTokens.Text,
            style = MaterialTheme.typography.headlineSmall
        )
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
        Divider(color = ScanTokens.InkRaised, thickness = 1.dp)
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
                        tint = if (active) ScanTokens.Blue else ScanTokens.TextOnDark.copy(alpha = 0.72f)
                    )
                    Text(
                        text = item.label,
                        color = if (active) ScanTokens.Blue else ScanTokens.TextOnDark.copy(alpha = 0.72f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
