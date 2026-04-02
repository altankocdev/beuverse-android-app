package com.altankoc.beuverse.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

fun Uri.toMultipartBody(context: Context, partName: String): MultipartBody.Part? {
    val contentResolver = context.contentResolver
    val fileName = contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    } ?: "temp_image.jpg"

    val file = File(context.cacheDir, fileName)
    contentResolver.openInputStream(this)?.use { inputStream ->
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }

    file.compressImage()

    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, file.name, requestFile)
}

fun Bitmap.toMultipartBody(context: Context, partName: String): MultipartBody.Part {
    val file = File(context.cacheDir, "cropped_image_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { outputStream ->
        this.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
    }
    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, file.name, requestFile)
}

private fun File.compressImage(quality: Int = 70) {
    try {
        val bitmap = BitmapFactory.decodeFile(this.absolutePath) ?: return
        val outputStream = FileOutputStream(this)
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        outputStream.flush()
        outputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun List<Uri>.toMultipartBodyList(context: Context, partName: String): List<MultipartBody.Part> {
    return this.mapNotNull { it.toMultipartBody(context, partName) }
}