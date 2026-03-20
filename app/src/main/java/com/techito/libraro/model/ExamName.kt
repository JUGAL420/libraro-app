package com.techito.libraro.model

import java.io.Serializable

data class ExamName(
    val id: Int,
    val name: String,
    val isActive: Boolean = true
) : Serializable
