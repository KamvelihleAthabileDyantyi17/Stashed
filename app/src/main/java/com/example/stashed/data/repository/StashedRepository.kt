package com.example.stashed.data.repository

import android.util.Log
import com.example.stashed.data.dao.CategoryDao
import com.example.stashed.data.dao.ExpenseDao
import com.example.stashed.data.dao.GoalDao
import com.example.stashed.data.dao.UserDao
import com.example.stashed.data.entities.Category
import com.example.stashed.data.entities.Expense
import com.example.stashed.data.entities.Goal
import com.example.stashed.data.entities.User
import com.example.stashed.utils.DateUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class StashedRepository(
    private val userDao: UserDao,
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val goalDao: GoalDao
) {
    // ── Firebase Cloud Firestore ───────────────────────────────────────────
    private val firestoreDb = FirebaseFirestore.getInstance()

    // ── Users ──────────────────────────────────────────────────────────────
    suspend fun registerUser(user: User): Long = userDao.registerUser(user)
    suspend fun getUserByUsername(username: String): User? = userDao.getUserByUsername(username)
    suspend fun loginUser(username: String, password: String): User? = userDao.loginUser(username, password)
    suspend fun getUserById(id: Int): User? = userDao.getUserById(id)

    // ── Expenses ───────────────────────────────────────────────────────────
    suspend fun insertExpense(expense: Expense): Long {
        val id = expenseDao.insertExpense(expense)
        // Optionally auto-sync when a new expense is added locally
        syncExpenseToCloud(expense.copy(id = id.toInt()))
        return id
    }

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

    suspend fun pruneOldExpenses(userId: Int) {
        val threeMonthsAgo = System.currentTimeMillis() - (90L * 24 * 60 * 60 * 1000)
        expenseDao.deleteExpensesBefore(userId, threeMonthsAgo)
    }

    // ── Categories ─────────────────────────────────────────────────────────
    suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    // Converted to String to match Firebase UID requirements
    fun getCategoriesForUser(userId: Int): Flow<List<Category>> = categoryDao.getCategoriesForUser(userId.toString())
    suspend fun getCategoriesSync(userId: Int): List<Category> = categoryDao.getCategoriesForUserSync(userId.toString())
    suspend fun getCategoryById(id: Int): Category? = categoryDao.getCategoryById(id)

    suspend fun seedDefaultCategories(userId: Int) {
        if (categoryDao.getCategoryCount(userId.toString()) == 0) {
            // Fixed variable names: iconName -> icon
            val defaults = listOf(
                Category(userId = userId.toString(), name = "Food", icon = "ic_food", isDefault = true),
                Category(userId = userId.toString(), name = "Transport", icon = "ic_transport", isDefault = true),
                Category(userId = userId.toString(), name = "Housing", icon = "ic_housing", isDefault = true),
                Category(userId = userId.toString(), name = "Health", icon = "ic_health", isDefault = true),
                Category(userId = userId.toString(), name = "Entertainment", icon = "ic_entertainment", isDefault = true),
                Category(userId = userId.toString(), name = "Education", icon = "ic_education", isDefault = true)
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

    // ── Cloud Sync Execution ───────────────────────────────────────────────

    // Pushes a single expense to the cloud (isolated inside the user's document)
    private fun syncExpenseToCloud(expense: Expense) {
        val userId = expense.userId.toString()
        val expenseId = expense.id.toString()

        firestoreDb.collection("users").document(userId)
            .collection("expenses").document(expenseId)
            .set(expense)
            .addOnSuccessListener {
                Log.d("StashedSync", "Expense $expenseId successfully synced to cloud!")
            }
            .addOnFailureListener { e ->
                Log.e("StashedSync", "Failed to sync expense $expenseId", e)
            }
    }

    // Master function for the "Sync to Cloud" button in your XML UI
    suspend fun syncAllUserDataToCloud(userId: Int) {
        val userStrId = userId.toString()

        // 1. Sync User Profile
        val user = getUserById(userId)
        if (user != null) {
            firestoreDb.collection("users").document(userStrId).set(user)
        }

        // 2. Sync Categories
        val categories = getCategoriesSync(userId)
        categories.forEach { category ->
            firestoreDb.collection("users").document(userStrId)
                // Fixed ID reference from id to categoryId
                .collection("categories").document(category.categoryId.toString())
                .set(category)
        }

        // Note: You can add lists of Expenses and Goals here later using the same pattern!
        Log.d("StashedSync", "Master cloud sync completed for user $userId")
    }
}