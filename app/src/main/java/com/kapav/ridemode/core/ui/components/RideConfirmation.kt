package com.kapav.ridemode.core.ui.components

import android.os.SystemClock
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.bold.MinusCircle
import com.adamglin.phosphoricons.fill.MinusCircle
import com.adamglin.phosphoricons.fill.XCircle
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.kapav.ridemode.core.util.confirmationFractionRemaining
import com.kapav.ridemode.core.util.confirmationSecondsRemaining

/** Call dialogs take window space rather than inheriting a small widget's bounds. */
@Composable
fun RideConfirmationDialog(title: String, detail: String, deadlineMillis: Long,
    confirmLabel: String, confirmIcon: ImageVector, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.padding(RideUiTokens.screenPadding).widthIn(max = RideUiTokens.confirmationMaxWidth)
            .fillMaxWidth(), shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surface) {
            RideConfirmation(title, detail, deadlineMillis, confirmLabel, confirmIcon,
                onDismiss = onDismiss, onConfirm = onConfirm,
                modifier = Modifier.padding(RideUiTokens.contentGap))
        }
    }
}

/** Expiry always cancels; action callbacks also check the deadline to reject delayed taps. */
@Composable
fun RideConfirmation(title: String, detail: String, deadlineMillis: Long,
    confirmLabel: String, confirmIcon: ImageVector, onDismiss: () -> Unit, onConfirm: () -> Unit,
    modifier: Modifier = Modifier, dismissLabel: String = "Dismiss", destructive: Boolean = false) {
    var resolved by remember(deadlineMillis) { mutableStateOf(false) }
    val dismiss by rememberUpdatedState(onDismiss)
    LaunchedEffect(deadlineMillis) {
        val remaining = deadlineMillis - SystemClock.elapsedRealtime()
        if (remaining > 0) kotlinx.coroutines.delay(remaining)
        if (!resolved) { resolved = true; dismiss() }
    }
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val roomy = maxWidth >= RideUiTokens.confirmationWideWidth
        val buttonHeight = if (roomy) RideUiTokens.confirmationLargeHeight else RideUiTokens.confirmationCompactHeight
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
            Text(title, style = if (roomy) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (detail.isNotBlank()) Text(detail, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                CountdownDismissButton(deadlineMillis, dismissLabel, roomy,
                    Modifier.weight(1f).height(buttonHeight)) {
                    if (!resolved) { resolved = true; onDismiss() }
                }
                Surface(onClick = {
                    if (!resolved) {
                        resolved = true
                        if (SystemClock.elapsedRealtime() < deadlineMillis) onConfirm() else onDismiss()
                    }
                }, modifier = Modifier.weight(1f).height(buttonHeight), shape = RoundedCornerShape(50),
                    color = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = if (destructive) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary) {
                    ConfirmationButtonLabel(confirmLabel, confirmIcon, roomy)
                }
            }
        }
    }
}

@Composable
private fun CountdownDismissButton(deadline: Long, label: String, roomy: Boolean,
    modifier: Modifier, onClick: () -> Unit) {
    val now = remember(deadline) { mutableLongStateOf(SystemClock.elapsedRealtime()) }
    LaunchedEffect(deadline) {
        while (now.longValue < deadline) withFrameNanos { now.longValue = SystemClock.elapsedRealtime() }
    }
    val fill = MaterialTheme.colorScheme.secondaryContainer
    val seconds by remember(deadline) { derivedStateOf { confirmationSecondsRemaining(deadline, now.longValue) } }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Surface(onClick = onClick, modifier = modifier.semantics {
        stateDescription = "Dismisses automatically in ${seconds} seconds"
    }, shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.surfaceContainerLow,
        contentColor = MaterialTheme.colorScheme.onSurface) {
        Box {
            Canvas(Modifier.matchParentSize()) {
                val width = size.width * confirmationFractionRemaining(deadline, now.longValue)
                drawRect(fill, topLeft = androidx.compose.ui.geometry.Offset(if (rtl) size.width - width else 0f, 0f),
                    size = androidx.compose.ui.geometry.Size(width, size.height))
            }
            ConfirmationButtonLabel("$label · ${seconds}s", PhosphorIcons.Bold.MinusCircle, roomy)
        }
    }
}

@Composable
private fun ConfirmationButtonLabel(label: String, icon: ImageVector, roomy: Boolean) {
    val style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    if (roomy) Row(Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding),
        horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(RideUiTokens.iconSize))
        Text(label, style = style, textAlign = TextAlign.Center)
    } else Column(Modifier.fillMaxSize().padding(RideUiTokens.confirmationLabelInset),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, Modifier.size(RideUiTokens.iconSize))
        Text(label, style = style, textAlign = TextAlign.Center)
    }
}
