package com.techito.libraro.ui.library

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.techito.libraro.LibraroApp
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityLibraryProfileBinding
import com.techito.libraro.ui.LoginOptionActivity
import com.techito.libraro.ui.library.branch.LibraryBranchMasterActivity
import com.techito.libraro.ui.library.branchfloor.BranchFloorBlockActivity
import com.techito.libraro.ui.library.exam.ExamNameMasterActivity
import com.techito.libraro.ui.library.expense.ExpenseNameMasterActivity
import com.techito.libraro.ui.library.plan.LibraryPlanActivity
import com.techito.libraro.ui.library.planprice.LibraryPlanPriceActivity
import com.techito.libraro.ui.library.plantypeorshift.LibraryPlanTypeShiftActivity
import com.techito.libraro.ui.library.users.LibraryUsersActivity
import com.techito.libraro.utils.AppUtils
import kotlinx.coroutines.launch

class LibraryProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_library_profile)
        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.menuUsers.root.setOnClickListener {
            startActivity(Intent(this, LibraryUsersActivity::class.java))
        }
        binding.menuBranch.root.setOnClickListener {
            startActivity(Intent(this, LibraryBranchMasterActivity::class.java))
        }
        binding.menuFloor.root.setOnClickListener {
            startActivity(Intent(this, BranchFloorBlockActivity::class.java))
        }
        binding.menuPlan.root.setOnClickListener {
            startActivity(Intent(this, LibraryPlanActivity::class.java))
        }

        binding.menuPlanTypeShift.root.setOnClickListener {
            startActivity(Intent(this, LibraryPlanTypeShiftActivity::class.java))
        }
        binding.menuPlanPrice.root.setOnClickListener {
            startActivity(Intent(this, LibraryPlanPriceActivity::class.java))
        }
        binding.menuExpense.root.setOnClickListener {
            startActivity(Intent(this, ExpenseNameMasterActivity::class.java))
        }
        binding.menuExams.root.setOnClickListener {
            startActivity(Intent(this, ExamNameMasterActivity::class.java))
        }
        binding.menuLogout.root.setOnClickListener {
            AppUtils.showCustomAlertDialog(this@LibraryProfileActivity,"Logout!","Are you sure you want to logout?", positiveText = "Logout",
                onPositiveClick = {
                    LibraroApp.logout(this@LibraryProfileActivity)
                })
        }
    }
}
