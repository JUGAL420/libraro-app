package com.techito.libraro.model

data class Plan(
    val id: String,
    val name: String,
    val price: String,
    val benefits: List<String>
)
