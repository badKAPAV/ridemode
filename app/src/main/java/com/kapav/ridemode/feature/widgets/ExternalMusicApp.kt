package com.kapav.ridemode.feature.widgets

import android.app.Activity
import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.os.Build
import com.kapav.ridemode.data.MusicSessionState

/** Immutable source snapshot: a newly selected media provider cannot replace a pending request. */
internal data class MusicAppTarget(val packageName: String, val sessionActivity: PendingIntent?)

internal fun MusicSessionState.musicAppTarget(): MusicAppTarget? =
    sourcePackageForLaunch()?.let { MusicAppTarget(it, sessionActivity) }

/** Determines whether tapping artwork or details should show the device music app chooser sheet. */
internal fun MusicSessionState.shouldOpenChooser(): Boolean =
    !playing || musicAppTarget() == null

/** Prefer the provider's now-playing UI, then its launcher; neither action starts playback. */
internal fun openMusicApp(context: Context, target: MusicAppTarget) {
    if (target.sessionActivity != null && runCatching {
        val options = if (Build.VERSION.SDK_INT >= 34) ActivityOptions.makeBasic().apply {
            setPendingIntentBackgroundActivityStartMode(
                if (Build.VERSION.SDK_INT >= 36) ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOW_IF_VISIBLE
                else ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)
        }.toBundle() else null
        target.sessionActivity.send(context, 0, null, null, null, null, options)
    }.isSuccess) return
    val launched = runCatching {
        val intent = context.packageManager.getLaunchIntentForPackage(target.packageName)
            ?: return@runCatching false
        if (context !is Activity) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        true
    }.getOrDefault(false)
    if (!launched) Toast.makeText(context, "Music app is unavailable", Toast.LENGTH_SHORT).show()
}

/** Builds an open-with chooser intent for all music player apps installed on the device. */
internal fun musicAppChooserIntent(context: Context): Intent {
    val pm = context.packageManager
    val selectorIntent = Intent.makeMainSelectorActivity(
        Intent.ACTION_MAIN,
        Intent.CATEGORY_APP_MUSIC
    )
    val selectorApps = runCatching { pm.queryIntentActivities(selectorIntent, 0) }.getOrDefault(emptyList())
    val selectorPackages = selectorApps.mapNotNull { it.activityInfo?.packageName }.toSet()

    val musicPlayerIntent = Intent("android.intent.action.MUSIC_PLAYER")
    val legacyApps = runCatching { pm.queryIntentActivities(musicPlayerIntent, 0) }.getOrDefault(emptyList())

    val searchIntent = Intent("android.media.action.MEDIA_PLAY_FROM_SEARCH")
    val searchApps = runCatching { pm.queryIntentActivities(searchIntent, 0) }.getOrDefault(emptyList())

    val additionalPackages = (legacyApps + searchApps)
        .mapNotNull { it.activityInfo?.packageName }
        .filter { it !in selectorPackages && it != context.packageName }
        .distinct()

    val additionalIntents = additionalPackages.mapNotNull { pkg ->
        pm.getLaunchIntentForPackage(pkg)
    }

    val baseIntent = when {
        selectorApps.isNotEmpty() -> selectorIntent
        legacyApps.isNotEmpty() -> musicPlayerIntent
        else -> selectorIntent
    }

    val chooser = Intent.createChooser(baseIntent, "Open with")
    if (additionalIntents.isNotEmpty()) {
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, additionalIntents.toTypedArray())
    }
    if (context !is Activity) chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    return chooser
}

/** Opens the system "Open with" sheet for all music player apps on the device. */
internal fun openMusicAppChooser(context: Context) {
    val launched = runCatching {
        val chooser = musicAppChooserIntent(context)
        context.startActivity(chooser)
        true
    }.getOrDefault(false)
    if (!launched) Toast.makeText(context, "No music apps found", Toast.LENGTH_SHORT).show()
}
