package com.kapav.ridemode.feature.widgets

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.widget.Toast

/** Ask Android for the currently selected assistant; never hard-code an assistant package. */
internal fun openDeviceAssistant(context: Context) {
    var host = context
    while (host is ContextWrapper && host !is Activity && host.baseContext !== host) host = host.baseContext
    val activity = (host as? Activity)?.takeUnless { it.isFinishing || it.isDestroyed }
    val shown = runCatching { activity?.showAssist(Bundle.EMPTY) == true }.getOrDefault(false)
    if (!shown) Toast.makeText(context, "Your default assistant is unavailable. Check Android assistant settings", Toast.LENGTH_LONG).show()
}
