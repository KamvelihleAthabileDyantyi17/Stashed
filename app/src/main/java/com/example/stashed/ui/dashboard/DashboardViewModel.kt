package com.example.stashed.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.stashed.data.entities.Expense
import com.example.stashed.data.repository.StashedRepository
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: StashedRepository,
    private val userId: Int
) : ViewModel() {

    // This specifically calls getExpensesForCurrentMonth to match your repository
    val currentMonthExpenses: LiveData<List<Expense>> = repository.getExpensesForCurrentMonth(userId).asLiveData()

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
}