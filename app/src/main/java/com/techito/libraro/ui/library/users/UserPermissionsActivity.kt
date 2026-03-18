package com.techito.libraro.ui.library.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityUserPermissionsBinding
import com.techito.libraro.databinding.ItemPermissionCheckboxBinding
import com.techito.libraro.databinding.ItemPermissionHeaderBinding
import com.techito.libraro.utils.AppUtils

class UserPermissionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserPermissionsBinding
    private val permissionList = mutableListOf<UserPermissionItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_permissions)

        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerView()
        loadDummyData()
    }

    private fun setupRecyclerView() {
        val adapter = PermissionAdapter(permissionList)
        val layoutManager = GridLayoutManager(this, 2)

        // Header takes 2 spans (full width), Checkbox takes 1 span
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (permissionList[position].isHeader) 2 else 1
            }
        }

        binding.rvPermissionSections.layoutManager = layoutManager
        binding.rvPermissionSections.adapter = adapter
    }

    private fun loadDummyData() {
        permissionList.clear()

        // Section 1
        permissionList.add(UserPermissionItem("", "Dashboard Permissions", isHeader = true))
        permissionList.add(UserPermissionItem("1", "Dashboard"))
        permissionList.add(UserPermissionItem("2", "Library Master Console"))
        permissionList.add(UserPermissionItem("3", "Learner List"))
        permissionList.add(UserPermissionItem("4", "Seat Assignment"))

        // Section 2
        permissionList.add(UserPermissionItem("", "Menu Permissions", isHeader = true))
        permissionList.add(UserPermissionItem("5", "Dashboard"))
        permissionList.add(UserPermissionItem("6", "Learner List"))
        permissionList.add(UserPermissionItem("7", "Dashboard"))
        permissionList.add(UserPermissionItem("8", "Learner List"))

        // Section 3
        permissionList.add(UserPermissionItem("", "Operational Permissions", isHeader = true))
        permissionList.add(UserPermissionItem("9", "Dashboard"))
        permissionList.add(UserPermissionItem("10", "Learner List"))

        binding.rvPermissionSections.adapter?.notifyDataSetChanged()
    }

    // Data Model
    data class UserPermissionItem(
        val id: String,
        val name: String,
        var isChecked: Boolean = false,
        val isHeader: Boolean = false
    )

    // Adapter Implementation
    inner class PermissionAdapter(private val items: List<UserPermissionItem>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val TYPE_HEADER = 0
        private val TYPE_ITEM = 1

        override fun getItemViewType(position: Int): Int {
            return if (items[position].isHeader) TYPE_HEADER else TYPE_ITEM
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == TYPE_HEADER) {
                val b = ItemPermissionHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(b)
            } else {
                val b = ItemPermissionCheckboxBinding.inflate(inflater, parent, false)
                ItemViewHolder(b)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = items[position]
            if (holder is HeaderViewHolder) {
                holder.binding.title = item.name
            } else if (holder is ItemViewHolder) {
                holder.binding.name = item.name
                holder.binding.cbPermission.isChecked = item.isChecked
                holder.binding.cbPermission.setOnCheckedChangeListener { _, isChecked ->
                    item.isChecked = isChecked
                }
            }
        }

        override fun getItemCount(): Int = items.size

        inner class HeaderViewHolder(val binding: ItemPermissionHeaderBinding) : RecyclerView.ViewHolder(binding.root)
        inner class ItemViewHolder(val binding: ItemPermissionCheckboxBinding) : RecyclerView.ViewHolder(binding.root)
    }
}