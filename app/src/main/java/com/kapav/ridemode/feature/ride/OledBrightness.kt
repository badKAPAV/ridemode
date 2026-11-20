package com.kapav.ridemode.feature.ride

import android.view.WindowManager
import com.kapav.ridemode.core.ui.theme.RideUiTokens

/** Never leave a saver override on a paused or PiP window, even after a late UI callback. */
internal fun oledWindowBrightness(
    saver: Boolean,
    resumed: Boolean,
    inPip: Boolean,
    extraDim: Boolean
): Float = when {
    !saver || !resumed || inPip -> WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
    extraDim -> RideUiTokens.oledDimWindowBrightness
    else -> RideUiTokens.oledWindowBrightness
}

/** Also dims emitted content on devices that clamp low window-brightness requests. */
internal fun oledContentOpacity(extraDim: Boolean): Float =
    if (extraDim) RideUiTokens.oledDimContentOpacity else RideUiTokens.oledContentOpacity
