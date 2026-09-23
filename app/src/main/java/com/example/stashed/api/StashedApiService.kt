package com.example.stashed.api

import com.example.stashed.data.entities.Expense
import com.example.stashed.data.entities.Category
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StashedApiService {

    // Example: Fetch all expenses for a specific user from your backend
    @GET("users/{userId}/expenses")
    suspend fun getUserExpenses(@Path("userId") userId: Int): Response<List<Expense>>

    // Example: Sync a new expense to the cloud/database
    @POST("expenses/sync")
    suspend fun syncExpense(@Body expense: Expense): Response<Void>

    // Example: Fetch categories
    @GET("users/{userId}/categories")
    suspend fun getUserCategories(@Path("userId") userId: Int): Response<List<Category>>
}