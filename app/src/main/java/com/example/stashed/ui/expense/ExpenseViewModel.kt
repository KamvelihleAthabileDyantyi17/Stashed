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

    fun addExpense(categoryId: Int, amount: Double, note: String) {
        if (amount <= 0) {
            _error.value = "Amount must be greater than zero"
            return
        }
        if (categoryId == -1) {
            _error.value = "Please select a category"
            return
        }
        viewModelScope.launch {
            try {
                val expense = Expense(
                    userId = userId,
                    categoryId = categoryId,
                    amount = amount,
                    note = note.trim()
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
