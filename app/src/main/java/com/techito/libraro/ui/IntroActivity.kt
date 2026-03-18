package com.techito.libraro.ui

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.techito.libraro.LibraroApp
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityIntroBinding
import com.techito.libraro.model.IntroSlide
import com.techito.libraro.ui.library.adapter.IntroSliderAdapter
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.utils.AppUtils.dpToPx
import kotlinx.coroutines.launch

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding
    private lateinit var introSliderAdapter: IntroSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppUtils.changeStatusBarColor(this, R.color.white, true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)
        AppUtils.handleSystemBars(binding.mainLayout)
        initAdapter()
        setupViewPager()
        setupIndicators()
        setCurrentIndicator(0)

        binding.btnNext.setOnClickListener {
            if (binding.vpIntroSlider.currentItem + 1 < introSliderAdapter.itemCount) {
                binding.vpIntroSlider.currentItem += 1
            } else {
                navigateToNext()
            }
        }

        binding.btnSkip.setOnClickListener {
            navigateToNext()
        }
    }

    private fun initAdapter() {
        val slides = listOf(
            IntroSlide(
                getString(R.string.intro_title_1),
                getString(R.string.intro_description_1),
                R.drawable.intro_1
            ),
            IntroSlide(
                getString(R.string.intro_title_2),
                getString(R.string.intro_description_2),
                R.drawable.intro_2
            ),
            IntroSlide(
                getString(R.string.intro_title_3),
                getString(R.string.intro_description_3),
                R.drawable.intro_3
            )
        )
        introSliderAdapter = IntroSliderAdapter(slides)
    }

    private fun setupViewPager() {
        binding.vpIntroSlider.adapter = introSliderAdapter
        binding.vpIntroSlider.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                if (position == introSliderAdapter.itemCount - 1) {
                    binding.btnNext.text = getString(R.string.btn_lets_get_started)
                } else {
                    binding.btnNext.text = getString(R.string.btn_next)
                }
            }
        })
    }

    private fun setupIndicators() {
        binding.llIndicators.removeAllViews()
        val itemCount = introSliderAdapter.itemCount
        val margin = 8.dpToPx(this)
        val layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(margin, 0, margin, 0)

        for (i in 0 until itemCount) {
            val indicator = ImageView(this)
            indicator.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.indicator_inactive)
            )
            indicator.layoutParams = layoutParams
            binding.llIndicators.addView(indicator)
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.llIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = binding.llIndicators.getChildAt(i) as? ImageView
            if (i == index) {
                imageView?.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.indicator_active)
                )
            } else {
                imageView?.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.indicator_inactive)
                )
            }
        }
    }

    private fun navigateToNext() {
        lifecycleScope.launch {
            LibraroApp.preferenceManager.setFirstTimeLaunch(false)
            startActivity(Intent(this@IntroActivity, LoginOptionActivity::class.java))
            finish()
        }
    }
}
