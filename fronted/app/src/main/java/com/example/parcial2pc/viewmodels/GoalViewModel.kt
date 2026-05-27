package com.example.parcial2pc.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2pc.models.Goal
import com.example.parcial2pc.models.GoalCreateRequest
import com.example.parcial2pc.models.Member
import com.example.parcial2pc.models.Payment
import com.example.parcial2pc.models.states.GoalState
import com.example.parcial2pc.repositories.SavingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalViewModel : ViewModel() {

    private val repository = SavingsRepository()

    private val _uiState = MutableStateFlow<GoalState>(GoalState.Idle)
    val uiState: StateFlow<GoalState> = _uiState.asStateFlow()

    private val _createGoalSuccess = MutableStateFlow(false)
    val createGoalSuccess: StateFlow<Boolean> = _createGoalSuccess.asStateFlow()

    private val _paymentSuccess = MutableStateFlow(false)
    val paymentSuccess: StateFlow<Boolean> = _paymentSuccess.asStateFlow()

    private val _selectedGoal = MutableStateFlow<Goal?>(null)
    val selectedGoal: StateFlow<Goal?> = _selectedGoal.asStateFlow()

    private val _paymentsForGoal = MutableStateFlow<List<Payment>>(emptyList())
    val paymentsForGoal: StateFlow<List<Payment>> = _paymentsForGoal.asStateFlow()

    // Imagen pendiente de subir tras crear la meta
    var pendingImageFile: java.io.File? = null

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

    fun createGoal(request: GoalCreateRequest) {
        viewModelScope.launch {
            _uiState.value = GoalState.Loading
            repository.createGoal(request)
                .onSuccess { newGoal ->
                    // Si hay imagen pendiente la sube automáticamente
                    pendingImageFile?.let { file ->
                        uploadImage(newGoal.id, file)
                        pendingImageFile = null
                    }
                    loadGoals()
                    _createGoalSuccess.value = true
                }
                .onFailure { _uiState.value = GoalState.Error(it.message ?: "Error al crear") }
        }
    }

    fun resetCreateGoalStatus() {
        _createGoalSuccess.value = false
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
                    loadGoalDetail(payment.goalId)
                    loadPaymentsForGoal(payment.goalId)
                    _paymentSuccess.value = true
                }
                .onFailure { }
        }
    }

    fun resetPaymentStatus() {
        _paymentSuccess.value = false
    }

    fun loadPaymentsForGoal(goalId: String) {
        viewModelScope.launch {
            repository.getPayments(goalId)
                .onSuccess { _paymentsForGoal.value = it }
                .onFailure { _paymentsForGoal.value = emptyList() }
        }
    }

    fun uploadImage(goalId: String, imageFile: java.io.File) {
        viewModelScope.launch {
            repository.uploadImage(goalId, imageFile)
                .onSuccess { loadGoals() }
                .onFailure { }
        }
    }
}