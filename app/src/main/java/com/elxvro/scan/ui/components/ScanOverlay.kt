package com.elxvro.scan.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.elxvro.scan.ui.theme.ScanTokens
import kotlin.math.min

@Composable
fun ScanOverlay(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "scanLine")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanProgress"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val frame = min(size.width * 0.68f, size.height * 0.39f)
        val left = (size.width - frame) / 2f
        val top = size.height * 0.19f
        val right = left + frame
        val bottom = top + frame
        val scrim = Color.Black.copy(alpha = 0.18f)
        drawRect(scrim, topLeft = Offset.Zero, size = androidx.compose.ui.geometry.Size(size.width, top))
        drawRect(scrim, topLeft = Offset(0f, bottom), size = androidx.compose.ui.geometry.Size(size.width, size.height - bottom))
        drawRect(scrim, topLeft = Offset(0f, top), size = androidx.compose.ui.geometry.Size(left, frame))
        drawRect(scrim, topLeft = Offset(right, top), size = androidx.compose.ui.geometry.Size(size.width - right, frame))

        val stroke = 5.dp.toPx()
        val arm = frame * 0.18f
        val blue = ScanTokens.Blue
        val cap = StrokeCap.Round
        fun line(a: Offset, b: Offset) = drawLine(blue, a, b, strokeWidth = stroke, cap = cap)
        line(Offset(left, top + arm), Offset(left, top))
        line(Offset(left, top), Offset(left + arm, top))
        line(Offset(right - arm, top), Offset(right, top))
        line(Offset(right, top), Offset(right, top + arm))
        line(Offset(left, bottom - arm), Offset(left, bottom))
        line(Offset(left, bottom), Offset(left + arm, bottom))
        line(Offset(right - arm, bottom), Offset(right, bottom))
        line(Offset(right, bottom), Offset(right, bottom - arm))

        val y = top + 16.dp.toPx() + (frame - 32.dp.toPx()) * progress
        drawLine(
            color = ScanTokens.BlueBright.copy(alpha = 0.72f),
            start = Offset(left + 22.dp.toPx(), y),
            end = Offset(right - 22.dp.toPx(), y),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
