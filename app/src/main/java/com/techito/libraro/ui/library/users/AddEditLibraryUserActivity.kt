package com.techito.libraro.ui.library.users

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityAddEditLibraryUserBinding
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.utils.imagehelper.ImagePickerConfig
import com.techito.libraro.utils.imagehelper.ImagePickerManager
import com.techito.libraro.utils.imagehelper.ImagePickerResult
import com.techito.libraro.viewmodel.LibraryUserViewModel

class AddEditLibraryUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditLibraryUserBinding
    private val viewModel: LibraryUserViewModel by viewModels()
    private var isEdit = false
    private lateinit var picker: ImagePickerManager

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->

            if (granted) {
                picker.openCameraDirect() // we’ll expose this
            } else {
                AppUtils.showToast(this@AddEditLibraryUserActivity, "Camera permission required")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_library_user)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        isEdit = intent.getBooleanExtra("isEdit", false)
        binding.isEdit = isEdit

        setupImagePicker()
        setupObservers()
        setupClickListeners()
        setupRoleDropdown()
        setupBranchDropdown()
    }

    private fun setupImagePicker() {
        picker = ImagePickerManager(this, ImagePickerConfig()) { result ->
            when (result) {
                is ImagePickerResult.Success -> {
                    viewModel.setImage(result.uri, result.file)
                }

                is ImagePickerResult.Error -> {
                    AppUtils.showToast(this, result.message)
                }

                ImagePickerResult.Cancelled -> {}
            }
        }
    }

    private fun setupObservers() {
        viewModel.selectedImageUri.observe(this) { uri ->
            binding.ivProfile.setImageURI(uri)
        }
        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                AppUtils.showToast(this, it)
                viewModel.onErrorHandled()
            }
        }

        viewModel.userActionSuccess.observe(this) { message ->
            message?.let {
                AppUtils.showToast(this, it)
                viewModel.onActionSuccessHandled()
                finish()
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.layoutProgress.clProgress.isVisible = isLoading
        }
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivProfile.setOnClickListener {
            picker.show(
                onCameraClick = {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        picker.openCameraDirect()
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            )
        }

        binding.ivEditPicture.setOnClickListener {
            binding.ivProfile.performClick()
        }
    }

    private fun setupRoleDropdown() {
        val roles = arrayOf("Library Manager", "Librarian", "Staff")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        binding.actvRole.setAdapter(adapter)

        binding.actvRole.setOnItemClickListener { _, _, position, _ ->
            viewModel.role.value = roles[position]
        }
    }

    private fun setupBranchDropdown() {
        val branches = arrayOf("Branch 1", "Branch 2", "Branch 3")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, branches)
        binding.etBranches.setAdapter(adapter)

        binding.etBranches.setOnItemClickListener { _, _, position, _ ->
            viewModel.branches.value = branches[position]
        }
    }
}