package com.elxvro.scan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.elxvro.scan.ui.theme.ScanTokens

@Composable
fun LowLightHint(
    canUseTorch: Boolean,
    onEnableTorch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.72f), RoundedCornerShape(14.dp))
            .border(1.dp, ScanTokens.Warning.copy(alpha = 0.78f), RoundedCornerShape(14.dp))
            .clickable(enabled = canUseTorch, onClick = onEnableTorch)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.FlashlightOn,
            contentDescription = null,
            tint = ScanTokens.Warning
        )
        Column(Modifier.padding(start = 8.dp)) {
            Text(
                text = "Ortam karanlık",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = if (canUseTorch) "Daha hızlı tarama için feneri aç" else "Daha aydınlık bir alana geç",
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
