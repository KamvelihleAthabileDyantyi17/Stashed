package data.dao

import androidx.room.*
import data.entities.Expense

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE userId = :userId AND date BETWEEN :startMs AND :endMs ORDER BY date DESC")
    suspend fun getExpensesForMonth(userId: Int, startMs: Long, endMs: Long): List<Expense>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE userId = :userId AND date BETWEEN :startMs AND :endMs")
    suspend fun getTotalSpentForMonth(userId: Int, startMs: Long, endMs: Long): Double

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentExpenses(userId: Int, limit: Int = 5): List<Expense>
}