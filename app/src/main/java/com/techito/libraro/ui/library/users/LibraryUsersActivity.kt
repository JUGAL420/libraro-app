package com.techito.libraro.ui.library.users

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityLibraryUsersBinding
import com.techito.libraro.ui.library.users.UserPermissionsActivity
import com.techito.libraro.ui.library.users.LibraryUsersAdapter
import com.techito.libraro.utils.AppUtils

class LibraryUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryUsersBinding
    private lateinit var usersAdapter: LibraryUsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_library_users)
        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        setupRecyclerView()
        setupListeners()

    }

    private fun setupListeners() {

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivAddUser.setOnClickListener {
            openAddEditUser(false)
        }

        binding.fabAddUser.setOnClickListener {
            binding.ivAddUser.performClick()
        }
    }

    private fun setupRecyclerView() {
        usersAdapter = LibraryUsersAdapter(
            onEditClick = { position ->
                openAddEditUser(true)
            },
            onDeleteClick = { position ->
                // Handle delete logic here
            },
            onPermissionClick = { position ->
                startActivity(Intent(this, UserPermissionsActivity::class.java))
            }
        )
        binding.rvUsers.adapter = usersAdapter
    }

    private fun openAddEditUser(isEdit: Boolean) {
        val intent = Intent(this, AddEditLibraryUserActivity::class.java)
        intent.putExtra("isEdit", isEdit)
        startActivity(intent)
    }
}