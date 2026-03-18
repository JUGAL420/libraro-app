package com.techito.libraro.utils.imagehelper

data class ImagePickerConfig(
    val allowCamera: Boolean = true,
    val allowGallery: Boolean = true,
    val allowFiles: Boolean = true,
    val crop: Boolean = true
)