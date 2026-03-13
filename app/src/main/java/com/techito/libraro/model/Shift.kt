package com.techito.libraro.model

data class Shift(
    var id: Int,
    var shiftName: String = "",
    var customName: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var duration: String = "",
    var price: String = ""
)
