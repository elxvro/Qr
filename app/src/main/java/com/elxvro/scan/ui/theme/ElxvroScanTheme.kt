package com.elxvro.scan.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val ScanColors = darkColorScheme(
    primary = ScanTokens.Blue,
    onPrimary = ScanTokens.TextOnDark,
    secondary = ScanTokens.BlueBright,
    background = ScanTokens.Ink,
    onBackground = ScanTokens.TextOnDark,
    surface = ScanTokens.Card,
    onSurface = ScanTokens.Text,
    surfaceVariant = ScanTokens.InkRaised,
    onSurfaceVariant = ScanTokens.TextOnDark,
    outline = ScanTokens.Divider,
    error = ScanTokens.Danger
)

private val ScanTypography = Typography(
    headlineSmall = TextStyle(fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 17.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold),
    labelSmall = TextStyle(fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.Medium)
)

@Composable
fun ElxvroScanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ScanColors,
        typography = ScanTypography,
        content = content
    )
}
