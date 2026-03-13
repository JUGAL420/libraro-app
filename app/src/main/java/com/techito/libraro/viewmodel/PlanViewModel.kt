package com.techito.libraro.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.techito.libraro.model.Plan
import com.techito.libraro.model.PlanMode

class PlanViewModel : ViewModel() {

    val planModes = MutableLiveData<List<PlanMode>>()
    val selectedPlanMode = MutableLiveData<PlanMode>()
    val currentPlanIndex = MutableLiveData<Int>(0)
    
    // LiveData to trigger navigation
    val navigateToAddBranch = MutableLiveData<Boolean>()

    init {
        loadPlans()
    }

    private fun loadPlans() {
        val basicBenefits = listOf(
            "Unlimited Seat Booking",
            "Seat Re-New",
            "Swap Seat (Change Seat)",
            "Change Plan (Allow till 7 days)",
            "Upgrade Plan (Allow last 5 days)",
            "Edit Profile",
            "Close Plan Any time",
            "Delete Learner (Soft Delete)",
            "Restore Learner"
        )

        val monthlyPlans = listOf(
            Plan("1", "Basic Plan", "49", basicBenefits),
            Plan("2", "Premium Plan", "99", basicBenefits + "Priority Support"),
            Plan("3", "Ultimate Plan", "149", basicBenefits + "Custom Branding")
        )

        val yearlyPlans = listOf(
            Plan("4", "Basic Yearly", "499", basicBenefits),
            Plan("5", "Premium Yearly", "999", basicBenefits + "Priority Support")
        )

        val modes = listOf(
            PlanMode("Monthly", monthlyPlans),
            PlanMode("Yearly", yearlyPlans)
        )

        planModes.value = modes
        selectedPlanMode.value = modes[0]
    }

    fun onPlanModeSelected(mode: PlanMode) {
        selectedPlanMode.value = mode
        currentPlanIndex.value = 0
    }

    fun onMakePaymentClicked() {
        // Trigger the navigation event
        navigateToAddBranch.value = true
    }
    
    fun onNavigationHandled() {
        navigateToAddBranch.value = false
    }
}
