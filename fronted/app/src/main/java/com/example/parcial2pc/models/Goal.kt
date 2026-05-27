package com.example.parcial2pc.models

// Modelo que representa una meta de ahorro familiar.
// totalSaved y members los calcula el backend y los incluye en la respuesta.
data class Goal(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val totalValue: Double = 0.0,
    val targetDate: String = "",
    val imageUrl: String? = null,
    val members: List<Member> = emptyList(),
    val totalSaved: Double = 0.0
) {
    // Porcentaje de progreso calculado en el cliente
    val progressPercent: Int
        get() = if (totalValue > 0) ((totalSaved / totalValue) * 100).toInt().coerceAtMost(100) else 0

    // Cuánto debe aportar cada miembro si el aporte es equitativo
    val equitableContribution: Double
        get() = if (members.isNotEmpty()) totalValue / members.size else 0.0
}