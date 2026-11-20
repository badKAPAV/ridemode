package com.kapav.ridemode.feature.widgets

import android.Manifest
import android.os.SystemClock
import com.kapav.ridemode.core.ui.components.RideConfirmationDialog
import com.kapav.ridemode.core.util.CONFIRMATION_TIMEOUT_SECONDS
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.Phone
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.telephony.PhoneNumberUtils
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.fillMaxWidth
import com.kapav.ridemode.core.model.SosFavorite
import com.kapav.ridemode.core.model.WidgetConfigs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import kotlin.math.floor

/** Number of square shortcuts that can fit without shrinking below the design minimum. */
internal data class SosGrid(val columns: Int, val rows: Int, val visible: Int, val tileDp: Float)

internal fun sosGrid(available: Int, widthDp: Float, heightDp: Float,
    minTileDp: Float, gapDp: Float): SosGrid {
    if (available <= 0 || widthDp <= 0f || heightDp <= 0f) return SosGrid(0, 0, 0, 0f)
    val count = minOf(available, RideUiTokens.sosMaxVisible)
    val effectiveMin = minOf(minTileDp, heightDp)
    val maxColumns = floor((widthDp + gapDp) / (effectiveMin + gapDp)).toInt().coerceAtLeast(1)
    val maxRows = floor((heightDp + gapDp) / (effectiveMin + gapDp)).toInt().coerceAtLeast(1)
    val candidates = (1..minOf(count, maxColumns)).flatMap { columns ->
        (1..minOf(count, maxRows)).map { rows ->
            val visible = minOf(count, columns * rows)
            val side = minOf((widthDp - gapDp * (columns - 1)) / columns,
                (heightDp - gapDp * (rows - 1)) / rows,
                RideUiTokens.sosMaxTileSide.value)
            SosGrid(columns, rows, visible, side)
        }
    }
    return candidates.filter { it.tileDp >= effectiveMin || (it.columns == 1 && it.rows == 1) }
        .maxWithOrNull(compareBy<SosGrid> { it.visible }.thenBy { it.tileDp })
        ?: SosGrid(1, 1, 1, minOf(widthDp, heightDp))
}

/** Per-widget saved contacts; editor previews never launch a call. */
@Composable
internal fun SosFavoritesWidget(modifier: Modifier = Modifier, widgetId: String = "", configJson: String = "{}", editor: Boolean = false) {
    val config = remember(configJson) { WidgetConfigs.sos(configJson) }
    val context = LocalContext.current
    var target by remember(widgetId, configJson) { mutableStateOf<SosFavorite?>(null) }
    var pendingCall by remember(widgetId) { mutableStateOf<String?>(null) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val number = pendingCall
        pendingCall = null
        if (number != null) openCall(context, number, granted)
    }
    target?.let { contact ->
        val deadline = remember(contact.id) { SystemClock.elapsedRealtime() + CONFIRMATION_TIMEOUT_SECONDS * 1_000L }
        RideConfirmationDialog(title = "Call ${contact.name}?", detail = contact.number,
            deadlineMillis = deadline, confirmLabel = if (config.directCall) "Call" else "Open dialer",
            confirmIcon = PhosphorIcons.Fill.Phone, onDismiss = { target = null }, onConfirm = {
                target = null
                val emergency = runCatching { PhoneNumberUtils.isEmergencyNumber(contact.number) }.getOrDefault(true)
                if (!config.directCall || emergency) openCall(context, contact.number, false)
                else if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                    openCall(context, contact.number, true)
                else {
                    pendingCall = contact.number
                    permission.launch(Manifest.permission.CALL_PHONE)
                }
            })
    }
    BoxWithConstraints(modifier) {
        if (config.contacts.isEmpty()) {
            Surface(Modifier.fillMaxSize(), shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer) {
                Box(Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding), contentAlignment = Alignment.Center) {
                    AdaptiveWidgetText("SOS favorites\nChoose contacts in the editor’s Widget settings",
                        MaterialTheme.typography.titleSmall, MaterialTheme.colorScheme.onPrimaryContainer,
                        Modifier.fillMaxSize(), textAlign = androidx.compose.ui.text.style.TextAlign.Center, verticalCentered = true)
                }
            }
            return@BoxWithConstraints
        }
        val grid = sosGrid(config.contacts.size, maxWidth.value, maxHeight.value,
            RideUiTokens.sosMinTileWidth.value, RideUiTokens.sosTileGap.value)
        if (grid.visible == 0) return@BoxWithConstraints
        val side = grid.tileDp.dp
        val shape = RoundedCornerShape(side * RideUiTokens.sosTileCornerFraction)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(RideUiTokens.sosTileGap, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally) {
            config.contacts.chunked(grid.columns.coerceAtLeast(1)).forEach { group ->
                Row(horizontalArrangement = Arrangement.spacedBy(RideUiTokens.sosTileGap), verticalAlignment = Alignment.CenterVertically) {
                    group.forEach { contact ->
                        key(contact.id) {
                        Surface(onClick = { target = contact }, enabled = !editor,
                            modifier = Modifier.size(side).semantics { contentDescription = "SOS favorite ${contact.name}; ${contact.number}" },
                            shape = shape, color = MaterialTheme.colorScheme.primary) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                SosAvatar(contact.photo, contact.initials, Modifier.fillMaxSize())
                                Surface(Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = RideUiTokens.sosNameSurfaceOpacity)) {
                                    Text(contact.name, modifier = Modifier.padding(RideUiTokens.batteryVerticalTextInset),
                                        style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                }
                            }
                        }
                    }
                    }
                }
            }
        }
    }
}

@Composable
internal fun SosAvatar(photo: String?, initials: String, modifier: Modifier = Modifier) {
    val bitmap by produceState<android.graphics.Bitmap?>(null, photo) {
        value = null
        value = if (photo == null) null else withContext(Dispatchers.IO) {
            runCatching { val bytes = Base64.decode(photo, Base64.NO_WRAP)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size) }.getOrNull()
        }
    }
    val image = bitmap
    if (image != null) Image(image.asImageBitmap(), contentDescription = null, modifier = modifier, contentScale = ContentScale.Crop)
    else Box(modifier, contentAlignment = Alignment.Center) {
        AdaptiveWidgetText(initials, MaterialTheme.typography.headlineLarge, MaterialTheme.colorScheme.onPrimary,
            Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding), maxSize = RideUiTokens.sosInitialsMaxSp.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center, verticalCentered = true)
    }
}
