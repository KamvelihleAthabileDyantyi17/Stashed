package com.example.stashed.ui.settings

import androidx.lifecycle.*
import com.example.stashed.data.entities.User
import com.example.stashed.data.repository.StashedRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: StashedRepository,
    private val userId: Int
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    init { loadUser() }

    private fun loadUser() {
        viewModelScope.launch {
            _user.value = repository.getUserById(userId)
        }
    }
}