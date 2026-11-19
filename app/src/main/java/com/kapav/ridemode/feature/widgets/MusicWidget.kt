package com.kapav.ridemode.feature.widgets

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.SystemClock
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.MusicNotes
import com.adamglin.phosphoricons.fill.Pause
import com.adamglin.phosphoricons.fill.Play
import com.adamglin.phosphoricons.fill.SkipBack
import com.adamglin.phosphoricons.fill.SkipForward
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.kapav.ridemode.data.MusicAction
import com.kapav.ridemode.data.MusicAvailability
import com.kapav.ridemode.data.MusicSessionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/** Shows the highest-priority active session; the editor remains noninteractive. */
@Composable
fun MusicWidget(editor: Boolean, music: MusicSessionState,
    onAction: (MusicAction) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val artwork by produceState<Bitmap?>(initialValue = music.artwork,
        music.artwork, music.artworkUri) {
        value = music.artwork ?: withContext(Dispatchers.IO) {
            val uri = music.artworkUri?.let { Uri.parse(it) } ?: return@withContext null
            if (uri.scheme !in setOf("content", "file", "android.resource")) return@withContext null
            runCatching {
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
                val sample = maxOf(bounds.outWidth, bounds.outHeight).coerceAtLeast(1) / 512
                val options = BitmapFactory.Options().apply { inSampleSize = sample.coerceAtLeast(1) }
                context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it, null, options)
                }
            }.getOrNull()
        }
    }
    var clock by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }
    LaunchedEffect(music.positionUpdatedAtMs, music.playing) {
        clock = SystemClock.elapsedRealtime()
        while (music.playing) { delay(1_000); clock = SystemClock.elapsedRealtime() }
    }
    val title = when (music.availability) {
        MusicAvailability.READY -> music.title?.takeIf { it.isNotBlank() } ?: "Unknown track"
        MusicAvailability.ACCESS_REQUIRED -> "Music access needed"
        MusicAvailability.NO_SESSION -> "No active music"
        MusicAvailability.ERROR -> "Music unavailable"
    }
    val artist = when (music.availability) {
        MusicAvailability.READY -> music.artist?.takeIf { it.isNotBlank() }
            ?: music.packageName.orEmpty()
        MusicAvailability.ACCESS_REQUIRED -> "Enable in Settings"
        MusicAvailability.NO_SESSION -> "Open a music app"
        MusicAvailability.ERROR -> "Try again in Settings"
    }
    BoxWithConstraints(modifier) {
        val usableHeight = maxHeight - RideUiTokens.widgetPadding
        val headerHeight = usableHeight * RideUiTokens.musicHeaderHeightFraction
        val metadataLineHeight = minOf(headerHeight * RideUiTokens.musicMetadataLineFraction,
            RideUiTokens.musicMetadataLineMax)
        Column(Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
            Box(Modifier.fillMaxWidth().height(headerHeight)) {
                Surface(Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary.copy(
                        alpha = RideUiTokens.musicDetailsContainerOpacity),
                    shape = RoundedCornerShape(RideUiTokens.rideButtonCorner)) {
                    Box(Modifier.fillMaxSize().padding(
                        start = headerHeight + RideUiTokens.contentGap,
                        end = RideUiTokens.contentGap), contentAlignment = Alignment.CenterStart) {
                        Column(Modifier.fillMaxWidth().wrapContentHeight(),
                            verticalArrangement = Arrangement.spacedBy(RideUiTokens.musicMetadataGap)) {
                            AdaptiveWidgetText(title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth().height(metadataLineHeight),
                                maxSize = RideUiTokens.musicTitleMaxSp.sp,
                                verticalCentered = true)
                            AdaptiveWidgetText(artist,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth().height(metadataLineHeight),
                                maxSize = RideUiTokens.musicArtistMaxSp.sp,
                                verticalCentered = true)
                        }
                    }
                }
                val duration = music.durationMs
                if (music.availability == MusicAvailability.READY && duration != null) {
                    val elapsed = music.positionAt(clock)
                    LinearProgressIndicator(progress = { elapsed.toFloat() / duration },
                        modifier = Modifier.align(Alignment.BottomEnd)
                            .padding(start = headerHeight + RideUiTokens.contentGap,
                                end = RideUiTokens.contentGap)
                            .fillMaxWidth()
                            .semantics { contentDescription =
                                "Playback ${elapsed / 1000} of ${duration / 1000} seconds" })
                }
                Surface(Modifier.size(headerHeight).align(Alignment.CenterStart)
                    .semantics { contentDescription = if (artwork == null) "Album art unavailable" else "Album art" },
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(RideUiTokens.rideButtonCorner)) {
                    Box(contentAlignment = Alignment.Center) {
                        if (artwork != null && music.availability == MusicAvailability.READY) {
                            Image(artwork!!.asImageBitmap(), contentDescription = null,
                                modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Icon(PhosphorIcons.Fill.MusicNotes, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(minOf(RideUiTokens.featureIconSize,
                                    headerHeight * RideUiTokens.musicArtworkIconFraction)))
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                MusicControl(PhosphorIcons.Fill.SkipBack, "Previous track", editor,
                    music.supports(MusicAction.PREVIOUS), Modifier.weight(1f).fillMaxHeight()) {
                    onAction(MusicAction.PREVIOUS)
                }
                MusicControl(if (music.playing) PhosphorIcons.Fill.Pause else PhosphorIcons.Fill.Play,
                    if (music.playing) "Pause" else "Play", editor,
                    music.supports(MusicAction.PLAY_PAUSE), Modifier.weight(1f).fillMaxHeight(),
                    filled = true, capsule = !music.playing) {
                    onAction(MusicAction.PLAY_PAUSE)
                }
                MusicControl(PhosphorIcons.Fill.SkipForward, "Next track", editor,
                    music.supports(MusicAction.NEXT), Modifier.weight(1f).fillMaxHeight()) {
                    onAction(MusicAction.NEXT)
                }
            }
        }
    }
}

@Composable
private fun MusicControl(
    icon: ImageVector, label: String, editor: Boolean, enabled: Boolean, modifier: Modifier,
    filled: Boolean = false, capsule: Boolean = false, onClick: () -> Unit
) {
    BoxWithConstraints(modifier) {
        val cornerRadius by animateDpAsState(
            targetValue = if (capsule) minOf(maxWidth, maxHeight) / 2
                else RideUiTokens.rideButtonCorner,
            animationSpec = tween(RideUiTokens.musicShapeTransitionMillis),
            label = "Music play button shape"
        )
        val shape = RoundedCornerShape(cornerRadius)
        val iconSize = minOf(RideUiTokens.featureIconSize,
            minOf(maxWidth, maxHeight) * RideUiTokens.musicControlIconFraction)
        if (filled && !editor) {
            Button(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxSize()
                .semantics { contentDescription = label }, shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary),
                contentPadding = PaddingValues(0.dp)) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(iconSize))
            }
        } else {
            Box(Modifier.fillMaxSize()
                .then(if (filled) Modifier.background(MaterialTheme.colorScheme.primary, shape) else Modifier)
                .semantics { contentDescription = label }
                .then(if (editor) Modifier else Modifier.clickable(enabled = enabled, onClick = onClick)),
                contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null,
                    tint = if (filled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(iconSize))
            }
        }
    }
}
