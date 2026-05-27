package com.example.parcial2pc.repositories

import com.example.parcial2pc.models.Goal
import com.example.parcial2pc.models.GoalCreateRequest
import com.example.parcial2pc.models.Member
import com.example.parcial2pc.models.Payment
import com.example.parcial2pc.service.SavingsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

// Intermediario entre Retrofit (API) y el ViewModel.
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

    suspend fun createGoal(request: GoalCreateRequest): Result<Goal> = withContext(Dispatchers.IO) {
        try {
            println("Creating goal request: $request")
            Result.success(api.createGoal(request)) 
        } catch (e: Exception) {
            Result.failure(e) 
        }
    }

    suspend fun addMember(member: Member): Result<Member> = withContext(Dispatchers.IO) {
        try { Result.success(api.addMember(member)) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun registerPayment(payment: Payment): Result<Payment> = withContext(Dispatchers.IO) {
        try { Result.success(api.registerPayment(payment)) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun uploadImage(goalId: String, imageFile: File): Result<Goal> = withContext(Dispatchers.IO) {
        try {
            val requestFile = imageFile.asRequestBody("image/*".toMediaType())
            val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
            Result.success(api.uploadGoalImage(goalId, body))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}