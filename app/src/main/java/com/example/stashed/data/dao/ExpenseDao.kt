package com.example.stashed.data.dao

import androidx.room.*
import com.example.stashed.data.entities.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    // 1. Added the Long return type so the repository knows the new expense's ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    // 2. Added the missing update function
    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // 3. Added missing function to get all expenses for a user
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getExpensesForUser(userId: Int): Flow<List<Expense>>

    // 4. Changed to Flow so your UI updates automatically when you add an expense
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    fun getRecentExpenses(userId: Int, limit: Int = 5): Flow<List<Expense>>

    // 5. Changed to Flow to match repository
    @Query("SELECT * FROM expenses WHERE userId = :userId AND date BETWEEN :startMs AND :endMs ORDER BY date DESC")
    fun getExpensesForMonth(userId: Int, startMs: Long, endMs: Long): Flow<List<Expense>>

    // 6. Added the missing function to calculate specific category totals (crucial for your budget limits!)
    // 6. Fixed to use 'category' (String) instead of 'categoryId'
    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE userId = :userId AND category = :category AND date BETWEEN :startMs AND :endMs")
    suspend fun getTotalForCategoryThisMonth(userId: Int, category: String, startMs: Long, endMs: Long): Double
    // 7. Fixed spelling to "Spend" to perfectly match your StashedRepository
    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE userId = :userId AND date BETWEEN :startMs AND :endMs")
    suspend fun getTotalSpendForMonth(userId: Int, startMs: Long, endMs: Long): Double

    @Query("DELETE FROM expenses WHERE userId = :userId AND date < :beforeDate")
    suspend fun deleteExpensesBefore(userId: Int, beforeDate: Long)

    // 8. NEW: Grab all offline expenses that haven't been synced to Firebase yet
    @Query("SELECT * FROM expenses WHERE isSynced = 0")
    suspend fun getUnsyncedExpenses(): List<Expense>
}