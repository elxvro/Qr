package com.elxvro.scan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elxvro.scan.R
import com.elxvro.scan.ui.theme.ScanTokens

@Composable
fun ReferenceHeader(
    title: String,
    brand: Boolean = false,
    onMenu: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    trailing: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScanTokens.Ink)
            .statusBarsPadding()
            .height(ScanTokens.HeaderHeight)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            onBack != null -> IconButton(onClick = onBack) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Geri", tint = ScanTokens.TextOnDark)
            }
            onMenu != null -> IconButton(onClick = onMenu) {
                Icon(Icons.Outlined.Menu, contentDescription = "Menü", tint = ScanTokens.TextOnDark)
            }
            else -> Spacer(Modifier.size(48.dp))
        }
        if (brand) {
            Icon(
                painter = painterResource(R.drawable.ic_launcher),
                contentDescription = "ELXVRO",
                tint = Color.Unspecified,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.size(8.dp))
        }
        Text(
            text = title,
            color = ScanTokens.TextOnDark,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        trailing()
    }
}

@Composable
fun ReferencePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(ScanTokens.PrimaryButtonHeight),
        shape = RoundedCornerShape(ScanTokens.RadiusButton),
        colors = ButtonDefaults.buttonColors(
            containerColor = ScanTokens.Blue,
            contentColor = Color.White,
            disabledContainerColor = ScanTokens.Blue.copy(alpha = 0.35f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        ),
        contentPadding = ButtonDefaults.ContentPadding
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun ReferenceChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = if (selected) ScanTokens.Blue else ScanTokens.InkRaised,
                shape = RoundedCornerShape(ScanTokens.RadiusChip)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = ScanTokens.TextOnDark,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
fun ReferenceActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    iconColor: Color = ScanTokens.Blue,
    showChevron: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(iconColor.copy(alpha = 0.10f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 11.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, color = ScanTokens.Text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            if (!subtitle.isNullOrBlank()) {
                Text(
                    subtitle,
                    color = ScanTokens.Muted,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (showChevron) {
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = ScanTokens.Muted, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun DividerLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(ScanTokens.Divider)
    )
}
