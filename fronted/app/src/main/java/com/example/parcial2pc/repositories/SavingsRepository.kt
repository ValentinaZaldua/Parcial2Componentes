package com.ud.riddle.repositories

import com.ud.riddle.models.Goal
import com.ud.riddle.models.Member
import com.ud.riddle.models.Payment
import com.ud.riddle.service.SavingsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Intermediario entre Retrofit (API) y el ViewModel.
// El ViewModel nunca habla directo con Retrofit, siempre pasa por aquí.
// Mismo patrón que GameDiscoveryRepository del proyecto base.
class SavingsRepository(
    private val api: SavingsApiService = SavingsApiService.create()
) {

    suspend fun getGoals(): Result<List<Goal>> = withContext(Dispatchers.IO) {
        try { Result.success(api.getGoals()) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getGoalById(id: String): Result<Goal> = withContext(Dispatchers.IO) {
        try { Result.success(api.getGoalById(id)) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getPayments(goalId: String): Result<List<Payment>> = withContext(Dispatchers.IO) {
        try { Result.success(api.getPaymentsByGoal(goalId)) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun createGoal(goal: Goal): Result<Goal> = withContext(Dispatchers.IO) {
        try { Result.success(api.createGoal(goal)) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun addMember(member: Member): Result<Member> = withContext(Dispatchers.IO) {
        try { Result.success(api.addMember(member)) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun registerPayment(payment: Payment): Result<Payment> = withContext(Dispatchers.IO) {
        try { Result.success(api.registerPayment(payment)) }
        catch (e: Exception) { Result.failure(e) }
    }
}