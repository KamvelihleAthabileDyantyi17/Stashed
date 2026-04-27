package com.example.stashed.data.dao

import androidx.room.*
import com.example.stashed.data.entities.BudgetGoal

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBudget(budget: BudgetGoal)

    @Query("SELECT * FROM budget_goals WHERE userId = :userId AND month = :month AND year = :year LIMIT 1")
    suspend fun getBudgetForMonth(userId: Int, month: Int, year: Int): BudgetGoal?
}