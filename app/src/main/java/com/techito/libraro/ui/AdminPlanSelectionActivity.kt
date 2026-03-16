package com.techito.libraro.ui

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.techito.libraro.LibraroApp
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityPlanSelectionBinding
import com.techito.libraro.model.PaymentCreateOrderData
import com.techito.libraro.ui.adapter.PlanSliderAdapter
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.viewmodel.SubscriptionPlanViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject

class AdminPlanSelectionActivity : AppCompatActivity(), PaymentResultWithDataListener {

    private lateinit var binding: ActivityPlanSelectionBinding
    private val viewModel: SubscriptionPlanViewModel by viewModels()
    private lateinit var planSliderAdapter: PlanSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppUtils.changeStatusBarColor(this, R.color.white, true)

        WindowCompat.setDecorFitsSystemWindows(window, true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_plan_selection)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Preload Razorpay
        Checkout.preload(applicationContext)

        handleInsets()
        setupPlanSlider()
        setupPlanModeDropdown()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.planModes.observe(this) { modesList ->
            modesList?.let { plans ->
                planSliderAdapter.setPlans(plans)
                setupIndicators(plans.size)
                setCurrentIndicator(0)
                binding.vpPlans.post {
                    binding.vpPlans.setCurrentItem(0, false)
                }
            }
        }

        viewModel.paymentOrderResponse.observe(this) { response ->
            response?.data?.let { orderData ->
                if (orderData.isPaid == true) {
                    AppUtils.showToast(this, response.message?:"Your free plan activated!")
                    viewModel.navigateToAddBranch.value = true
                } else if (!orderData.amount.isNullOrEmpty() && !orderData.orderId.isNullOrEmpty() && !orderData.keyId.isNullOrEmpty())
                    startPayment(orderData)
            }
        }

        viewModel.navigateToAddBranch.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                startActivity(Intent(this, AddBranchFloorActivity::class.java))
                viewModel.onNavigationHandled()
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                AppUtils.showToast(this, it)
                viewModel.onErrorHandled()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.layoutProgress.clProgress.isVisible = isLoading
        }

        // Fix for AutoCompleteTextView filtering issue
        viewModel.selectedPlanType.observe(this) { type ->
            type?.name?.let {
                binding.actvPlanMode.setText(it, false)
            }
        }

        viewModel.currentPlanIndex.observe(this) { position ->
            viewModel.updateCurrentPlanMode(position)
        }
    }

    private fun startPayment(orderData: PaymentCreateOrderData) {
        lifecycleScope.launch {
            val checkout = Checkout()
            // Use your live or test key here
            checkout.setKeyID(orderData.keyId)

            try {
                val options = JSONObject()
                options.put("name", getString(R.string.app_name))
                options.put(
                    "description",
                    "Subscription for ${viewModel.selectedPlanType.value?.name ?: ""} ${viewModel.selectedPlanMode.value?.name ?: ""}"
                )
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
                options.put("theme.color", "#1B1464");
                options.put("currency", orderData.currency ?: "INR")
//            val amount = amountStr.toDouble() * 100
                options.put("amount", orderData.amount?.toInt() ?: 100)

                options.put("order_id", orderData.orderId)
                val retryObj = JSONObject()
                retryObj.put("enabled", true)
                retryObj.put("max_count", 4)

                options.put("retry", retryObj)

                /*   val prefill = JSONObject()
                   val appSettings = LibraroApp.preferenceManager.appSettings.first()
                   if (appSettings != null && !appSettings.contactEmail.isNullOrEmpty()) {
                       prefill.put("email", appSettings.contactEmail?.get(0) ?: "")
                   }
                   if (appSettings != null && !appSettings.contactNumber.isNullOrEmpty()) {
                       prefill.put("contact", appSettings.contactNumber?.get(0) ?: "")
                   }
                   options.put("prefill", prefill)*/

                val notesObj = JSONObject()
                notesObj.put("Transaction_no", orderData.transactionId)
                val libraryId = LibraroApp.preferenceManager.libraryId.first()
                notesObj.put("Library_id", libraryId ?: "")
                options.put("notes", notesObj)

                checkout.open(this@AdminPlanSelectionActivity, options)
            } catch (e: Exception) {
                AppUtils.showToast(
                    this@AdminPlanSelectionActivity,
                    "Error in payment: " + e.message
                )
                e.printStackTrace()
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?, paymentData: PaymentData) {
        Log.d("Payment Successful: ", "$razorpayPaymentID \n ${Gson().toJson(paymentData)}")
        Checkout.clearUserData(this@AdminPlanSelectionActivity)
        razorpayPaymentID?.let {
            viewModel.verifyOrder(
                it.toString(),
                paymentData.orderId,
                paymentData.signature,
                "success",
                Gson().toJson(paymentData)
            )
        }
    }

    override fun onPaymentError(code: Int, response: String?, paymentData: PaymentData?) {
        Log.d("Payment Failed: ", "$response \n ${Gson().toJson(paymentData)}")
        Checkout.clearUserData(this@AdminPlanSelectionActivity)
        viewModel.verifyOrder(
            paymentData?.paymentId ?: "",
            paymentData?.orderId ?: "",
            paymentData?.signature ?: "",
            "failed",
            "\"Response: \" $response, \"data: \" ${Gson().toJson(paymentData)}"
        )
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
        viewModel.planTypes.observe(this) { types ->
            if (!types.isNullOrEmpty()) {
                val planTypeNames = types.map { it.name ?: "" }
                val adapter =
                    ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, planTypeNames)
                binding.actvPlanMode.setAdapter(adapter)

                binding.actvPlanMode.setOnItemClickListener { _, _, position, _ ->
                    viewModel.onPlanTypeSelected(types[position])
                }
            }
        }
    }

    private fun setupIndicators(count: Int) {
        binding.llIndicators.removeAllViews()
        if (count <= 1) return

        val layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)

        for (i in 0 until count) {
            val indicator = ImageView(this)
            indicator.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.indicator_inactive
                )
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
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.indicator_inactive
                    )
                )
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

    override fun onDestroy() {
        super.onDestroy()
        Checkout.clearUserData(this)
    }
}
