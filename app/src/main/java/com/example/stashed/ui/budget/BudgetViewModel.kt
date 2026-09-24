package com.example.stashed.ui.budget

import androidx.lifecycle.*
import com.example.stashed.data.entities.Category
import com.example.stashed.data.entities.Expense
import com.example.stashed.data.repository.StashedRepository
import kotlinx.coroutines.launch

class BudgetViewModel(
    private val repository: StashedRepository,
    private val userId: Int
) : ViewModel() {

    val categories: LiveData<List<Category>> =
        repository.getCategoriesForUser(userId).asLiveData()

    // --- ADDED THIS LINE FOR THE BAR CHART ---
    val currentMonthExpenses: LiveData<List<Expense>> =
        repository.getExpensesForCurrentMonth(userId).asLiveData()

    private val _saveResult = MutableLiveData<Boolean?>()
    val saveResult: LiveData<Boolean?> = _saveResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun addCategory(name: String, budgetLimit: Double) {
        if (name.isBlank()) { _error.value = "Category name is required"; return }
        if (budgetLimit < 0) { _error.value = "Budget limit cannot be negative"; return }

        viewModelScope.launch {
            try {
                // FIXED: Converted userId to String for Firebase compatibility
                // FIXED: Removed budgetLimit (handled in a separate table now)
                val cat = Category(userId = userId.toString(), name = name.trim())
                repository.insertCategory(cat)
                _saveResult.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add category"
            }
        }
    }

    fun updateBudgetLimit(category: Category, newLimit: Double) {
        if (newLimit < 0) { _error.value = "Budget limit cannot be negative"; return }
        viewModelScope.launch {
            // FIXED: Removed the .copy(budgetLimit = newLimit) since the column is gone.
            // We will wire this up to your new Budget table later, but this keeps it compiling for now!
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch { repository.deleteCategory(category) }
    }

    fun clearError() { _error.value = null }
    fun clearSaveResult() { _saveResult.value = null }
}