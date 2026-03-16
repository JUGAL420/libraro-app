package com.techito.libraro.model
import com.google.gson.annotations.SerializedName

data class StaticDataListResponse(
    @SerializedName("code")
    var code: Int?,
    @SerializedName("data")
    var `data`: StaticDataListData?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?
)

data class StaticDataListData(
    @SerializedName("exams")
    var exams: List<Any?>?,
    @SerializedName("expenses")
    var expenses: List<StaticExpense?>?,
    @SerializedName("features")
    var features: List<StaticFeature?>?,
    @SerializedName("libraryUserRoles")
    var libraryUserRoles: List<StaticLibraryUserRole?>?,
    @SerializedName("monthly_options")
    var monthlyOptions: List<StaticMonthlyOption?>?,
    @SerializedName("plan_duration")
    var planDuration: List<StaticPlanDuration?>?,
    @SerializedName("plan_types")
    var planTypes: List<StaticPlanType?>?
)

data class StaticExpense(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("name")
    var name: String?
)

data class StaticFeature(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("name")
    var name: String?
)

data class StaticLibraryUserRole(
    @SerializedName("guard_name")
    var guardName: String?,
    @SerializedName("name")
    var name: String?
)

data class StaticMonthlyOption(
    @SerializedName("label")
    var label: String?,
    @SerializedName("value")
    var value: String?
)

data class StaticPlanDuration(
    @SerializedName("label")
    var label: String?,
    @SerializedName("value")
    var value: String?
)

data class StaticPlanType(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("name")
    var name: String?
)


