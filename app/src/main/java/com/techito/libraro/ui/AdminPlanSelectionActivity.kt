package com.techito.libraro.ui

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityPlanSelectionBinding
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.viewmodel.PlanViewModel
import com.techito.libraro.ui.adapter.PlanSliderAdapter

class AdminPlanSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlanSelectionBinding
    private lateinit var viewModel: PlanViewModel
    private lateinit var planSliderAdapter: PlanSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppUtils.changeStatusBarColor(this, R.color.white, true)

        WindowCompat.setDecorFitsSystemWindows(window, true)
        viewModel = ViewModelProvider(this)[PlanViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_plan_selection)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        handleInsets()
        setupPlanSlider()
        setupPlanModeDropdown()
        setupNavigationObserver()
        
        viewModel.selectedPlanMode.observe(this) { mode ->
            planSliderAdapter.setPlans(mode.plans)
            setupIndicators(mode.plans.size)
            setCurrentIndicator(0)
        }
    }

    private fun setupNavigationObserver() {
        viewModel.navigateToAddBranch.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                startActivity(Intent(this, AddBranchFloorActivity::class.java))
                viewModel.onNavigationHandled()
            }
        }
    }

    private fun setupPlanSlider() {
        planSliderAdapter = PlanSliderAdapter()
        binding.vpPlans.adapter = planSliderAdapter
        
        binding.vpPlans.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                viewModel.currentPlanIndex.value = position
            }
        })
    }

    private fun setupPlanModeDropdown() {
        viewModel.planModes.observe(this) { modes ->
            val modeNames = modes.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, modeNames)
            binding.actvPlanMode.setAdapter(adapter)
            
            binding.actvPlanMode.setOnItemClickListener { _, _, position, _ ->
                viewModel.onPlanModeSelected(modes[position])
            }
        }
    }

    private fun setupIndicators(count: Int) {
        binding.llIndicators.removeAllViews()
        if (count <= 1) return
        
        val indicators = arrayOfNulls<ImageView>(count)
        val layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        
        for (i in 0 until count) {
            indicators[i] = ImageView(this)
            indicators[i]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive))
            indicators[i]?.layoutParams = layoutParams
            binding.llIndicators.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.llIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = binding.llIndicators.getChildAt(i) as? ImageView
            if (i == index) {
                imageView?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_active))
            } else {
                imageView?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive))
            }
        }
    }

    private fun handleInsets() {
        val mainLayout = binding.mainLayout
        val originalPadding = Rect(
            mainLayout.paddingLeft,
            mainLayout.paddingTop,
            mainLayout.paddingRight,
            mainLayout.paddingBottom
        )

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                originalPadding.left,
                originalPadding.top + systemBars.top,
                originalPadding.right,
                originalPadding.bottom + systemBars.bottom
            )
            insets
        }
    }
}
