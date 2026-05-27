package com.example.parcial2pc.models

// Representa un pago que hizo un miembro hacia una meta
data class Payment(
    val id: String = "",
    val memberId: String = "",
    val goalId: String = "",
    val amount: Double = 0.0,
    val method: String = "",       // ej: "Transferencia bancaria"
    val description: String = "",
    val date: String = ""
)