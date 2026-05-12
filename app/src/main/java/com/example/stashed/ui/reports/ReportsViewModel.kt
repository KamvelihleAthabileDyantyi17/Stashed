package com.example.stashed.ui.reports

import androidx.lifecycle.*
import com.example.stashed.data.entities.Category
import com.example.stashed.data.entities.Expense
import com.example.stashed.data.repository.StashedRepository
import kotlinx.coroutines.launch

data class CategorySpend(val category: Category, val totalSpent: Double)

class ReportsViewModel(
    private val repository: StashedRepository,
    private val userId: Int
) : ViewModel() {

    val currentMonthExpenses: LiveData<List<Expense>> =
        repository.getExpensesForCurrentMonth(userId).asLiveData()

    private val _categorySpends = MutableLiveData<List<CategorySpend>>()
    val categorySpends: LiveData<List<CategorySpend>> = _categorySpends

    init { loadCategorySpends() }

    fun loadCategorySpends() {
        viewModelScope.launch {
            val categories = repository.getCategoriesSync(userId)
            val spends = categories.mapNotNull { cat ->
                val spent = repository.getTotalForCategoryThisMonth(userId, cat.categoryId)
                if (spent > 0) CategorySpend(cat, spent) else null
            }.sortedByDescending { it.totalSpent }
            _categorySpends.value = spends
        }
    }
}
