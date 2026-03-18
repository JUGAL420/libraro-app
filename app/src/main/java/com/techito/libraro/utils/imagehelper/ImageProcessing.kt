package com.techito.libraro.utils.imagehelper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

object ImageProcessing {

    suspend fun compressToMaxSize(context: Context, uri: Uri): File {
        return withContext(Dispatchers.IO) {

            val inputStream = context.contentResolver.openInputStream(uri)!!
            var bitmap = BitmapFactory.decodeStream(inputStream)

            bitmap = fixRotation(context, uri, bitmap)

            val file = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")

            var quality = 90
            var stream: ByteArrayOutputStream

            do {
                stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
                quality -= 5
            } while (stream.size() > 1_500_000 && quality > 10)

            file.writeBytes(stream.toByteArray())
            file
        }
    }

    private fun fixRotation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {

        return try {
            val input = context.contentResolver.openInputStream(uri)!!
            val exif = ExifInterface(input)

            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val matrix = Matrix()

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                else -> return bitmap
            }

            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        } catch (e: Exception) {
            bitmap
        }
    }
}