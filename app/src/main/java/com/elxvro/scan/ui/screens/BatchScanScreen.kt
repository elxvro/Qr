package com.elxvro.scan.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FlashlightOff
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.elxvro.scan.AppPrefs
import com.elxvro.scan.BarcodePresentation
import com.elxvro.scan.ScanFeedback
import com.elxvro.scan.ScanStore
import com.elxvro.scan.batch.BatchScanEntry
import com.elxvro.scan.batch.BatchScanExport
import com.elxvro.scan.batch.BatchScanSession
import com.elxvro.scan.scanner.ScannerController
import com.elxvro.scan.ui.components.ReferenceHeader
import com.elxvro.scan.ui.components.ScanOverlay
import com.elxvro.scan.ui.theme.ScanTokens
import com.google.mlkit.vision.barcode.common.Barcode

@Composable
fun BatchScanScreen(
    store: ScanStore,
    prefs: AppPrefs,
    onBack: () -> Unit
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
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var torch by remember { mutableStateOf(settings.defaultTorch) }
    var zoom by remember { mutableFloatStateOf(1f) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var session by remember { mutableStateOf(BatchScanSession()) }

    fun toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    fun handleCode(code: Barcode) {
        val raw = code.rawValue?.trim().orEmpty()
        if (raw.isBlank()) return
        val format = BarcodePresentation.formatName(code.format)
        val kind = if (code.format == Barcode.FORMAT_QR_CODE) "QR" else "Barkod"

        if (session.contains(raw, format)) {
            toast("Bu kod listede zaten var")
            return
        }

        val entry = BatchScanEntry(
            value = raw,
            format = format,
            kind = kind,
            time = System.currentTimeMillis()
        )
        session = session.add(entry)
        store.add(raw, format, kind)
        feedback.play(settings)
    }

    fun exportEntries(): List<BatchScanEntry> {
        val selected = session.selectedEntries()
        return if (selected.isNotEmpty()) selected else session.entries
    }

    fun shareText(fileName: String, mime: String, content: String) {
        runCatching {
            context.startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = mime
                        putExtra(Intent.EXTRA_SUBJECT, fileName)
                        putExtra(Intent.EXTRA_TEXT, content)
                    },
                    "Dışa aktar"
                )
            )
        }.onFailure { toast("Dışa aktarma açılamadı") }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionGranted = it }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            controller.scanUri(
                uri = uri,
                onResult = ::handleCode,
                onEmpty = { toast("Bir görselde kod bulunamadı") },
                onError = { toast("Bir görsel okunamadı") }
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
        ReferenceHeader(title = "Toplu Tarama", onBack = onBack)

        if (!permissionGranted) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ScanTokens.Paper)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Kamera izni gerekli", color = ScanTokens.Text, fontWeight = FontWeight.SemiBold)
                BatchTextButton("Kamera İzni Ver") {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
                BatchTextButton("Galeriden Çoklu Oku") {
                    galleryLauncher.launch("image/*")
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.54f)
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
                                    onError = { toast("Kamera başlatılamadı") }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                ScanOverlay()

                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                        .background(Color.Black.copy(alpha = 0.68f), RoundedCornerShape(18.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.24f), RoundedCornerShape(18.dp))
                        .padding(horizontal = 13.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = session.entries.size.toString(),
                        color = ScanTokens.Blue,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " benzersiz kod",
                        color = Color.White,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BatchCameraAction(Icons.Outlined.Image, "Galeri") {
                        galleryLauncher.launch("image/*")
                    }
                    BatchZoomPill(
                        zoom = zoom,
                        onOne = { zoom = controller.setZoomRatio(1f) },
                        onTwo = { zoom = controller.setZoomRatio(2f) }
                    )
                    BatchCameraAction(
                        icon = if (torch) Icons.Outlined.FlashlightOn else Icons.Outlined.FlashlightOff,
                        label = "Fener",
                        active = torch
                    ) {
                        if (!controller.hasFlash()) toast("Bu cihazda flaş bulunamadı")
                        else torch = controller.toggleTorch()
                    }
                }
            }
        }

        BatchResultPanel(
            modifier = Modifier.weight(0.46f),
            session = session,
            onToggle = { session = session.toggleSelection(it) },
            onSelectAll = {
                session = if (session.selectedIds.size == session.entries.size && session.entries.isNotEmpty()) {
                    session.clearSelection()
                } else {
                    session.selectAll()
                }
            },
            onDeleteSelected = {
                session = session.removeSelected()
            },
            onClear = {
                session = session.clear()
            },
            onCsv = {
                val items = exportEntries()
                if (items.isEmpty()) toast("Dışa aktarılacak kod yok")
                else shareText("ELXVRO-Toplu-Tarama.csv", "text/csv", BatchScanExport.toCsv(items))
            },
            onJson = {
                val items = exportEntries()
                if (items.isEmpty()) toast("Dışa aktarılacak kod yok")
                else shareText("ELXVRO-Toplu-Tarama.json", "application/json", BatchScanExport.toJson(items))
            }
        )
    }
}

@Composable
private fun BatchResultPanel(
    modifier: Modifier = Modifier,
    session: BatchScanSession,
    onToggle: (String) -> Unit,
    onSelectAll: () -> Unit,
    onDeleteSelected: () -> Unit,
    onClear: () -> Unit,
    onCsv: () -> Unit,
    onJson: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                ScanTokens.Paper,
                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Oturum Listesi",
                    color = ScanTokens.Text,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    if (session.selectedIds.isEmpty()) {
                        session.entries.size.toString() + " kod"
                    } else {
                        session.selectedIds.size.toString() + " seçili"
                    },
                    color = ScanTokens.Muted
                )
            }

            BatchCompactButton(
                if (session.selectedIds.size == session.entries.size && session.entries.isNotEmpty()) {
                    "Seçimi Kaldır"
                } else {
                    "Tümünü Seç"
                },
                onSelectAll
            )
        }

        if (session.entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Kodları art arda okut. Aynı kod bu oturumda ikinci kez eklenmez.",
                    color = ScanTokens.Muted
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(top = 8.dp)
            ) {
                session.entries.forEach { entry ->
                    val selected = entry.id in session.selectedIds
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 7.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                if (selected) ScanTokens.Blue else ScanTokens.Divider,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onToggle(entry.id) }
                            .padding(11.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (selected) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (selected) ScanTokens.Blue else ScanTokens.Muted
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 10.dp)
                        ) {
                            Text(
                                entry.value,
                                color = ScanTokens.Text,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                entry.format + " • " + entry.kind,
                                color = ScanTokens.Muted
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            BatchActionButton(Icons.Outlined.FileDownload, "CSV", Modifier.weight(1f), onCsv)
            BatchActionButton(Icons.Outlined.FileDownload, "JSON", Modifier.weight(1f), onJson)
            if (session.selectedIds.isNotEmpty()) {
                BatchActionButton(Icons.Outlined.Delete, "Sil", Modifier.weight(1f), onDeleteSelected)
            } else if (session.entries.isNotEmpty()) {
                BatchCompactButton("Temizle", onClear, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BatchCameraAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean = false,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    if (active) ScanTokens.Blue.copy(alpha = 0.24f) else Color.Black.copy(alpha = 0.58f),
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
            Icon(icon, contentDescription = label, tint = Color.White)
        }
        Text(label, color = Color.White, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun BatchZoomPill(
    zoom: Float,
    onOne: () -> Unit,
    onTwo: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.64f), RoundedCornerShape(18.dp))
            .border(1.dp, Color.White.copy(alpha = 0.34f), RoundedCornerShape(18.dp))
            .padding(3.dp)
    ) {
        BatchZoomChoice("1x", zoom < 1.5f, onOne)
        BatchZoomChoice("2x", zoom >= 1.5f, onTwo)
    }
}

@Composable
private fun BatchZoomChoice(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                if (selected) Color.White.copy(alpha = 0.16f) else Color.Transparent,
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 6.dp)
    ) {
        Text(label, color = Color.White)
    }
}

@Composable
private fun BatchActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(44.dp)
            .background(Color.White, RoundedCornerShape(11.dp))
            .border(1.dp, ScanTokens.Divider, RoundedCornerShape(11.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = ScanTokens.Blue, modifier = Modifier.size(18.dp))
        Text(label, color = ScanTokens.Text, modifier = Modifier.padding(start = 5.dp))
    }
}

@Composable
private fun BatchCompactButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, ScanTokens.Divider, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = ScanTokens.Text)
    }
}

@Composable
private fun BatchTextButton(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .height(48.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, ScanTokens.Divider, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = ScanTokens.Text, fontWeight = FontWeight.Medium)
    }
}
