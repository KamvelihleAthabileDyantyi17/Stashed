package com.example.stashed.data.dao

import androidx.room.*
import com.example.stashed.data.entities.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY dateAdded DESC")
    fun getExpensesForUser(userId: Int): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE userId = :userId AND categoryId = :categoryId ORDER BY dateAdded DESC")
    fun getExpensesByCategory(userId: Int, categoryId: Int): Flow<List<Expense>>

    @Query("""
        SELECT * FROM expenses WHERE userId = :userId
        AND dateAdded >= :startOfMonth AND dateAdded <= :endOfMonth
        ORDER BY dateAdded DESC
    """)
    fun getExpensesForMonth(userId: Int, startOfMonth: Long, endOfMonth: Long): Flow<List<Expense>>

    @Query("""
        SELECT SUM(amount) FROM expenses WHERE userId = :userId
        AND categoryId = :categoryId
        AND dateAdded >= :startOfMonth AND dateAdded <= :endOfMonth
    """)
    suspend fun getTotalForCategoryThisMonth(userId: Int, categoryId: Int, startOfMonth: Long, endOfMonth: Long): Double?

    @Query("""
        SELECT SUM(amount) FROM expenses WHERE userId = :userId
        AND dateAdded >= :startOfMonth AND dateAdded <= :endOfMonth
    """)
    suspend fun getTotalSpendForMonth(userId: Int, startOfMonth: Long, endOfMonth: Long): Double?

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY dateAdded DESC LIMIT :limit")
    fun getRecentExpenses(userId: Int, limit: Int = 5): Flow<List<Expense>>
}
