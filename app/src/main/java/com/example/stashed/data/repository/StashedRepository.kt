package com.example.stashed.data.repository

import com.example.stashed.data.dao.CategoryDao
import com.example.stashed.data.dao.ExpenseDao
import com.example.stashed.data.dao.GoalDao
import com.example.stashed.data.dao.UserDao
import com.example.stashed.data.entities.Category
import com.example.stashed.data.entities.Expense
import com.example.stashed.data.entities.Goal
import com.example.stashed.data.entities.User
import com.example.stashed.utils.DateUtils
import kotlinx.coroutines.flow.Flow

class StashedRepository(
    private val userDao: UserDao,
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val goalDao: GoalDao
) {
    // ── Users ──────────────────────────────────────────────────────────────
    suspend fun insertUser(user: User): Long = userDao.insertUser(user)
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
    suspend fun getUserById(id: Int): User? = userDao.getUserById(id)
    suspend fun updateUser(user: User) = userDao.updateUser(user)

    // ── Expenses ───────────────────────────────────────────────────────────
    suspend fun insertExpense(expense: Expense): Long = expenseDao.insertExpense(expense)
    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)
    fun getExpensesForUser(userId: Int): Flow<List<Expense>> = expenseDao.getExpensesForUser(userId)
    fun getRecentExpenses(userId: Int, limit: Int = 5) = expenseDao.getRecentExpenses(userId, limit)

    fun getExpensesForCurrentMonth(userId: Int): Flow<List<Expense>> {
        val (start, end) = DateUtils.currentMonthRange()
        return expenseDao.getExpensesForMonth(userId, start, end)
    }

    suspend fun getTotalForCategoryThisMonth(userId: Int, categoryId: Int): Double {
        val (start, end) = DateUtils.currentMonthRange()
        return expenseDao.getTotalForCategoryThisMonth(userId, categoryId, start, end) ?: 0.0
    }

    suspend fun getTotalSpendForMonth(userId: Int): Double {
        val (start, end) = DateUtils.currentMonthRange()
        return expenseDao.getTotalSpendForMonth(userId, start, end) ?: 0.0
    }

    // ── Categories ─────────────────────────────────────────────────────────
    suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)
    fun getCategoriesForUser(userId: Int): Flow<List<Category>> = categoryDao.getCategoriesForUser(userId)
    suspend fun getCategoriesSync(userId: Int): List<Category> = categoryDao.getCategoriesForUserSync(userId)
    suspend fun getCategoryById(id: Int): Category? = categoryDao.getCategoryById(id)

    suspend fun seedDefaultCategories(userId: Int) {
        if (categoryDao.getCategoryCount(userId) == 0) {
            val defaults = listOf(
                Category(userId = userId, name = "Food", iconName = "ic_food", isDefault = true),
                Category(userId = userId, name = "Transport", iconName = "ic_transport", isDefault = true),
                Category(userId = userId, name = "Housing", iconName = "ic_housing", isDefault = true),
                Category(userId = userId, name = "Health", iconName = "ic_health", isDefault = true),
                Category(userId = userId, name = "Entertainment", iconName = "ic_entertainment", isDefault = true),
                Category(userId = userId, name = "Education", iconName = "ic_education", isDefault = true)
            )
            categoryDao.insertCategories(defaults)
        }
    }

    // ── Goals ──────────────────────────────────────────────────────────────
    suspend fun insertGoal(goal: Goal): Long = goalDao.insertGoal(goal)
    suspend fun updateGoal(goal: Goal) = goalDao.updateGoal(goal)
    suspend fun deleteGoal(goal: Goal) = goalDao.deleteGoal(goal)
    fun getGoalsForUser(userId: Int): Flow<List<Goal>> = goalDao.getGoalsForUser(userId)
    suspend fun addToGoal(goalId: Int, amount: Double) = goalDao.addToGoal(goalId, amount)
    suspend fun markGoalComplete(goalId: Int) = goalDao.markGoalComplete(goalId)
}
