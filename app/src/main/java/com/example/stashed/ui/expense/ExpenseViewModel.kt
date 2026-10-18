package com.example.stashed.ui.expense

import androidx.lifecycle.*
import com.example.stashed.data.entities.Category
import com.example.stashed.data.entities.Expense
import com.example.stashed.data.repository.StashedRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val repository: StashedRepository,
    private val userId: Int
) : ViewModel() {

    val allExpenses: LiveData<List<Expense>> =
        repository.getExpensesForUser(userId).asLiveData()

    val categories: LiveData<List<Category>> =
        repository.getCategoriesForUser(userId).asLiveData()

    private val _saveResult = MutableLiveData<Boolean?>()
    val saveResult: LiveData<Boolean?> = _saveResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // FIXED: Changed categoryId: Int to category: String
    fun addExpense(category: String, amount: Double, note: String) {
        if (amount <= 0) {
            _error.value = "Amount must be greater than zero"
            return
        }
        // FIXED: String blank check instead of -1 integer check
        if (category.isBlank()) {
            _error.value = "Please select a category"
            return
        }
        viewModelScope.launch {
            try {
                val expense = Expense(
                    userId = userId,
                    category = category, // FIXED: Matches the new database structure
                    amount = amount,
                    description = note.trim(),
                    date = System.currentTimeMillis()
                )
                repository.insertExpense(expense)
                _saveResult.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to save expense"
            }
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch { repository.deleteExpense(expense) }
    }

    fun clearError() { _error.value = null }
    fun clearSaveResult() { _saveResult.value = null }
}