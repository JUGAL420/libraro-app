package com.techito.libraro.model
import com.google.gson.annotations.SerializedName

data class BranchConfigurationRequest(
    @SerializedName("branch_details")
    var branchDetails: BranchConfigurationBranchDetails?,
    @SerializedName("branch_master")
    var branchMaster: BranchConfigurationBranchMaster?,
    @SerializedName("floors")
    var floors: List<BranchConfigurationFloor?>?,
    @SerializedName("plan")
    var plan: BranchConfigurationPlan?,
    @SerializedName("shifts")
    var shifts: List<BranchConfigurationShift?>?
)

data class BranchConfigurationBranchDetails(
    @SerializedName("branch_name")
    var branchName: String?,
    @SerializedName("contact_number")
    var contactNumber: String?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("founded_date")
    var foundedDate: String?,
    @SerializedName("upi_id")
    var upiId: String?
)

data class BranchConfigurationBranchMaster(
    @SerializedName("extend_days")
    var extendDays: Int?,
    @SerializedName("locker_amount")
    var lockerAmount: Int?,
    @SerializedName("operating_hours")
    var operatingHours: Int?,
    @SerializedName("total_seats")
    var totalSeats: Int?
)

data class BranchConfigurationFloor(
    @SerializedName("floor_name")
    var floorName: String?,
    @SerializedName("seat_from")
    var seatFrom: Int?,
    @SerializedName("seat_to")
    var seatTo: Int?
)

data class BranchConfigurationPlan(
    @SerializedName("days")
    var days: String?,
    @SerializedName("plan_name")
    var planName: String?
)

data class BranchConfigurationShift(
    @SerializedName("custom_name")
    var customName: String?,
    @SerializedName("duration_hours")
    var durationHours: String?,
    @SerializedName("end_time")
    var endTime: String?,
    @SerializedName("price")
    var price: Int?,
    @SerializedName("start_time")
    var startTime: String?,
    @SerializedName("type")
    var type: String?
)
