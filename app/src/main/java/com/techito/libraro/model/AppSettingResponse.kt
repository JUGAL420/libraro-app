package com.techito.libraro.model
import com.google.gson.annotations.SerializedName

data class AppSettingResponse(
    @SerializedName("data")
    var `data`: AppSettingData?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?
)

data class AppSettingData(
    @SerializedName("app_version")
    var appVersion: String?,
    @SerializedName("contact_email")
    var contactEmail: List<String?>?,
    @SerializedName("contact_number")
    var contactNumber: List<String?>?,
    @SerializedName("facebook")
    var facebook: String?,
    @SerializedName("force_update")
    var forceUpdate: Boolean?,
    @SerializedName("instagram")
    var instagram: String?,
    @SerializedName("isMaintenance")
    var isMaintenance: Boolean?,
    @SerializedName("learner_sample")
    var learnerSample: String?,
    @SerializedName("linkedin")
    var linkedin: String?,
    @SerializedName("master_sample")
    var masterSample: String?,
    @SerializedName("privacy_policy")
    var privacyPolicy: String?,
    @SerializedName("terms_and_conditions")
    var termsAndConditions: String?,
    @SerializedName("whatsapp")
    var whatsapp: String?,
    @SerializedName("youtube")
    var youtube: String?
)


