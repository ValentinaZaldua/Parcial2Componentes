package com.example.parcial2pc

import com.example.parcial2pc.models.Goal
import com.example.parcial2pc.models.Member
import org.junit.Test
import org.junit.Assert.*

// Prueba unitaria sobre la lógica del modelo Goal
// Valida el cálculo de progreso y aporte equitativo
class GoalUnitTest {

    @Test
    fun `progreso correcto cuando hay aportes`() {
        val goal = Goal(
            id = "1",
            name = "Televisor",
            totalValue = 3000000.0,
            totalSaved = 1200000.0,
            members = listOf(
                Member(id = "1", name = "Jorge", goalId = "1"),
                Member(id = "2", name = "Ana", goalId = "1")
            )
        )
        assertEquals(40, goal.progressPercent)
    }

    @Test
    fun `progreso no supera 100 aunque se sobrepase la meta`() {
        val goal = Goal(
            id = "1",
            name = "Moto",
            totalValue = 1000000.0,
            totalSaved = 1500000.0
        )
        assertEquals(100, goal.progressPercent)
    }

    @Test
    fun `aporte equitativo se divide correctamente entre miembros`() {
        val goal = Goal(
            id = "1",
            name = "Carro",
            totalValue = 40000000.0,
            members = listOf(
                Member(id = "1", name = "Luis", goalId = "1"),
                Member(id = "2", name = "Maria", goalId = "1"),
                Member(id = "3", name = "Pedro", goalId = "1"),
                Member(id = "4", name = "Ana", goalId = "1")
            )
        )
        assertEquals(10000000.0, goal.equitableContribution, 0.0)
    }

    @Test
    fun `meta sin miembros retorna aporte equitativo cero`() {
        val goal = Goal(
            id = "1",
            name = "Viaje",
            totalValue = 8000000.0,
            members = emptyList()
        )
        assertEquals(0.0, goal.equitableContribution, 0.0)
    }
}