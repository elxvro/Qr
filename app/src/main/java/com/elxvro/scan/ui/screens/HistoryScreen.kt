package com.elxvro.scan.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elxvro.scan.HistoryExport
import com.elxvro.scan.HistoryFilter
import com.elxvro.scan.HistoryLogic
import com.elxvro.scan.ScanItem
import com.elxvro.scan.ScanStore
import com.elxvro.scan.SmartAction
import com.elxvro.scan.SmartActionExecutor
import com.elxvro.scan.SmartActionResolver
import com.elxvro.scan.SmartActionType
import com.elxvro.scan.ui.components.ReferenceChip
import com.elxvro.scan.ui.components.ReferenceHeader
import com.elxvro.scan.ui.theme.ScanTokens
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(store: ScanStore, onBack: () -> Unit) {
    val context = LocalContext.current
    var refresh by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(HistoryFilter.ALL) }
    var newestFirst by remember { mutableStateOf(true) }
    var selectMode by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(emptySet<String>()) }
    var menuExpanded by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    val all = remember(refresh) { store.list() }
    val visible = remember(all, filter, query, newestFirst) {
        HistoryLogic.searchAndFilter(all, filter, query, newestFirst)
    }

    fun toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    fun refreshList() { refresh++ }
    fun clearSelection() {
        selected = emptySet()
        selectMode = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScanTokens.Ink)
    ) {
        ReferenceHeader(
            title = "Geçmiş",
            onBack = onBack,
            trailing = {
                Text(
                    text = if (selectMode) "Bitti" else "Seç",
                    color = ScanTokens.BlueBright,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .combinedClickable(
                            onClick = {
                                selectMode = !selectMode
                                if (!selectMode) selected = emptySet()
                            },
                            onLongClick = {}
                        )
                        .padding(horizontal = 8.dp, vertical = 10.dp)
                )
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Diğer", tint = ScanTokens.TextOnDark)
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        containerColor = ScanTokens.Card
                    ) {
                        DropdownMenuItem(
                            text = { Text("CSV dışa aktar", color = ScanTokens.Text) },
                            leadingIcon = { Icon(Icons.Outlined.FileUpload, null, tint = ScanTokens.Blue) },
                            onClick = {
                                menuExpanded = false
                                shareExport(context, "ELXVRO Scan Geçmiş.csv", HistoryExport.toCsv(all))
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("JSON dışa aktar", color = ScanTokens.Text) },
                            leadingIcon = { Icon(Icons.Outlined.FileUpload, null, tint = ScanTokens.Blue) },
                            onClick = {
                                menuExpanded = false
                                shareExport(context, "ELXVRO Scan Geçmiş.json", HistoryExport.toJson(all))
                            }
                        )
                    }
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            listOf(
                "Tümü" to HistoryFilter.ALL,
                "QR Kod" to HistoryFilter.QR,
                "Barkod" to HistoryFilter.BARCODE,
                "Favoriler" to HistoryFilter.FAVORITES
            ).forEach { (label, value) ->
                ReferenceChip(label, filter == value) { filter = value }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    ScanTokens.Paper,
                    RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .padding(top = 12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                placeholder = { Text("Geçmişte ara", color = ScanTokens.Muted) },
                leadingIcon = { Icon(Icons.Outlined.Search, null, tint = ScanTokens.Muted) },
                trailingIcon = {
                    if (query.isNotBlank()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Outlined.Clear, contentDescription = "Temizle", tint = ScanTokens.Muted)
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = ScanTokens.Blue,
                    unfocusedBorderColor = ScanTokens.Divider,
                    focusedTextColor = ScanTokens.Text,
                    unfocusedTextColor = ScanTokens.Text,
                    cursorColor = ScanTokens.Blue
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${visible.size} kayıt • ${all.count { it.favorite }} favori",
                    color = ScanTokens.Muted,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .border(1.dp, ScanTokens.Divider, RoundedCornerShape(10.dp))
                        .combinedClickable(
                            onClick = { newestFirst = !newestFirst },
                            onLongClick = {}
                        )
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Sort, null, tint = ScanTokens.Muted, modifier = Modifier.size(17.dp))
                    Text(
                        if (newestFirst) "Yeni" else "Eski",
                        color = ScanTokens.Text,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            if (selectMode) {
                SelectionToolbar(
                    count = selected.size,
                    onFavorite = {
                        store.setFavorite(selected, true)
                        refreshList()
                        clearSelection()
                    },
                    onShare = {
                        val text = all.filter { it.id in selected }.joinToString("\n\n") { it.value }
                        if (text.isNotBlank()) SmartActionExecutor.share(context, text)
                        clearSelection()
                    },
                    onDelete = { if (selected.isNotEmpty()) confirmDelete = true }
                )
            }

            if (visible.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.QrCodeScanner, null, tint = ScanTokens.Muted, modifier = Modifier.size(38.dp))
                        Text(
                            if (query.isBlank()) "Henüz kayıt yok" else "Eşleşen kayıt yok",
                            color = ScanTokens.Muted,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    items(visible, key = { it.id }) { item ->
                        HistoryRow(
                            item = item,
                            selected = item.id in selected,
                            selectMode = selectMode,
                            onClick = {
                                if (selectMode) {
                                    selected = toggle(selected, item.id)
                                } else {
                                    runCatching {
                                        SmartActionExecutor.execute(context, SmartActionResolver.resolve(item.value, item.kind))
                                    }.onFailure { toast("İşlem açılamadı") }
                                }
                            },
                            onLongClick = {
                                selectMode = true
                                selected = toggle(selected, item.id)
                            },
                            onFavorite = {
                                store.toggleFavorite(item.id)
                                refreshList()
                            }
                        )
                    }
                    item { Spacer(Modifier.height(14.dp)) }
                }
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            containerColor = ScanTokens.Card,
            shape = RoundedCornerShape(18.dp),
            title = { Text("Seçili kayıtları sil", color = ScanTokens.Text) },
            text = { Text("${selected.size} kayıt kalıcı olarak silinsin mi?", color = ScanTokens.Muted) },
            confirmButton = {
                TextButton(onClick = {
                    store.delete(selected)
                    confirmDelete = false
                    refreshList()
                    clearSelection()
                }) { Text("Sil", color = ScanTokens.Danger) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("Vazgeç", color = ScanTokens.Blue) }
            }
        )
    }
}

@Composable
private fun SelectionToolbar(count: Int, onFavorite: () -> Unit, onShare: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .background(ScanTokens.Blue.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .border(1.dp, ScanTokens.Blue.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
            .padding(start = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$count seçili", color = ScanTokens.Blue, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        IconButton(onClick = onFavorite, enabled = count > 0) { Icon(Icons.Outlined.Star, "Favorile", tint = ScanTokens.Warning) }
        IconButton(onClick = onShare, enabled = count > 0) { Icon(Icons.Outlined.Share, "Paylaş", tint = ScanTokens.Blue) }
        IconButton(onClick = onDelete, enabled = count > 0) { Icon(Icons.Outlined.DeleteOutline, "Sil", tint = ScanTokens.Danger) }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryRow(
    item: ScanItem,
    selected: Boolean,
    selectMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onFavorite: () -> Unit
) {
    val action = SmartActionResolver.resolve(item.value, item.kind)
    val icon = iconFor(action)
    val iconColor = colorFor(action)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selected) ScanTokens.Blue.copy(alpha = 0.08f) else Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, if (selected) ScanTokens.Blue.copy(alpha = 0.35f) else ScanTokens.Divider, RoundedCornerShape(12.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconColor.copy(alpha = 0.10f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (selected) Icons.Outlined.CheckCircle else icon,
                contentDescription = null,
                tint = if (selected) ScanTokens.Blue else iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
            Text(
                item.value,
                color = ScanTokens.Text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "${item.format} • ${date(item.time)}",
                color = ScanTokens.Muted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
        if (!selectMode) {
            IconButton(onClick = onFavorite, modifier = Modifier.size(40.dp)) {
                Icon(
                    if (item.favorite) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Favori",
                    tint = if (item.favorite) ScanTokens.Warning else ScanTokens.Muted
                )
            }
        }
    }
}

private fun iconFor(action: SmartAction): ImageVector = when (action.type) {
    SmartActionType.OPEN_URL -> Icons.Outlined.Link
    SmartActionType.DIAL -> Icons.Outlined.Phone
    SmartActionType.EMAIL -> Icons.Outlined.Email
    SmartActionType.SMS -> Icons.Outlined.Sms
    SmartActionType.MAP -> Icons.Outlined.LocationOn
    SmartActionType.WIFI -> Icons.Outlined.Wifi
    SmartActionType.CONTACT -> Icons.Outlined.Description
    SmartActionType.SEARCH_PRODUCT -> Icons.Outlined.Inventory2
    SmartActionType.SHARE_TEXT -> Icons.Outlined.Description
}

private fun colorFor(action: SmartAction): Color = when (action.type) {
    SmartActionType.DIAL, SmartActionType.SMS -> ScanTokens.Success
    SmartActionType.EMAIL -> ScanTokens.Danger
    SmartActionType.WIFI, SmartActionType.OPEN_URL, SmartActionType.MAP -> ScanTokens.Blue
    SmartActionType.CONTACT -> ScanTokens.Warning
    SmartActionType.SEARCH_PRODUCT, SmartActionType.SHARE_TEXT -> ScanTokens.Muted
}

private fun date(time: Long): String = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(time))

private fun toggle(current: Set<String>, id: String): Set<String> = if (id in current) current - id else current + id

private fun shareExport(context: Context, title: String, data: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, data)
    }
    context.startActivity(Intent.createChooser(intent, title))
}
