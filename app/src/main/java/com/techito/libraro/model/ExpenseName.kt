package com.techito.libraro.model

import java.io.Serializable

data class ExpenseName(
    val id: Int,
    val name: String,
    val isActive: Boolean = true
) : Serializable
