package com.example.stashed.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stashed.data.repository.StashedRepository
import com.example.stashed.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: StashedRepository,
    private val currentUserId: Int // Pass the logged-in user's ID here
) : ViewModel() {

    // UI State for Total Spend
    private val _totalSpend = MutableStateFlow(0.0)
    val totalSpend: StateFlow<Double> = _totalSpend.asStateFlow()

    // UI State for the Month Label
    private val _currentMonthLabel = MutableStateFlow(
        DateUtils.formatMonthYear(System.currentTimeMillis())
    )
    val currentMonthLabel: StateFlow<String> = _currentMonthLabel.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            // Fetch total spend for the current month from Room
            val spend = repository.getTotalSpendForMonth(currentUserId)
            _totalSpend.value = spend
        }
    }

    // Triggered by your gold "Sync to Cloud" button
    fun triggerCloudSync() {
        viewModelScope.launch {
            repository.syncAllUserDataToCloud(currentUserId)
        }
    }
}