package com.example.stashed.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_goals")
data class BudgetGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val categoryId: Int? = null,    // null = it's the overall monthly goal
    val minimumGoal: Double = 0.0,  // Minimum target spend
    val maximumGoal: Double,        // Maximum spend allowed
    val month: Int,                 // 1-12
    val year: Int
)