package com.kapav.ridemode.feature.pageeditor

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kapav.ridemode.core.database.WidgetInstance
import com.kapav.ridemode.core.model.*
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.kapav.ridemode.data.readSosContact
import com.kapav.ridemode.data.readSosPhoto
import com.kapav.ridemode.feature.widgets.SosAvatar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WidgetSettingsDialog(widget: WidgetInstance, onDismiss: () -> Unit, onSave: suspend (String) -> Boolean) {
    var draft by rememberSaveable(widget.id) { mutableStateOf(widget.configJson) }
    var error by remember(widget.id) { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var replacement by rememberSaveable(widget.id) { mutableStateOf<String?>(null) }
    var photoTarget by rememberSaveable(widget.id) { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri = result.data?.data
        if (result.resultCode == Activity.RESULT_OK && uri == null) error = "No phone number returned. Please choose again"
        if (result.resultCode == Activity.RESULT_OK && uri != null) scope.launch {
            busy = true; error = null
            try {
                val contact = withContext(Dispatchers.IO) { readSosContact(context, uri) }
                val current = WidgetConfigs.sos(draft)
                val replaced = replacement
                if (current.contacts.any { it.number == contact.number && it.id != replaced }) {
                    error = "That number is already in this widget"
                } else {
                    val contacts = if (replaced == null) (current.contacts + contact).take(6)
                        else current.contacts.map { if (it.id == replaced) contact.copy(id = replaced) else it }
                    draft = WidgetConfigs.sosJson(draft, current.copy(contacts = contacts))
                }
            } catch (failure: Exception) {
                if (failure is kotlinx.coroutines.CancellationException) throw failure
                error = if (failure is IllegalArgumentException) failure.message else "Unable to read this contact. Choose a phone number again"
            }
            finally { busy = false; replacement = null }
        }
    }
    val photos = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val id = photoTarget
        if (uri != null && id != null) scope.launch {
            busy = true; error = null
            try {
                val photo = withContext(Dispatchers.IO) { readSosPhoto(context, uri) }
                val current = WidgetConfigs.sos(draft)
                draft = WidgetConfigs.sosJson(draft, current.copy(contacts = current.contacts.map {
                    if (it.id == id) it.copy(photo = photo) else it
                }))
            } catch (failure: Exception) {
                if (failure is kotlinx.coroutines.CancellationException) throw failure
                error = "Unable to use this photo. Initials are still available"
            }
            finally { busy = false; photoTarget = null }
        }
    }
    fun pickContact(id: String?) {
        replacement = id
        try { picker.launch(Intent(Intent.ACTION_PICK).setType(Phone.CONTENT_TYPE)) }
        catch (_: ActivityNotFoundException) { error = "No contacts picker available on this device" }
    }
    Dialog(onDismissRequest = { if (!busy) onDismiss() }, properties = DialogProperties(
        usePlatformDefaultWidth = false, decorFitsSystemWindows = false, dismissOnClickOutside = false)) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.fillMaxSize().safeDrawingPadding().padding(RideUiTokens.contentGap),
                verticalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onDismiss, enabled = !busy) { Text("Cancel") }
                    Text(if (widget.typeId == "speedometer") "Speedometer settings" else "Contacts for this SOS widget",
                        style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    Button(onClick = { scope.launch {
                        busy = true; error = null
                        try {
                            if (!onSave(draft)) error = "Unable to save. Your changes are still here; try again"
                        } catch (failure: Exception) {
                            if (failure is kotlinx.coroutines.CancellationException) throw failure
                            error = "Unable to save. Your changes are still here; try again"
                        } finally { busy = false }
                    } }, enabled = !busy) { Text("Save") }
                }
                if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(RideUiTokens.contentGap)) {
                    if (widget.typeId == "speedometer") {
                        val config = WidgetConfigs.speed(draft)
                        Text("Units for this tile", style = MaterialTheme.typography.titleSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
                            listOf("default" to "App default", "kmh" to "km/h", "mph" to "mph").forEachIndexed { index, (value, label) ->
                                ToggleButton(checked = config.units == value,
                                    onCheckedChange = { draft = WidgetConfigs.speedJson(draft, config.copy(units = value)) },
                                    shapes = when (index) { 0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                        2 -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes() }) { Text(label) }
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Show GPS status when space allows", modifier = Modifier.weight(1f))
                            Switch(config.showStatus, onCheckedChange = {
                                draft = WidgetConfigs.speedJson(draft, config.copy(showStatus = it)) })
                        }
                        Text("GPS speed is shown only with a fresh usable fix. Missing data shows —, never a made-up zero.")
                    } else {
                        val config = WidgetConfigs.sos(draft)
                        Text("${config.contacts.size}/6 selected. These contacts belong only to this widget.")
                        config.contacts.forEachIndexed { index, contact ->
                            key(contact.id) { Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                                Column(Modifier.padding(RideUiTokens.contentGap)) {
                                    Row(verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(RideUiTokens.contentGap)) {
                                        Surface(Modifier.size(RideUiTokens.panelIconContainer), color = MaterialTheme.colorScheme.primary,
                                            shape = MaterialTheme.shapes.medium) {
                                            SosAvatar(contact.photo, contact.initials, Modifier.fillMaxSize())
                                        }
                                        OutlinedTextField(value = contact.name, label = { Text("Display name") }, singleLine = true,
                                            modifier = Modifier.weight(1f), enabled = !busy,
                                            onValueChange = { name ->
                                                val updated = contact.copy(name = name.take(80).ifBlank { contact.number })
                                                draft = WidgetConfigs.sosJson(draft, config.copy(contacts = config.contacts.map {
                                                    if (it.id == contact.id) updated else it }))
                                            })
                                    }
                                    Text(contact.number, style = MaterialTheme.typography.bodyMedium)
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                                        TextButton(onClick = { pickContact(contact.id) }, enabled = !busy) { Text("Change number") }
                                        TextButton(onClick = { photoTarget = contact.id
                                            try { photos.launch("image/*") } catch (_: ActivityNotFoundException) { error = "No photo picker available" }
                                        }, enabled = !busy) { Text("Photo") }
                                        if (contact.photo != null) TextButton(onClick = {
                                            draft = WidgetConfigs.sosJson(draft, config.copy(contacts = config.contacts.map {
                                                if (it.id == contact.id) it.copy(photo = null) else it }))
                                        }, enabled = !busy) { Text("Use initials") }
                                        TextButton(onClick = {
                                            val list = config.contacts.toMutableList()
                                            java.util.Collections.swap(list, index, index - 1)
                                            draft = WidgetConfigs.sosJson(draft, config.copy(contacts = list))
                                        }, enabled = !busy && index > 0) { Text("Earlier") }
                                        TextButton(onClick = {
                                            val list = config.contacts.toMutableList()
                                            java.util.Collections.swap(list, index, index + 1)
                                            draft = WidgetConfigs.sosJson(draft, config.copy(contacts = list))
                                        }, enabled = !busy && index < config.contacts.lastIndex) { Text("Later") }
                                        TextButton(onClick = { draft = WidgetConfigs.sosJson(draft,
                                            config.copy(contacts = config.contacts.filter { it.id != contact.id })) }, enabled = !busy) { Text("Remove") }
                                    }
                                }
                            }
                        }
                        }
                        Button(onClick = { pickContact(null) }, enabled = !busy && config.contacts.size < 6) { Text("Choose contact") }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Direct calling after confirmation", modifier = Modifier.weight(1f))
                            Switch(config.directCall, onCheckedChange = { draft = WidgetConfigs.sosJson(draft, config.copy(directCall = it)) }, enabled = !busy)
                        }
                        Text("By default, a confirmed tap opens the phone dialer. Direct calling asks for call permission; emergency numbers always use the dialer. Names, numbers and thumbnails are saved for this tile; reselect a contact to refresh changed details.")
                    }
                }
            }
        }
    }
}
