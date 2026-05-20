package com.ud.riddle.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ud.riddle.models.Goal
import com.ud.riddle.models.Member
import com.ud.riddle.models.states.GoalState
import com.ud.riddle.repositories.SavingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Igual al GameViewModel del proyecto base.
// La UI solo observa _uiState y _selectedGoal. Nunca ejecuta lógica aquí.
class GoalViewModel : ViewModel() {

    private val repository = SavingsRepository()

    private val _uiState = MutableStateFlow<GoalState>(GoalState.Idle)
    val uiState: StateFlow<GoalState> = _uiState.asStateFlow()

    private val _selectedGoal = MutableStateFlow<Goal?>(null)
    val selectedGoal: StateFlow<Goal?> = _selectedGoal.asStateFlow()

    // Carga la lista de metas desde la API
    fun loadGoals() {
        _uiState.value = GoalState.Loading
        viewModelScope.launch {
            repository.getGoals()
                .onSuccess { _uiState.value = GoalState.Success(it) }
                .onFailure { _uiState.value = GoalState.Error(it.message ?: "Error") }
        }
    }

    // Carga el detalle de una meta específica
    fun loadGoalDetail(id: String) {
        viewModelScope.launch {
            repository.getGoalById(id)
                .onSuccess { _selectedGoal.value = it }
                .onFailure { /* manejar */ }
        }
    }

    // Crea una nueva meta y recarga la lista
    fun createGoal(goal: Goal) {
        viewModelScope.launch {
            repository.createGoal(goal)
                .onSuccess { loadGoals() }
                .onFailure { _uiState.value = GoalState.Error(it.message ?: "Error al crear") }
        }
    }

    // Agrega un miembro a una meta y recarga el detalle
    fun addMember(member: Member, goalId: String) {
        viewModelScope.launch {
            repository.addMember(member)
                .onSuccess { loadGoalDetail(goalId) }
                .onFailure { /* manejar */ }
        }
    }
}