package com.kapav.ridemode.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.CallLog
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class CallHistoryAccess { REQUIRED, LOADING, READY, UNAVAILABLE }

data class RecentCall(
    val id: Long, val name: String?, val number: String?,
    val type: Int, val timestampMs: Long
)

data class CallHistoryState(
    val access: CallHistoryAccess = CallHistoryAccess.REQUIRED,
    val calls: List<RecentCall> = emptyList()
)

internal fun isCallablePhoneNumber(number: String): Boolean =
    number.matches(Regex("^[+]?[0-9][0-9 ().-]{1,}$"))

/** Reads system Recents only while Ride/editor is visible; keeps no separate call record. */
@Singleton
class CallHistoryRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val owners = mutableSetOf<String>()
    private val _state = MutableStateFlow(CallHistoryState())
    val state = _state.asStateFlow()
    private var observerRegistered = false
    private var refreshJob: Job? = null
    private val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) = refresh()
    }

    fun start(owner: String) {
        if (!owners.add(owner)) return
        if (owners.size == 1) {
            if (hasPermission()) {
                runCatching {
                    context.contentResolver.registerContentObserver(CallLog.Calls.CONTENT_URI,
                        true, observer)
                }.onSuccess { observerRegistered = true }
            }
            refresh()
        }
    }

    fun stop(owner: String) {
        owners.remove(owner)
        if (owners.isNotEmpty()) return
        refreshJob?.cancel()
        if (observerRegistered) {
            runCatching { context.contentResolver.unregisterContentObserver(observer) }
            observerRegistered = false
        }
        _state.value = CallHistoryState()
    }

    fun refresh() {
        if (!hasPermission()) {
            refreshJob?.cancel()
            _state.value = CallHistoryState()
            return
        }
        if (owners.isEmpty()) return
        refreshJob?.cancel()
        _state.value = CallHistoryState(CallHistoryAccess.LOADING, _state.value.calls)
        refreshJob = scope.launch {
            val result = withContext(Dispatchers.IO) { runCatching { readFive() } }
            if (owners.isEmpty()) return@launch
            _state.value = result.fold(
                onSuccess = { CallHistoryState(CallHistoryAccess.READY, it) },
                onFailure = { CallHistoryState(CallHistoryAccess.UNAVAILABLE) }
            )
        }
    }

    private fun hasPermission(): Boolean = ContextCompat.checkSelfPermission(context,
        Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED

    private fun readFive(): List<RecentCall> {
        val projection = arrayOf(CallLog.Calls._ID, CallLog.Calls.CACHED_NAME,
            CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE,
            CallLog.Calls.NUMBER_PRESENTATION)
        val result = ArrayList<RecentCall>(5)
        context.contentResolver.query(CallLog.Calls.CONTENT_URI, projection, null, null,
            "${CallLog.Calls.DATE} DESC")?.use { cursor ->
            val id = cursor.getColumnIndexOrThrow(CallLog.Calls._ID)
            val name = cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)
            val number = cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
            val type = cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE)
            val date = cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)
            val presentation = cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER_PRESENTATION)
            while (result.size < 5 && cursor.moveToNext()) {
                val phone = cursor.getString(number)?.trim().orEmpty()
                val callable = cursor.getInt(presentation) == CallLog.Calls.PRESENTATION_ALLOWED &&
                    isCallablePhoneNumber(phone)
                result += RecentCall(cursor.getLong(id), cursor.getString(name)?.trim(),
                    phone.takeIf { callable }, cursor.getInt(type), cursor.getLong(date))
            }
        } ?: error("Call log unavailable")
        return result
    }
}
