package com.techito.libraro.model

data class Floor(
    var id: Int,
    var floorName: String = "",
    var seatNoFrom: String = "",
    var seatNoTo: String = ""
)
