package com.kapav.ridemode.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.telephony.PhoneNumberUtils
import android.util.Base64
import com.kapav.ridemode.core.model.SosFavorite
import com.kapav.ridemode.core.model.validFavoriteNumber
import java.io.ByteArrayOutputStream
import java.util.UUID

/** The system picker grants access to just the chosen phone row; copy a small local snapshot. */
internal fun readSosContact(context: Context, uri: Uri): SosFavorite {
    require(uri.scheme == "content")
    val resolver = context.contentResolver
    val selected = runCatching { resolver.query(uri,
        arrayOf(Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_THUMBNAIL_URI), null, null, null) }.getOrElse {
        resolver.query(uri, arrayOf(Phone.DISPLAY_NAME, Phone.NUMBER), null, null, null)
    }
    return selected?.use { cursor ->
        require(cursor.moveToFirst()) { "This contact has no phone number" }
        val rawNumber = cursor.getString(cursor.getColumnIndexOrThrow(Phone.NUMBER)).orEmpty()
        require(rawNumber.matches(Regex("^[+0-9 ().-]+$"))) { "Choose a plain phone number without dialing codes or extensions" }
        val number = PhoneNumberUtils.normalizeNumber(rawNumber)
        require(validFavoriteNumber(number)) { "Choose a contact with a valid phone number" }
        val name = cursor.getString(cursor.getColumnIndexOrThrow(Phone.DISPLAY_NAME)).orEmpty().trim().take(80).ifEmpty { number }
        val photoUri = cursor.getColumnIndex(Phone.PHOTO_THUMBNAIL_URI).takeIf { it >= 0 }?.let(cursor::getString)
        val photo = photoUri?.let { runCatching { readSosPhoto(context, Uri.parse(it)) }.getOrNull() }
        SosFavorite(UUID.randomUUID().toString(), name, number, photo)
    } ?: error("Contact is no longer available")
}

/** Store only a bounded thumbnail; no long-lived provider grant or shared external file is needed. */
internal fun readSosPhoto(context: Context, uri: Uri): String {
    require(uri.scheme == "content")
    val resolver = context.contentResolver
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    require(bounds.outWidth > 0 && bounds.outHeight > 0) { "Unable to read this image" }
    var sample = 1
    while (maxOf(bounds.outWidth, bounds.outHeight) / sample > 256) sample *= 2
    val bitmap = resolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
    } ?: error("Unable to read this image")
    val scale = minOf(1f, 128f / maxOf(bitmap.width, bitmap.height))
    val thumbnail = Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt().coerceAtLeast(1),
        (bitmap.height * scale).toInt().coerceAtLeast(1), true)
    val bytes = ByteArrayOutputStream().also { thumbnail.compress(Bitmap.CompressFormat.JPEG, 80, it) }.toByteArray()
    if (thumbnail !== bitmap) thumbnail.recycle()
    bitmap.recycle()
    return Base64.encodeToString(bytes, Base64.NO_WRAP).also { require(it.length <= 32768) { "Image is too large" } }
}
