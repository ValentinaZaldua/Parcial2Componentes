package com.ud.riddle.service

import com.ud.riddle.models.Goal
import com.ud.riddle.models.GoalCreateRequest
import com.ud.riddle.models.Member
import com.ud.riddle.models.Payment
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// Define todos los endpoints del backend.
interface SavingsApiService {

    @GET("goals")
    suspend fun getGoals(): List<Goal>

    @GET("goals/{id}")
    suspend fun getGoalById(@Path("id") id: String): Goal

    @GET("goals/{id}/payments")
    suspend fun getPaymentsByGoal(@Path("id") goalId: String): List<Payment>

    @POST("goals")
    suspend fun createGoal(@Body goal: GoalCreateRequest): Goal

    @POST("members")
    suspend fun addMember(@Body member: Member): Member

    @POST("payments")
    suspend fun registerPayment(@Body payment: Payment): Payment

    companion object {
        private const val BASE_URL = "http://10.0.2.2:3000/"

        fun create(): SavingsApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SavingsApiService::class.java)
        }
    }
}