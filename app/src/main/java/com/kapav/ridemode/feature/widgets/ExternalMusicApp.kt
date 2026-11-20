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
