package com.applocklara.applocklara.features.intruder.ui

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.applocklara.applocklara.R
import com.applocklara.applocklara.features.intruder.IntruderSelfieLog
import com.applocklara.applocklara.features.intruder.IntruderSelfieManager
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntruderLogsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    var logs by remember { mutableStateOf(IntruderSelfieManager.loadLogs(context)) }
    var selectedPhotoPath by remember { mutableStateOf<String?>(null) }
    var showClearAllConfirm by remember { mutableStateOf(false) }
    var logToDelete by remember { mutableStateOf<IntruderSelfieLog?>(null) }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.intruder_selfie_logs_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.testTag("intruder_logs_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.settings_screen_back_cd)
                        )
                    }
                },
                actions = {
                    if (logs.isNotEmpty()) {
                        IconButton(
                            onClick = { showClearAllConfirm = true },
                            modifier = Modifier.testTag("intruder_logs_clear_all_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = stringResource(R.string.intruder_selfie_logs_clear_all)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (logs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NoPhotography,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.intruder_selfie_logs_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(logs, key = { it.id }) { log ->
                        IntruderLogCard(
                            log = log,
                            onPhotoClick = { selectedPhotoPath = log.photoPath },
                            onDeleteClick = { logToDelete = log }
                        )
                    }
                }
            }
        }
    }

    // Full Screen Photo Viewer Dialog
    selectedPhotoPath?.let { path ->
        Dialog(
            onDismissRequest = { selectedPhotoPath = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { selectedPhotoPath = null },
                contentAlignment = Alignment.Center
            ) {
                val bitmap = remember(path) {
                    try {
                        BitmapFactory.decodeFile(path)?.asImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Full Screen Intruder Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = "Failed to load photo",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    // Clear All Confirmation Dialog
    if (showClearAllConfirm) {
        AlertDialog(
            onDismissRequest = { showClearAllConfirm = false },
            title = { Text(stringResource(R.string.intruder_selfie_logs_clear_all)) },
            text = { Text(stringResource(R.string.intruder_selfie_logs_clear_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        IntruderSelfieManager.clearAllLogs(context)
                        logs = emptyList()
                        showClearAllConfirm = false
                        Toast.makeText(context, "All logs cleared", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(stringResource(R.string.intruder_selfie_logs_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllConfirm = false }) {
                    Text(stringResource(R.string.intruder_selfie_logs_cancel))
                }
            }
        )
    }

    // Delete Single Log Confirmation Dialog
    logToDelete?.let { log ->
        AlertDialog(
            onDismissRequest = { logToDelete = null },
            title = { Text(stringResource(R.string.intruder_selfie_logs_delete)) },
            text = { Text(stringResource(R.string.intruder_selfie_logs_delete_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        IntruderSelfieManager.deleteLog(context, log.id)
                        logs = IntruderSelfieManager.loadLogs(context)
                        logToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.intruder_selfie_logs_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { logToDelete = null }) {
                    Text(stringResource(R.string.intruder_selfie_logs_cancel))
                }
            }
        )
    }
}

@Composable
fun IntruderLogCard(
    log: IntruderSelfieLog,
    onPhotoClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("intruder_log_card_${log.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo Thumbnail
            val bitmap = remember(log.photoPath) {
                try {
                    val file = File(log.photoPath)
                    if (file.exists()) {
                        BitmapFactory.decodeFile(log.photoPath)?.asImageBitmap()
                    } else null
                } catch (e: Exception) {
                    null
                }
            }

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onPhotoClick() },
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Intruder Photo Thumbnail",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.NoPhotography,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.intruder_selfie_logs_app_label, log.appName),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.intruder_selfie_logs_date_label, log.dateTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.intruder_selfie_logs_failed_attempts, log.failedAttemptCount),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Delete Button
            IconButton(
                onClick = { onDeleteClick() },
                modifier = Modifier
                    .testTag("delete_log_button_${log.id}")
                    .minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.intruder_selfie_logs_delete),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
            }
        }
    }
}
