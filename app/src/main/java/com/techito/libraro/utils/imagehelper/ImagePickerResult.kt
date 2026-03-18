package com.techito.libraro.utils.imagehelper

import android.net.Uri
import java.io.File

sealed class ImagePickerResult {
    data class Success(val uri: Uri, val file: File) : ImagePickerResult()
    data class Error(val message: String) : ImagePickerResult()
    object Cancelled : ImagePickerResult()
}