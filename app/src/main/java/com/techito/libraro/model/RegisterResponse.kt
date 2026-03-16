package com.techito.libraro.model

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("data")
    var `data`: RegisterData?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?
)

data class RegisterData(
    @SerializedName("library_id")
    var libraryId: Int?
)


