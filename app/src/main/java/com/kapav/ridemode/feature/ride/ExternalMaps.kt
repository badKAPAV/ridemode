package com.kapav.ridemode.feature.ride

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

internal fun openExternalMaps(context: Context, allowBrowserFallback: Boolean = true) {
    val uri = Uri.parse("https://www.google.com/maps")
    fun launch(intent: Intent): Boolean = try {
        if (context !is Activity) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        true
    } catch (_: ActivityNotFoundException) { false }
      catch (_: SecurityException) { false }
    if (launch(Intent(Intent.ACTION_VIEW, uri).setPackage("com.google.android.apps.maps"))) return
    if (allowBrowserFallback && launch(Intent(Intent.ACTION_VIEW, uri))) return
    Toast.makeText(context, if (allowBrowserFallback) "No app available to open Maps" else "Google Maps is unavailable",
        Toast.LENGTH_SHORT).show()
}
