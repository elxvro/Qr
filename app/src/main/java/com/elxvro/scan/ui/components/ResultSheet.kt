package com.elxvro.scan.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elxvro.scan.ResultSheetPolicy
import com.elxvro.scan.ScanPresentation
import com.elxvro.scan.SmartActionType
import com.elxvro.scan.UrlRiskLevel
import com.elxvro.scan.UrlSafetyPolicy
import com.elxvro.scan.UrlSafetyPresentation
import com.elxvro.scan.ui.theme.ScanTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultSheet(
    result: ScanPresentation,
    favorite: Boolean,
    onDismiss: () -> Unit,
    onPrimary: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onFavorite: () -> Unit
) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val urlSafety = remember(result.action.type, result.action.value) {
        if (result.action.type == SmartActionType.OPEN_URL) {
            UrlSafetyPolicy.analyze(result.action.value)
        } else {
            null
        }
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = state,
        containerColor = ScanTokens.Card,
        contentColor = ScanTokens.Text,
        shape = RoundedCornerShape(topStart = ScanTokens.RadiusSheet, topEnd = ScanTokens.RadiusSheet),
        dragHandle = {
            Spacer(
                Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .size(width = 42.dp, height = 4.dp)
                    .background(ScanTokens.Divider, RoundedCornerShape(99.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = result.typeLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = ScanTokens.Text,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = result.format,
                    style = MaterialTheme.typography.bodySmall,
                    color = ScanTokens.Muted,
                    modifier = Modifier.padding(top = 2.dp)
                )
                SelectionContainer {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .background(ScanTokens.Paper, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = result.raw,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ScanTokens.Text,
                            maxLines = 6,
                            overflow = TextOverflow.Ellipsis
                        )
                        resultMetadata(result)?.let { metadata ->
                            Text(
                                text = metadata,
                                style = MaterialTheme.typography.bodySmall,
                                color = ScanTokens.Muted,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                urlSafety?.let { report ->
                    UrlSafetyCard(
                        level = report.level,
                        host = report.host,
                        reasons = report.reasons.map(UrlSafetyPresentation::reasonText),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    )
                }

                if (ResultSheetPolicy.showPrimaryAction(result.action.type)) {
                    ReferencePrimaryButton(
                        text = if (
                            urlSafety != null && urlSafety.level != UrlRiskLevel.LOW
                        ) {
                            "Siteyi Yine de Aç"
                        } else {
                            result.action.label
                        },
                        onClick = onPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    )
                }
            }
            DividerLine()
            ReferenceActionRow(Icons.Outlined.ContentCopy, "Kopyala", onClick = onCopy)
            DividerLine()
            ReferenceActionRow(Icons.Outlined.Share, "Paylaş", onClick = onShare)
            DividerLine()
            ReferenceActionRow(
                if (favorite) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                if (favorite) "Favorilerden Çıkar" else "Favorilere Ekle",
                iconColor = ScanTokens.Warning,
                onClick = onFavorite
            )
            DividerLine()
            ReferenceActionRow(
                Icons.Outlined.History,
                "Geçmişe Kaydedildi",
                subtitle = "Bu sonuç cihazınızdaki tarama geçmişinde",
                showChevron = false,
                iconColor = ScanTokens.Muted,
                onClick = {}
            )
        }
    }
}

@Composable
private fun UrlSafetyCard(
    level: UrlRiskLevel,
    host: String?,
    reasons: List<String>,
    modifier: Modifier = Modifier
) {
    val accent = when (level) {
        UrlRiskLevel.LOW -> ScanTokens.Blue
        UrlRiskLevel.MEDIUM -> ScanTokens.Warning
        UrlRiskLevel.HIGH -> androidx.compose.ui.graphics.Color(0xFFD13B3B)
    }
    Column(
        modifier = modifier
            .background(accent.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = UrlSafetyPresentation.label(level),
            color = accent,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        if (!host.isNullOrBlank()) {
            Text(
                text = "Hedef: $host",
                color = ScanTokens.Text,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
        if (reasons.isEmpty()) {
            Text(
                text = "HTTPS bağlantısı yerel kontrolde ek risk işareti göstermedi.",
                color = ScanTokens.Muted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 3.dp)
            )
        } else {
            reasons.take(3).forEach { reason ->
                Text(
                    text = "• $reason",
                    color = ScanTokens.Muted,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}

private fun resultMetadata(result: ScanPresentation): String? {
    if (result.action.label == "Takvime Ekle" || result.action.value.startsWith("BEGIN:VEVENT", ignoreCase = true)) {
        return "Takvim etkinliği"
    }
    return when (result.action.type) {
        SmartActionType.OPEN_URL -> runCatching { Uri.parse(result.action.value).host }
            .getOrNull()?.takeIf { !it.isNullOrBlank() }?.let { "Alan adı: $it" }
        SmartActionType.SEARCH_PRODUCT -> "Ürün kodu • ${result.format}"
        SmartActionType.WIFI -> "Wi-Fi bağlantı bilgisi"
        SmartActionType.DIAL -> "Telefon numarası"
        SmartActionType.EMAIL -> "E-posta adresi"
        SmartActionType.SMS -> "SMS içeriği"
        SmartActionType.MAP -> "Konum bilgisi"
        SmartActionType.CONTACT -> "Kişi kartı"
        SmartActionType.SHARE_TEXT -> "Metin"
    }
}
