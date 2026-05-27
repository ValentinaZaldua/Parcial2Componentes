package com.example.parcial2pc.models

import com.google.gson.annotations.SerializedName

data class GoalCreateRequest(
    val name: String,
    val description: String,
    val totalValue: Double,
    val targetDate: String,
    val imageUrl: String? = null,
    @SerializedName("membersReq")
    val members: List<String>
)
