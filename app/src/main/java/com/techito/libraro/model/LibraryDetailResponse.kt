package com.techito.libraro.model
import com.google.gson.annotations.SerializedName
data class LibraryDetailResponse(
    @SerializedName("data")
    var `data`: LibraryDetail?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?
)

data class LibraryDetail(
    @SerializedName("active_plan")
    var activePlan: LibraryActivePlan?,
    @SerializedName("branches")
    var branches: List<LibraryBranches?>?,
    @SerializedName("library_email")
    var libraryEmail: String?,
    @SerializedName("library_id")
    var libraryId: Int?,
    @SerializedName("library_mobile")
    var libraryMobile: String?,
    @SerializedName("library_name")
    var libraryName: String?,
    @SerializedName("pyment_upi")
    var pymentUpi: String?
)

data class LibraryActivePlan(
    @SerializedName("end_date")
    var endDate: String?,
    @SerializedName("plan_id")
    var planId: Int?,
    @SerializedName("plan_name")
    var planName: String?,
    @SerializedName("plan_type")
    var planType: String?,
    @SerializedName("price")
    var price: String?,
    @SerializedName("start_date")
    var startDate: String?,
    @SerializedName("status")
    var status: String?
)

data class LibraryBranches(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("name")
    var name: String?
)


