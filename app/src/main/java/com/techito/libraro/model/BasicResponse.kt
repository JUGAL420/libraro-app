package com.techito.libraro.model

import com.google.gson.annotations.SerializedName

data class BasicResponse(
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?
)
