package com.techito.libraro.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityMainBinding
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppUtils.changeStatusBarColor(this, R.color.white, true)
        
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        handleInsets()
        AppUtils.startEntranceAnimation(binding.ivSeat)
        setupNavigation()
        setupSearchLogic()
        setupRefreshLogic()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        viewModel.navigateToSearch.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                if (navController.currentDestination?.id != R.id.navigation_search) {
                    navController.navigate(R.id.navigation_search)
                }
                viewModel.onNavigationHandled()
            }
        }
    }

    private fun setupSearchLogic() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.onSearchClicked()
                true
            } else {
                false
            }
        }
    }

    private fun setupRefreshLogic() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onRefresh()
        }

        viewModel.refreshEvent.observe(this) { isRefreshing ->
            if (isRefreshing) {
                // Find the current active fragment
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
                
                // If the fragment implements a custom Refreshable interface, trigger its refresh
                if (currentFragment is Refreshable) {
                    currentFragment.onRefresh()
                } else {
                    // Default behavior if not implemented
                    viewModel.onRefreshHandled()
                }
            }
        }
    }

    private fun handleInsets() {
        val header = binding.clHeader
        val originalPadding = Rect(
            header.paddingLeft,
            header.paddingTop,
            header.paddingRight,
            header.paddingBottom
        )

        ViewCompat.setOnApplyWindowInsetsListener(header) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                originalPadding.left,
                originalPadding.top + systemBars.top,
                originalPadding.right,
                originalPadding.bottom
            )
            insets
        }
    }

    interface Refreshable {
        fun onRefresh()
    }
}
