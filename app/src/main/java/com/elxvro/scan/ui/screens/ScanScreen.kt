package com.elxvro.scan.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.FlashlightOff
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PlaylistAddCheck
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.elxvro.scan.AppPrefs
import com.elxvro.scan.BarcodePresentation
import com.elxvro.scan.SafeActionPolicy
import com.elxvro.scan.ScanFeedback
import com.elxvro.scan.ScanPresentation
import com.elxvro.scan.ScanStore
import com.elxvro.scan.SmartActionExecutor
import com.elxvro.scan.SmartActionResolver
import com.elxvro.scan.scanner.ScannerController
import com.elxvro.scan.ui.components.LowLightHint
import com.elxvro.scan.ui.components.ReferenceHeader
import com.elxvro.scan.ui.components.ReferencePrimaryButton
import com.elxvro.scan.ui.components.ResultSheet
import com.elxvro.scan.ui.components.ScanOverlay
import com.elxvro.scan.ui.theme.ScanTokens
import com.google.mlkit.vision.barcode.common.Barcode

@Composable
fun ScanScreen(
    store: ScanStore,
    prefs: AppPrefs,
    onOpenSettings: () -> Unit,
    onOpenBatchScan: () -> Unit
) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val settings = remember { prefs.snapshot() }
    val controller = remember(settings.duplicateDelayMs) {
        ScannerController(context.applicationContext, settings.duplicateDelayMs)
    }
    val feedback = remember { ScanFeedback(context.applicationContext) }

    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var torch by remember { mutableStateOf(settings.defaultTorch) }
    var zoom by remember { mutableFloatStateOf(1f) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var result by remember { mutableStateOf<ScanPresentation?>(null) }
    var resultFavorite by remember { mutableStateOf(false) }
    var lowLight by remember { mutableStateOf(false) }

    fun toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    fun executeCurrent(action: com.elxvro.scan.SmartAction) {
        runCatching { SmartActionExecutor.execute(context, action) }
            .onFailure { toast("İşlem açılamadı") }
    }

    fun handleCode(code: Barcode) {
        val raw = code.rawValue?.trim().orEmpty()
        if (raw.isBlank()) return
        val kind = if (code.format == Barcode.FORMAT_QR_CODE) "QR" else "Barkod"
        val format = BarcodePresentation.formatName(code.format)
        val semantic = BarcodePresentation.semanticType(code.valueType)
        val item = store.add(raw, format, kind)
        val action = SmartActionResolver.resolve(raw, kind, semantic)
        val presentation = ScanPresentation(
            raw = raw,
            kind = kind,
            format = format,
            typeLabel = BarcodePresentation.typeLabel(code.valueType),
            semanticType = semantic,
            action = action,
            historyId = item.id
        )
        feedback.play(settings)
        if (settings.autoCopy) SmartActionExecutor.copy(context, raw)
        resultFavorite = false
        result = presentation
        if (settings.safeAutoOpen && SafeActionPolicy.mayAutoOpen(action)) {
            executeCurrent(action)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permissionGranted = granted
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        uris.forEach { uri ->
            controller.scanUri(
                uri = uri,
                onResult = ::handleCode,
                onEmpty = { toast("Bu görselde kod bulunamadı") },
                onError = { toast("Görsel okunamadı") }
            )
        }
    }

    DisposableEffect(controller, feedback) {
        onDispose {
            controller.close()
            feedback.close()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScanTokens.Ink)
    ) {
        ReferenceHeader(
            title = "ELXVRO Scan",
            brand = true,
            onMenu = onOpenSettings,
            trailing = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onOpenBatchScan) {
                        Icon(
                            Icons.Outlined.PlaylistAddCheck,
                            contentDescription = "Toplu Tarama",
                            tint = ScanTokens.Blue
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            Icons.Outlined.WorkspacePremium,
                            contentDescription = "Uygulama bilgisi",
                            tint = ScanTokens.Warning
                        )
                    }
                }
            }
        )

        if (!permissionGranted) {
            PermissionPanel(
                onRequest = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                onGallery = { galleryLauncher.launch("image/*") }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Black)
            ) {
                key(controller) {
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                                previewView = this
                                controller.start(
                                    owner = owner,
                                    previewView = this,
                                    initialTorch = torch,
                                    onResult = ::handleCode,
                                    onZoomChanged = { zoom = it },
                                    onLowLightChanged = { lowLight = it },
                                    onError = { toast("Kamera başlatılamadı") }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                ScanOverlay()

                if (lowLight && !torch) {
                    LowLightHint(
                        canUseTorch = controller.hasFlash(),
                        onEnableTorch = {
                            if (controller.hasFlash()) {
                                torch = controller.setTorch(true)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 14.dp)
                    )
                }

                ZoomPill(
                    zoom = zoom,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 114.dp),
                    onOne = { zoom = controller.setZoomRatio(1f) },
                    onTwo = { zoom = controller.setZoomRatio(2f) }
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 34.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CameraAction(
                        icon = Icons.Outlined.Image,
                        label = "Galeri",
                        onClick = { galleryLauncher.launch("image/*") }
                    )
                    CenterScanAction {
                        previewView?.let { view ->
                            controller.focusAt(view, view.width / 2f, view.height / 2f)
                        }
                    }
                    CameraAction(
                        icon = if (torch) Icons.Outlined.FlashlightOn else Icons.Outlined.FlashlightOff,
                        label = "Fener",
                        active = torch,
                        onClick = {
                            if (!controller.hasFlash()) toast("Bu cihazda flaş bulunamadı")
                            else torch = controller.toggleTorch()
                        }
                    )
                }
            }
        }
    }

    result?.let { current ->
        ResultSheet(
            result = current,
            favorite = resultFavorite,
            onDismiss = { result = null },
            onPrimary = { executeCurrent(current.action) },
            onCopy = {
                SmartActionExecutor.copy(context, current.raw)
                toast("Kopyalandı")
            },
            onShare = { SmartActionExecutor.share(context, current.raw) },
            onFavorite = {
                store.toggleFavorite(current.historyId)
                resultFavorite = !resultFavorite
            }
        )
    }
}

@Composable
private fun PermissionPanel(onRequest: () -> Unit, onGallery: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScanTokens.Paper)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(ScanTokens.Blue.copy(alpha = 0.10f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.CenterFocusStrong, contentDescription = null, tint = ScanTokens.Blue, modifier = Modifier.size(34.dp))
        }
        Text(
            "Kamera izni gerekli",
            style = MaterialTheme.typography.headlineSmall,
            color = ScanTokens.Text,
            modifier = Modifier.padding(top = 18.dp)
        )
        Text(
            "Canlı QR ve barkod taraması için kamera izni verin. İzin vermeden galeriden de kod okuyabilirsiniz.",
            style = MaterialTheme.typography.bodyMedium,
            color = ScanTokens.Muted,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        ReferencePrimaryButton(
            text = "Kamera İzni Ver",
            onClick = onRequest,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(50.dp)
                .border(1.dp, ScanTokens.Divider, RoundedCornerShape(ScanTokens.RadiusButton))
                .clickable(onClick = onGallery),
            contentAlignment = Alignment.Center
        ) {
            Text("Galeriden Oku", color = ScanTokens.Text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun ZoomPill(
    zoom: Float,
    modifier: Modifier = Modifier,
    onOne: () -> Unit,
    onTwo: () -> Unit
) {
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.64f), RoundedCornerShape(18.dp))
            .border(1.dp, Color.White.copy(alpha = 0.34f), RoundedCornerShape(18.dp))
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ZoomChoice("1x", zoom < 1.5f, onOne)
        ZoomChoice("2x", zoom >= 1.5f, onTwo)
    }
}

@Composable
private fun ZoomChoice(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (selected) Color.White.copy(alpha = 0.16f) else Color.Transparent, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun CameraAction(
    icon: ImageVector,
    label: String,
    active: Boolean = false,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(ScanTokens.SideScanButton)
                .background(
                    if (active) ScanTokens.Blue.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.58f),
                    CircleShape
                )
                .border(
                    1.dp,
                    if (active) ScanTokens.Blue else Color.White.copy(alpha = 0.34f),
                    CircleShape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(23.dp))
        }
        Text(
            label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}

@Composable
private fun CenterScanAction(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(ScanTokens.CenterScanButton)
            .background(ScanTokens.Blue, CircleShape)
            .padding(6.dp)
            .background(Color(0xFF05101B), CircleShape)
            .border(2.dp, Color.White.copy(alpha = 0.72f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Outlined.CenterFocusStrong, contentDescription = "Odakla", tint = Color.White, modifier = Modifier.size(31.dp))
    }
}
