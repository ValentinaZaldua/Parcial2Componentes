package com.example.parcial2pc.service

import com.example.parcial2pc.models.Goal
import com.example.parcial2pc.models.GoalCreateRequest
import com.example.parcial2pc.models.Member
import com.example.parcial2pc.models.Payment
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

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

    @Multipart
    @POST("goals/{id}/image")
    suspend fun uploadGoalImage(
        @Path("id") goalId: String,
        @Part image: MultipartBody.Part
    ): Goal

    companion object {
        private const val BASE_URL = "http://192.168.20.33:3000/"

        fun create(): SavingsApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SavingsApiService::class.java)
        }
    }
}
