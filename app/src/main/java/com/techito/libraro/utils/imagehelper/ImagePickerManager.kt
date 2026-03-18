package com.techito.libraro.utils.imagehelper

import android.app.Activity
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import java.io.File

class ImagePickerManager(
    private val activity: AppCompatActivity,
    private val config: ImagePickerConfig,
    private val callback: (ImagePickerResult) -> Unit
) {

    private lateinit var cameraUri: Uri

    private val cameraLauncher =
        activity.registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) handleCrop(cameraUri) else callback(ImagePickerResult.Cancelled)
        }

    private val galleryLauncher =
        activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { handleCrop(it) } ?: callback(ImagePickerResult.Cancelled)
        }

    private val fileLauncher =
        activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let { handleCrop(it) } ?: callback(ImagePickerResult.Cancelled)
        }

    private val cropLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val uri = UCrop.getOutput(result.data!!)
                uri?.let { processFinal(it) }
            } else callback(ImagePickerResult.Cancelled)
        }

    fun show(onCameraClick: (() -> Unit)? = null) {
        ImagePickerBottomSheet(config) {
            when (it) {
                PickType.CAMERA -> {
                    onCameraClick?.invoke() ?: openCameraDirect()
                }

                PickType.GALLERY -> openGallery()
                PickType.FILE -> openFiles()
            }
        }.show(activity.supportFragmentManager, "picker")
    }

    private fun openCamera() {
        val tempUri = FileUtils.createImageUri(activity)
        if (tempUri != null) {
            cameraUri = tempUri
            cameraLauncher.launch(cameraUri)
        }
    }

    fun openCameraDirect() {
        val tempUri = FileUtils.createImageUri(activity)
        if (tempUri != null) {
            cameraUri = tempUri
            cameraLauncher.launch(cameraUri)
        }
    }

    private fun openGallery() {
        galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun openFiles() {
        fileLauncher.launch(arrayOf("image/*"))
    }

    private fun handleCrop(uri: Uri) {

        if (!config.crop) {
            processFinal(uri)
            return
        }

        val dest = Uri.fromFile(File(activity.cacheDir, "IMG_${System.currentTimeMillis()}.jpg"))

        val options = UCrop.Options().apply {
            setCompressionQuality(100)
        }

        val intent = UCrop.of(uri, dest)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1024, 1024)
            .withOptions(options)
            .getIntent(activity)

        cropLauncher.launch(intent)
    }

    private fun processFinal(uri: Uri) {

        activity.lifecycleScope.launch {

            try {
                val file = ImageProcessing.compressToMaxSize(activity, uri)
                callback(ImagePickerResult.Success(uri, file))

            } catch (e: Exception) {
                callback(ImagePickerResult.Error(e.message ?: "Error"))
            }
        }
    }
}