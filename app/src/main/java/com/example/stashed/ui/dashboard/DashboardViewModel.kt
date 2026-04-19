package com.example.stashed.ui.dashboard

import androidx.lifecycle.*
import com.example.stashed.data.entities.Category
import com.example.stashed.data.entities.Expense
import com.example.stashed.data.repository.StashedRepository
import com.example.stashed.utils.DateUtils
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class CategoryBudgetItem(
    val category: Category,
    val spent: Double,
    val percentage: Double,
    val status: BudgetStatus
)

enum class BudgetStatus { GOOD, WARNING, DANGER }

class DashboardViewModel(
    private val repository: StashedRepository,
    private val userId: Int
) : ViewModel() {

    val recentExpenses: LiveData<List<Expense>> =
        repository.getRecentExpenses(userId, 5).asLiveData()

    val currentMonthExpenses: LiveData<List<Expense>> =
        repository.getExpensesForCurrentMonth(userId).asLiveData()

    private val _totalSpend = MutableLiveData<Double>(0.0)
    val totalSpend: LiveData<Double> = _totalSpend

    private val _categoryItems = MutableLiveData<List<CategoryBudgetItem>>()
    val categoryItems: LiveData<List<CategoryBudgetItem>> = _categoryItems

    val currentMonthLabel: String = DateUtils.formatMonthYear(System.currentTimeMillis())

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            val total = repository.getTotalSpendForMonth(userId)
            _totalSpend.value = total

            val categories = repository.getCategoriesSync(userId)
            val items = categories.map { cat ->
                val spent = repository.getTotalForCategoryThisMonth(userId, cat.categoryId)
                val pct = if (cat.budgetLimit > 0) (spent / cat.budgetLimit) * 100.0 else 0.0
                val status = when {
                    pct >= 100.0 -> BudgetStatus.DANGER
                    pct >= 70.0  -> BudgetStatus.WARNING
                    else         -> BudgetStatus.GOOD
                }
                CategoryBudgetItem(cat, spent, pct, status)
            }
            _categoryItems.value = items
        }
    }
}
