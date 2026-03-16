package com.techito.libraro.model
import com.google.gson.annotations.SerializedName

data class SubscriptionPlanResponse(
    @SerializedName("data")
    var `data`: SubscriptionPlanData?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?
)

data class SubscriptionPlanData(
    @SerializedName("subscription_plan")
    var subscriptionPlan: List<SubscriptionPlan?>?,
    @SerializedName("subscription_type")
    var subscriptionType: List<SubscriptionType?>?
)

data class SubscriptionPlan(
    @SerializedName("name")
    var name: String?,
    @SerializedName("plans")
    var plans: List<PlanData?>?
)

data class SubscriptionType(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("name")
    var name: String?
)

data class PlanData(
    @SerializedName("features")
    var features: List<PlanFeature?>?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("original_price")
    var originalPrice: String?,
    @SerializedName("price")
    var price: String?
)

data class PlanFeature(
    @SerializedName("enabled")
    var enabled: Boolean?,
    @SerializedName("name")
    var name: String?
)


