package com.techito.libraro.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techito.libraro.LibraroApp
import com.techito.libraro.R
import com.techito.libraro.data.remote.RetrofitClient
import com.techito.libraro.model.PaymentCreateOrderResponse
import com.techito.libraro.model.PlanData
import com.techito.libraro.model.SubscriptionPlanResponse
import com.techito.libraro.model.SubscriptionType
import com.techito.libraro.repository.AuthRepository
import com.techito.libraro.repository.MainRepository
import com.techito.libraro.utils.NetworkResult
import kotlinx.coroutines.launch

class SubscriptionPlanViewModel : ViewModel() {
    private val repository = MainRepository(RetrofitClient.apiService)
    private val authRepository = AuthRepository(RetrofitClient.userAuthApiService)
    private val preferenceManager = LibraroApp.preferenceManager

    private val _subscriptionPlansResponse =
        MutableLiveData<NetworkResult<SubscriptionPlanResponse>>()
    val subscriptionPlansResponse: LiveData<NetworkResult<SubscriptionPlanResponse>> =
        _subscriptionPlansResponse

    val planModes = MutableLiveData<List<PlanData>>()
    val selectedPlanMode = MutableLiveData<PlanData>()
    val planTypes = MutableLiveData<List<SubscriptionType>>()
    val selectedPlanType = MutableLiveData<SubscriptionType>()

    val currentPlanIndex = MutableLiveData<Int>(0)

    // Status/Loading
    val isLoading = MutableLiveData<Boolean>(false)
    val errorMessage = MutableLiveData<String?>()

    // LiveData to trigger payment process in Activity with order details
    private val _paymentOrderResponse = MutableLiveData<PaymentCreateOrderResponse?>()
    val paymentOrderResponse: LiveData<PaymentCreateOrderResponse?> = _paymentOrderResponse

    // LiveData to trigger navigation after successful payment
    val navigateToAddBranch = MutableLiveData<Boolean>()

    private fun getString(resId: Int): String {
        return LibraroApp.instance.getString(resId)
    }

    init {
        fetchSubscriptionPlans()
    }

    fun fetchSubscriptionPlans() {
        isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getSubscriptionPlans()) {
                is NetworkResult.Success -> {
                    _subscriptionPlansResponse.value = result
                    val types = result.data?.data?.subscriptionType?.filterNotNull() ?: emptyList()
                    if (types.isNotEmpty()) {
                        planTypes.value = types
                        selectedPlanType.value = types[0]
                        selectedPlanType.value?.let {
                            onPlanTypeSelected(it)
                        }
                    }
                }

                is NetworkResult.Error -> {
                    errorMessage.value = result.message
                }

                is NetworkResult.Unauthorized -> {
                    errorMessage.value = result.message
                }

                else -> {
                    errorMessage.value = getString(R.string.error_something_went_wrong_later)
                }
            }
            isLoading.value = false
        }
    }

    fun onPlanTypeSelected(type: SubscriptionType) {
        selectedPlanType.value = type
        val plans = _subscriptionPlansResponse.value?.data?.data?.subscriptionPlan?.filterNotNull()
            ?: emptyList()

        planModes.value = plans.find {
            it.name.equals(
                selectedPlanType.value?.name,
                ignoreCase = true
            )
        }?.plans?.filterNotNull() ?: emptyList()

        if (selectedPlanType.value != null && planModes.value?.isNotEmpty() == true) {
            selectedPlanMode.value = planModes.value?.get(0)
        }
        currentPlanIndex.value = 0
    }

    fun updateCurrentPlanMode(position: Int) {
        if (!planModes.value.isNullOrEmpty() && position < planModes.value!!.size) {
            selectedPlanMode.value = planModes.value?.get(position)
        }
    }

    fun onMakePaymentClicked() {
        if (selectedPlanType.value != null && selectedPlanMode.value != null) {
            val planTypeId = selectedPlanType.value?.id
            val planModeId = selectedPlanMode.value?.id
            if (planTypeId != null && planModeId != null) {
                createPaymentOrder(
                    planTypeId = planTypeId.toString(),
                    planModeId = planModeId.toString()
                )
            }
        }
    }

    private fun createPaymentOrder(planTypeId: String, planModeId: String) {
        isLoading.value = true
        viewModelScope.launch {
            val result =
                authRepository.createPaymentOrder(planTypeId = planTypeId, planModeId = planModeId)
            isLoading.value = false
            when (result) {
                is NetworkResult.Success -> {
                    if (result.data?.status == true) {
                        _paymentOrderResponse.value = result.data
                    } else {
                        errorMessage.value =
                            result.data?.message ?: "Failed to create payment order"
                    }
                }

                is NetworkResult.Error -> {
                    errorMessage.value = result.message
                }

                is NetworkResult.Unauthorized -> {
                    errorMessage.value = result.message
                }

                else -> {
                    errorMessage.value = getString(R.string.error_something_went_wrong_later)
                }
            }

        }
    }

    fun verifyOrder(
        razorpayPaymentId: String,
        razorpayOrderId: String,
        razorpaySignature: String,
        paymentStatus: String,
        paymentResponse: String
    ) {
        isLoading.value = true
        viewModelScope.launch {
            val result =
                authRepository.verifyPayment(
                    _paymentOrderResponse.value?.data?.transactionId.toString(),
                    razorpayPaymentId,
                    razorpayOrderId,
                    razorpaySignature,
                    paymentStatus,
                    paymentResponse
                )
            isLoading.value = false
            when (result) {
                is NetworkResult.Success -> {
                    if (result.data?.status == true) {
                        errorMessage.value = result.data.message
                        navigateToAddBranch.value = true
                    } else {
                        errorMessage.value =
                            result.data?.message ?: "Failed to verify payment"
                    }
                    onPaymentHandled()
                }

                is NetworkResult.Error -> {
                    onPaymentHandled()
                    errorMessage.value = result.message
                }

                is NetworkResult.Unauthorized -> {
                    onPaymentHandled()
                    errorMessage.value = result.message
                }

                else -> {
                    onPaymentHandled()
                    errorMessage.value = getString(R.string.error_something_went_wrong_later)
                }
            }

        }
    }

    fun onPaymentHandled() {
        _paymentOrderResponse.value = null
    }

    fun onNavigationHandled() {
        navigateToAddBranch.value = false
    }

    fun onErrorHandled() {
        errorMessage.value = null
    }
}
