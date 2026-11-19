package com.kapav.ridemode.feature.widgets

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.CallLog
import android.telephony.PhoneNumberUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.ContextCompat
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.Phone
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.kapav.ridemode.data.CallHistoryAccess
import com.kapav.ridemode.data.CallHistoryState
import com.kapav.ridemode.data.RecentCall
import java.text.SimpleDateFormat
import java.util.Date

/** Five system Recents inside one child scroll view; direct calls require confirmation. */
@Composable
fun CallHistoryWidget(state: CallHistoryState, editor: Boolean, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var confirmation by remember { mutableStateOf<String?>(null) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val number = confirmation
        confirmation = null
        if (number != null) openCall(context, number, granted)
    }
    if (confirmation != null) {
        AlertDialog(onDismissRequest = { confirmation = null },
            title = { Text("Call this number?") },
            text = { Text(confirmation.orEmpty()) },
            confirmButton = { TextButton(onClick = {
                val number = confirmation ?: return@TextButton
                if (runCatching { PhoneNumberUtils.isEmergencyNumber(number) }.getOrDefault(false)) {
                    confirmation = null
                    openCall(context, number, false)
                } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) ==
                    PackageManager.PERMISSION_GRANTED) {
                    confirmation = null
                    openCall(context, number, true)
                } else permission.launch(Manifest.permission.CALL_PHONE)
            }) { Text("Call") } },
            dismissButton = { TextButton(onClick = { confirmation = null }) { Text("Cancel") } })
    }
    Surface(modifier, color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.medium) {
        Column(Modifier.fillMaxSize().padding(RideUiTokens.widgetCardInset),
            verticalArrangement = Arrangement.spacedBy(RideUiTokens.plannedWidgetItemGap)) {
            Text("Recent Calls", style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1)
            Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(RideUiTokens.plannedWidgetItemGap)) {
                if (state.calls.isEmpty()) {
                    Text(when (state.access) {
                        CallHistoryAccess.REQUIRED -> "Call history needs access. Enable it in Settings."
                        CallHistoryAccess.LOADING -> "Loading recent calls"
                        CallHistoryAccess.READY -> "No recent calls"
                        CallHistoryAccess.UNAVAILABLE -> "Call history unavailable on this device"
                    }, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer)
                } else state.calls.forEach { call ->
                    CallRow(call, editor) { number -> confirmation = number }
                }
            }
        }
    }
}

@Composable
private fun CallRow(call: RecentCall, editor: Boolean, onCall: (String) -> Unit) {
    val locale = LocalLocale.current.platformLocale
    val formatter = remember(locale) { SimpleDateFormat("d MMM, HH:mm", locale) }
    val displayName = call.name?.takeIf { it.isNotBlank() } ?: call.number ?: "Private number"
    val type = when (call.type) {
        CallLog.Calls.INCOMING_TYPE -> "Incoming"
        CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
        CallLog.Calls.MISSED_TYPE -> "Missed"
        CallLog.Calls.REJECTED_TYPE -> "Declined"
        CallLog.Calls.BLOCKED_TYPE -> "Blocked"
        else -> "Call"
    }
    Row(Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(RideUiTokens.plannedWidgetItemGap),
        verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondary) {
            Box(Modifier.size(RideUiTokens.plannedWidgetAvatar), contentAlignment = Alignment.Center) {
                Text(displayName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondary)
            }
        }
        Column(Modifier.weight(1f)) {
            Text(displayName, style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${call.number ?: "Private"} · $type · ${formatter.format(Date(call.timestampMs))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        val number = call.number
        if (number != null && !editor) {
            IconButton(onClick = { onCall(number) }) {
                Icon(PhosphorIcons.Fill.Phone, contentDescription = "Call $displayName",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    }
}

private fun openCall(context: android.content.Context, number: String, direct: Boolean) {
    val uri = Uri.fromParts("tel", number, null)
    val emergency = runCatching { PhoneNumberUtils.isEmergencyNumber(number) }.getOrDefault(false)
    val action = if (direct && !emergency) Intent.ACTION_CALL else Intent.ACTION_DIAL
    runCatching { context.startActivity(Intent(action, uri)) }
        .onFailure { if (action == Intent.ACTION_CALL) runCatching {
            context.startActivity(Intent(Intent.ACTION_DIAL, uri))
        } }
}
