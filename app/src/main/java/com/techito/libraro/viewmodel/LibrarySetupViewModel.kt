package com.techito.libraro.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.techito.libraro.model.Floor
import com.techito.libraro.model.Shift

class LibrarySetupViewModel : ViewModel() {

    // Branch Details
    val branchName = MutableLiveData<String>()
    val founderDay = MutableLiveData<String>()
    val emailId = MutableLiveData<String>()
    val contactNo = MutableLiveData<String>()
    val upiId = MutableLiveData<String>()

    // Branch Master
    val totalSeats = MutableLiveData<String>()
    val operatingHrs = MutableLiveData<String>()
    val lockerAmt = MutableLiveData<String>()
    val extendDays = MutableLiveData<String>()
    val tokenMoney = MutableLiveData<String>()
    val wantFixEndDate = MutableLiveData<Boolean>(false)

    // Floors
    val floors = MutableLiveData<MutableList<Floor>>(mutableListOf(Floor(0)))

    // Shifts
    val shifts = MutableLiveData<MutableList<Shift>>(mutableListOf(Shift(0)))

    // Navigation LiveData
    val navigateToAddShifts = MutableLiveData<Boolean>()

    fun addFloor() {
        val currentFloors = floors.value ?: mutableListOf()
        currentFloors.add(Floor(currentFloors.size))
        floors.value = currentFloors
    }

    fun removeFloor(position: Int) {
        val currentFloors = floors.value ?: mutableListOf()
        if (currentFloors.size > 1) {
            currentFloors.removeAt(position)
            floors.value = currentFloors
        }
    }

    fun addShift() {
        val currentShifts = shifts.value ?: mutableListOf()
        currentShifts.add(Shift(currentShifts.size))
        shifts.value = currentShifts
    }

    fun removeShift(position: Int) {
        val currentShifts = shifts.value ?: mutableListOf()
        if (currentShifts.size > 1) {
            currentShifts.removeAt(position)
            shifts.value = currentShifts
        }
    }

    fun onSaveAndNextClicked() {
        // Trigger navigation to Add Shifts screen
        navigateToAddShifts.value = true
    }

    fun onNavigationHandled() {
        navigateToAddShifts.value = false
    }

    fun onFinishSetupClicked() {
        // Handle finish setup logic
    }
}
