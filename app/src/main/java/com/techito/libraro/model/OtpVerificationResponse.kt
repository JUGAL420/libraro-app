package com.techito.libraro.model

import com.google.gson.annotations.SerializedName

data class OtpVerificationResponse(
    @SerializedName("data")
    var `data`: RegisterData?,
    @SerializedName("is_email_verified")
    var isEmailVerified: Int?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?,
    @SerializedName("token")
    var token: String?,
    @SerializedName("user_type")
    var userType: String?
)




