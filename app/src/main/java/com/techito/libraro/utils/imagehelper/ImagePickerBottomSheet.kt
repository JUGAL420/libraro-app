package com.techito.libraro.utils.imagehelper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.techito.libraro.R

class ImagePickerBottomSheet(
    private val config: ImagePickerConfig,
    private val listener: (PickType) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.bs_image_picker, container, false)

        view.findViewById<View>(R.id.camera).apply {
            visibility = if (config.allowCamera) View.VISIBLE else View.GONE
            setOnClickListener { listener(PickType.CAMERA); dismiss() }
        }

        view.findViewById<View>(R.id.gallery).apply {
            visibility = if (config.allowGallery) View.VISIBLE else View.GONE
            setOnClickListener { listener(PickType.GALLERY); dismiss() }
        }

        view.findViewById<View>(R.id.files).apply {
            visibility = if (config.allowFiles) View.VISIBLE else View.GONE
            setOnClickListener { listener(PickType.FILE); dismiss() }
        }

        return view
    }
}

enum class PickType { CAMERA, GALLERY, FILE }