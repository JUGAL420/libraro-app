package com.techito.libraro.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techito.libraro.data.remote.RetrofitClient
import com.techito.libraro.model.BranchConfigurationBranchDetails
import com.techito.libraro.model.BranchConfigurationBranchMaster
import com.techito.libraro.model.BranchConfigurationFloor
import com.techito.libraro.model.BranchConfigurationPlan
import com.techito.libraro.model.BranchConfigurationRequest
import com.techito.libraro.model.BranchConfigurationShift
import com.techito.libraro.model.StaticDataListResponse
import com.techito.libraro.model.StaticMonthlyOption
import com.techito.libraro.repository.AuthRepository
import com.techito.libraro.repository.MainRepository
import com.techito.libraro.utils.NetworkResult
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

class BranchConfigurationViewModel : ViewModel() {

    private val repository = MainRepository(RetrofitClient.apiService)
    private val authRepository = AuthRepository(RetrofitClient.userAuthApiService)

    // Step 2: Branch & Floor Details
    val branchName = MutableLiveData<String>("")
    val founderDay = MutableLiveData<String>("")
    val emailId = MutableLiveData<String>("")
    val contactNo = MutableLiveData<String>("")
    val upiId = MutableLiveData<String>("")

    val totalSeats = MutableLiveData<String>("")
    val operatingHrs = MutableLiveData<String>("10")
    val lockerAmt = MutableLiveData<String>("")
    val extendDays = MutableLiveData<String>("")

    val planDays = MutableLiveData<BranchConfigurationPlan?>()

    val floors = MutableLiveData<MutableList<BranchConfigurationFloor>>(
        mutableListOf(BranchConfigurationFloor("", null, null))
    )

    // Step 3: Shifts
    val shifts = MutableLiveData<MutableList<BranchConfigurationShift>>(
        mutableListOf(BranchConfigurationShift("", null, "", null, "", ""))
    )

    // UI State & Master Data
    val staticData = MutableLiveData<StaticDataListResponse?>()
    val monthlyOptions = MutableLiveData<MutableList<StaticMonthlyOption>>()
    val planDaysLabels = MutableLiveData<List<String>>()
    val isLoading = MutableLiveData<Boolean>(false)
    val errorMessage = MutableLiveData<String?>()

    val navigateToAddShifts = MutableLiveData<Boolean>(false)
    val configurationSuccess = MutableLiveData<Boolean>(false)

    val planTypeNames = MutableLiveData<List<String>>(emptyList())


    fun fetchMasterStaticData() {
        isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getMasterStaticDataList()) {
                is NetworkResult.Success -> {
                    staticData.value = result.data
                    planTypeNames.value =
                        result.data?.data?.planTypes?.mapNotNull { it?.name } ?: emptyList()
                }

                is NetworkResult.Error -> errorMessage.value = result.message
                else -> errorMessage.value = "Unable to fetch master data"
            }
            isLoading.value = false
        }
    }

    fun addFloor() {
        val list = floors.value ?: mutableListOf()
        list.add(BranchConfigurationFloor("", null, null))
        floors.value = list
    }

    fun removeFloor(position: Int) {
        val list = floors.value ?: mutableListOf()
        if (list.size > 1) {
            list.removeAt(position)
            floors.value = list
        }
    }

    fun addShift() {
        val list = shifts.value ?: mutableListOf()
        list.add(BranchConfigurationShift("", null, "", null, "", ""))
        shifts.value = list
    }

    fun removeShift(position: Int) {
        val list = shifts.value ?: mutableListOf()
        if (list.size > 1) {
            list.removeAt(position)
            shifts.value = list
        }
    }

    fun calculateDuration(position: Int) {
        val list = shifts.value ?: return
        val shift = list.getOrNull(position) ?: return

        if (shift.startTime.isNullOrBlank() || shift.endTime.isNullOrBlank()) return

        try {
            // format is 12 hours with AM/PM (matching your picker)
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())

            val start = sdf.parse(shift.startTime!!)
            val end = sdf.parse(shift.endTime!!)

            if (start != null && end != null) {

                var diffMillis = end.time - start.time

                // Handle overnight case
                if (diffMillis < 0) {
                    diffMillis += 24L * 60 * 60 * 1000
                }

                val totalMinutes = diffMillis / (1000 * 60)

                // Convert to decimal hours
                val hours = totalMinutes / 60.0
                val duration = String.format(Locale.US, "%.2f", hours)

                // update only given position
                shift.durationHours = duration

                list[position] = shift
                shifts.value = list
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateShiftType(position: Int, type: String) {
        val shiftsList = shifts.value ?: return
        val shift = shiftsList.getOrNull(position) ?: return
        shift.type = type
        // If "Custom" is selected, clear customName so user can type.
        // Otherwise, customName matches the shift type.
        if (type.equals("Custom", ignoreCase = true)) {
            shift.customName = ""
        } else {
            shift.customName = type
        }
        
        shiftsList[position] = shift
        shifts.value = shiftsList
    }

    fun onSaveAndNextClicked() {
        val mobile = contactNo.value?.replace(" ", "")
            ?.removePrefix("+91")
            ?.removePrefix("91") ?: ""

        if (branchName.value.isNullOrBlank() || founderDay.value.isNullOrBlank() || emailId.value.isNullOrBlank() || contactNo.value.isNullOrBlank()) {
            errorMessage.value = "Branch Name, Founder's Day, Email, Contact No. cannot be empty"
            return
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailId.value?.trim() ?: "")
                .matches()
        ) {
            errorMessage.value = "Please enter valid email address"
            return
        } else if (mobile.length != 10) {
            errorMessage.value = "Please enter a valid 10-digit mobile number"
            return
        } else if (totalSeats.value.isNullOrBlank() || operatingHrs.value.isNullOrBlank() || lockerAmt.value.isNullOrBlank() || extendDays.value.isNullOrBlank()) {
            errorMessage.value = "Branch master details are mandatory. Please fill all the details"
            return
        } else if (planDays.value == null || planDays.value?.planName.isNullOrBlank()) {
            errorMessage.value = "Please enter plan data"
            return
        }

        val totalBranchSeats = totalSeats.value?.toIntOrNull() ?: 0
        val currentFloors = floors.value ?: mutableListOf()
        var totalFloorSeats = 0

        currentFloors.forEachIndexed { index, floor ->
            val hasSomeData = !floor.floorName.isNullOrBlank() || floor.seatFrom != null || floor.seatTo != null
            val isMandatory = currentFloors.size > 1

            if (isMandatory || hasSomeData) {
                if (floor.floorName.isNullOrBlank() || floor.seatFrom == null || floor.seatTo == null) {
                    errorMessage.value = if (isMandatory) "Please complete all details for floor ${index + 1}."
                    else "Enter all floor details."
                    return
                } else if (floor.seatFrom!! > floor.seatTo!!) {
                    errorMessage.value = "Please enter valid seat range for floor ${index + 1}"
                    return
                }
                totalFloorSeats += (floor.seatTo!! - floor.seatFrom!! + 1)
            }
        }

        if (totalFloorSeats > totalBranchSeats) {
            errorMessage.value = "Floors seats exceed branch capacity."
            return
        }

        navigateToAddShifts.value = true
    }

    fun onFinishSetupClicked() {
        Log.e("Branch Data >>>> ", branchName.value ?: "")
        val currentShifts = shifts.value ?: emptyList()
        if (currentShifts.isEmpty()) {
            errorMessage.value = "Please add at least one shift"
            return
        }
        var totalShiftHours = 0.0
        currentShifts.forEachIndexed { index, shift ->
            if (shift.startTime.isNullOrBlank() || shift.endTime.isNullOrBlank() || shift.price.isNullOrBlank() || shift.type.isNullOrBlank()) {
                errorMessage.value = "Please fill all details for Shift ${index + 1}"
                return
            }
            
            // Required Custom Name validation
            if (shift.customName.isNullOrBlank()) {
                errorMessage.value = "Please enter Custom Name for Shift ${index + 1}"
                return
            }
            
            totalShiftHours += shift.durationHours?.toDoubleOrNull() ?: 0.0
        }
        val branchHours = operatingHrs.value?.toDoubleOrNull() ?: 0.0
        // Validation: Total shifts duration must match branch operating hours
        if (abs(totalShiftHours - branchHours) > 0.01) {
            errorMessage.value = String.format(
                Locale.getDefault(),
                "Total shifts duration (%.2f hrs) must match branch operating hours (%.2f hrs)",
                totalShiftHours,
                branchHours
            )
            return
        }
        submitConfiguration()
    }

    private fun submitConfiguration() {
        isLoading.value = true
        viewModelScope.launch {
            // Filter out empty floors if only one was provided but not filled
            val floorsToSubmit = floors.value?.filter {
                !it.floorName.isNullOrBlank() && it.seatFrom != null && it.seatTo != null
            }

            // Create a copy of shifts and convert time format to HH:mm (24-hour)
            val inputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val shiftsToSubmit = shifts.value?.map { shift ->
                shift.copy(
                    startTime = try {
                        val date = inputFormat.parse(shift.startTime ?: "")
                        if (date != null) outputFormat.format(date) else shift.startTime
                    } catch (e: Exception) {
                        shift.startTime
                    },
                    endTime = try {
                        val date = inputFormat.parse(shift.endTime ?: "")
                        if (date != null) outputFormat.format(date) else shift.endTime
                    } catch (e: Exception) {
                        shift.endTime
                    }
                )
            }

            val request = BranchConfigurationRequest(
                branchDetails = BranchConfigurationBranchDetails(
                    branchName = branchName.value,
                    contactNumber = contactNo.value,
                    email = emailId.value,
                    foundedDate = founderDay.value,
                    upiId = upiId.value
                ),
                branchMaster = BranchConfigurationBranchMaster(
                    extendDays = extendDays.value?.toIntOrNull(),
                    lockerAmount = lockerAmt.value ?: "",
                    operatingHours = operatingHrs.value?.toIntOrNull(),
                    totalSeats = totalSeats.value?.toIntOrNull()
                ),
                floors = floorsToSubmit,
                plan = planDays.value,
                shifts = shiftsToSubmit
            )

            when (val result = authRepository.saveBranchConfiguration(request)) {
                is NetworkResult.Success -> {
                    errorMessage.value = result.message
                    configurationSuccess.value = true
                }

                is NetworkResult.Error -> errorMessage.value = result.message
                else -> errorMessage.value = "Failed to submit configuration"
            }
            isLoading.value = false
        }
    }

    fun onNavigationHandled() {
        navigateToAddShifts.value = false
    }

    fun onErrorHandled() {
        errorMessage.value = null
    }
}
