package com.ud.riddle.models.states

// Igual al patrón GameUiState del proyecto original.
// Representa los posibles estados de la pantalla.
sealed class GoalState {
    object Idle : GoalState()
    object Loading : GoalState()
    data class Success(val goals: List<com.ud.riddle.models.Goal>) : GoalState()
    data class Error(val message: String) : GoalState()
}