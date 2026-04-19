package com.example.stashed.data.dao

import androidx.room.*
import com.example.stashed.data.entities.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal): Long

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY isComplete ASC, deadline ASC")
    fun getGoalsForUser(userId: Int): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE goalId = :goalId LIMIT 1")
    suspend fun getGoalById(goalId: Int): Goal?

    @Query("UPDATE goals SET isComplete = 1 WHERE goalId = :goalId")
    suspend fun markGoalComplete(goalId: Int)

    @Query("UPDATE goals SET savedAmount = savedAmount + :amount WHERE goalId = :goalId")
    suspend fun addToGoal(goalId: Int, amount: Double)
}
