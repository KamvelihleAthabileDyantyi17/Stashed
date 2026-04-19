package com.example.stashed.ui.goals

import androidx.lifecycle.*
import com.example.stashed.data.entities.Goal
import com.example.stashed.data.repository.StashedRepository
import kotlinx.coroutines.launch

class GoalsViewModel(
    private val repository: StashedRepository,
    private val userId: Int
) : ViewModel() {

    val goals: LiveData<List<Goal>> =
        repository.getGoalsForUser(userId).asLiveData()

    private val _saveResult = MutableLiveData<Boolean?>()
    val saveResult: LiveData<Boolean?> = _saveResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun addGoal(name: String, targetAmount: Double, deadline: Long) {
        if (name.isBlank()) { _error.value = "Goal name is required"; return }
        if (targetAmount <= 0) { _error.value = "Target amount must be greater than zero"; return }
        if (deadline <= System.currentTimeMillis()) { _error.value = "Deadline must be in the future"; return }
        viewModelScope.launch {
            try {
                val goal = Goal(
                    userId = userId,
                    goalName = name.trim(),
                    targetAmount = targetAmount,
                    deadline = deadline
                )
                repository.insertGoal(goal)
                _saveResult.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add goal"
            }
        }
    }

    fun contributeToGoal(goal: Goal, amount: Double) {
        if (amount <= 0) { _error.value = "Contribution must be greater than zero"; return }
        viewModelScope.launch {
            repository.addToGoal(goal.goalId, amount)
            val updated = repository.getCategoriesSync(userId) // trigger refresh
            val newSaved = goal.savedAmount + amount
            if (newSaved >= goal.targetAmount) {
                repository.markGoalComplete(goal.goalId)
            }
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch { repository.deleteGoal(goal) }
    }

    fun clearError() { _error.value = null }
    fun clearSaveResult() { _saveResult.value = null }
}
