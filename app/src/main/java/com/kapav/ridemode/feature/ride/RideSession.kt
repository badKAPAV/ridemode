package com.kapav.ridemode.feature.ride

import android.Manifest
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.kapav.ridemode.feature.widgets.NavigationSdkSession
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.kapav.ridemode.R

object RideSession {
    const val EXTRA_STARTED_AT = "com.kapav.ridemode.STARTED_AT"
    val active = mutableStateOf(false)
    private var startedAt = 0L

    fun start(context: Context, time: Long = System.currentTimeMillis()) {
        if (!active.value) startedAt = time
        active.value = true
        RideNotification.show(context, startedAt)
    }

    fun end(context: Context) {
        active.value = false
        RideNotification.cancel(context)
    }
}

@AndroidEntryPoint
class RideNotificationReceiver : BroadcastReceiver() {
    @Inject lateinit var navigation: NavigationSdkSession
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_END_RIDE) {
            // The Activity may be stopped after PiP closes; do not wait for UI recomposition.
            navigation.endCurrentRide()
            RideSession.end(context)
        }
    }

    companion object {
        const val ACTION_END_RIDE = "com.kapav.ridemode.END_RIDE"
    }
}

private object RideNotification {
    private const val CHANNEL_ID = "active_ride"
    private const val NOTIFICATION_ID = 1001

    fun show(context: Context, startedAt: Long) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        val manager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= 26) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.ride_notification_channel),
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val openIntent = Intent(context, RideActivity::class.java)
            .putExtra(RideSession.EXTRA_STARTED_AT, startedAt)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val open = PendingIntent.getActivity(context, 1, openIntent, flags)
        val endIntent = Intent(context, RideNotificationReceiver::class.java)
            .setAction(RideNotificationReceiver.ACTION_END_RIDE)
        val end = PendingIntent.getBroadcast(context, 2, endIntent, flags)
        val builder = if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder(context, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(context)
        }
        val notification = builder
            .setSmallIcon(R.drawable.ic_ride_notification)
            .setContentTitle(context.getString(R.string.ride_pip_status))
            .setContentText(context.getString(R.string.ride_notification_text))
            .setContentIntent(open)
            .setWhen(startedAt)
            .setUsesChronometer(true)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .addAction(Notification.Action.Builder(
                Icon.createWithResource(context, R.drawable.ic_ride_notification),
                context.getString(R.string.ride_open), open
            ).build())
            .addAction(Notification.Action.Builder(
                Icon.createWithResource(context, R.drawable.ic_ride_notification),
                context.getString(R.string.end_ride), end
            ).build())
            .build()
        if (Build.VERSION.SDK_INT >= 36) {
            notification.extras.putBoolean("android.requestPromotedOngoing", true)
        }
        manager.notify(NOTIFICATION_ID, notification)
    }

    fun cancel(context: Context) {
        context.getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_ID)
    }
}
