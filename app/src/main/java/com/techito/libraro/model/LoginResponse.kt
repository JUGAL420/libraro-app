package com.techito.libraro.model
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    var `data`: RegisterData?,
    @SerializedName("is_email_verified")
    var isEmailVerified: Int?,
    @SerializedName("is_last_step")
    var isLastStep: Int?,
    @SerializedName("library_id")
    var libraryId: Int?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?,
    @SerializedName("token")
    var token: String?,
    @SerializedName("user_type")
    var userType: String?
)


