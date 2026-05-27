package com.example.parcial2pc.models

data class GoalCreateRequest(
    val name: String,
    val description: String,
    val totalValue: Double,
    val targetDate: String,
    val imageUrl: String? = null,
    val members: List<String>
)