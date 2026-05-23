package com.ud.riddle.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ud.riddle.models.Goal
import com.ud.riddle.models.Member
import com.ud.riddle.models.Payment
import com.ud.riddle.models.states.GoalState
import com.ud.riddle.repositories.SavingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalViewModel : ViewModel() {

    private val repository = SavingsRepository()

    private val _uiState = MutableStateFlow<GoalState>(GoalState.Idle)
    val uiState: StateFlow<GoalState> = _uiState.asStateFlow()

    private val _selectedGoal = MutableStateFlow<Goal?>(null)
    val selectedGoal: StateFlow<Goal?> = _selectedGoal.asStateFlow()

    private val _paymentsForGoal = MutableStateFlow<List<Payment>>(emptyList())
    val paymentsForGoal: StateFlow<List<Payment>> = _paymentsForGoal.asStateFlow()

    fun loadGoals() {
        _uiState.value = GoalState.Loading
        viewModelScope.launch {
            repository.getGoals()
                .onSuccess { _uiState.value = GoalState.Success(it) }
                .onFailure { _uiState.value = GoalState.Error(it.message ?: "Error") }
        }
    }

    fun loadGoalDetail(id: String) {
        viewModelScope.launch {
            repository.getGoalById(id)
                .onSuccess { _selectedGoal.value = it }
                .onFailure { }
        }
    }

    fun createGoal(goal: Goal) {
        viewModelScope.launch {
            repository.createGoal(goal)
                .onSuccess { loadGoals() }
                .onFailure { _uiState.value = GoalState.Error(it.message ?: "Error al crear") }
        }
    }

    fun addMember(member: Member, goalId: String) {
        viewModelScope.launch {
            repository.addMember(member)
                .onSuccess { loadGoalDetail(goalId) }
                .onFailure { }
        }
    }

    fun registerPayment(payment: Payment) {
        viewModelScope.launch {
            repository.registerPayment(payment)
                .onSuccess {
                    // Recarga el detalle para actualizar el progreso y los totales
                    loadGoalDetail(payment.goalId)
                    // Recarga la lista de pagos para que PaymentsListScreen se actualice
                    loadPaymentsForGoal(payment.goalId)
                }
                .onFailure { }
        }
    }

    fun loadPaymentsForGoal(goalId: String) {
        viewModelScope.launch {
            repository.getPayments(goalId)
                .onSuccess { _paymentsForGoal.value = it }
                .onFailure { _paymentsForGoal.value = emptyList() }
        }
    }
}