package com.example.stashed.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stashed.data.repository.StashedRepository
import com.example.stashed.ui.auth.AuthViewModel
import com.example.stashed.ui.budget.BudgetViewModel
import com.example.stashed.ui.dashboard.DashboardViewModel
import com.example.stashed.ui.expense.ExpenseViewModel
import com.example.stashed.ui.goals.GoalsViewModel
import com.example.stashed.ui.reports.ReportsViewModel
import com.example.stashed.ui.settings.SettingsViewModel

class ViewModelFactory(
    private val repository: StashedRepository,
    private val userId: Int = -1
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(repository) as T
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(repository, userId) as T
            modelClass.isAssignableFrom(ExpenseViewModel::class.java) ->
                ExpenseViewModel(repository, userId) as T
            modelClass.isAssignableFrom(BudgetViewModel::class.java) ->
                BudgetViewModel(repository, userId) as T
            modelClass.isAssignableFrom(GoalsViewModel::class.java) ->
                GoalsViewModel(repository, userId) as T
            modelClass.isAssignableFrom(ReportsViewModel::class.java) ->
                ReportsViewModel(repository, userId) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(repository, userId) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
