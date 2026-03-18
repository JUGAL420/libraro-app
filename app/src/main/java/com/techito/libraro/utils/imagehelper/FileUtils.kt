package com.techito.libraro.utils.imagehelper

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.util.UUID

object FileUtils {

    fun createImageUri(context: Context): Uri? {
        val imageFileName = "IMG_${System.currentTimeMillis()}"
        val cacheDir = File(context.cacheDir, "images")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        try {
            var file = File.createTempFile(imageFileName, ".jpg", cacheDir)
            if(file == null){
                file = File(context.externalCacheDir, "camera_image_${UUID.randomUUID()}.jpg")
            }
            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    fun uriToFile(context: Context, uri: Uri): File {
        val input = context.contentResolver.openInputStream(uri)!!
        val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")

        input.use { inputStream ->
            file.outputStream().use { output ->
                inputStream.copyTo(output)
            }
        }

        return file
    }
}