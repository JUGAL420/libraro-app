package com.techito.libraro.ui.library

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.techito.libraro.LibraroApp
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

        AppUtils.handleSystemBars(binding.clHeader,true, false)
        AppUtils.startEntranceAnimation(binding.ivSeat)
        setupNavigation()
        setupSearchLogic()
        setupRefreshLogic()
        setupListeners()
        observeViewModel()
        
        // Fetch library details on start
        viewModel.fetchLibraryDetails()
    }

    private fun setupListeners() {
        binding.ivProfile.setOnClickListener {
            startActivity(Intent(this, LibraryProfileActivity::class.java))
        }
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

    private fun observeViewModel() {
        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                AppUtils.showToast(this@MainActivity, it)
                viewModel.errorMessage.value = null
            }
        }

        viewModel.unAuthenticated.observe(this) { unAuthenticated ->
            if(unAuthenticated){
                LibraroApp.logout(this@MainActivity)
            }
        }
        
        viewModel.libraryDetails.observe(this) { details ->
            details?.let {
                // TODO : Update UI components if needed based on library details
//                binding.tvLibraryName.text = it.libraryName
            }
        }
    }

    interface Refreshable {
        fun onRefresh()
    }
}