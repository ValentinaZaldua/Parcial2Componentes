package com.example.parcial2pc.models

// Representa un integrante que aporta a una meta de ahorro
data class Member(
    val id: String = "",
    val name: String = "",
    val goalId: String = "",
    val totalPaid: Double = 0.0  // se puede calcular sumando sus pagos
)