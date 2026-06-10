package com.example.stashed.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stashed.data.entities.User
import com.example.stashed.data.repository.StashedRepository
import com.example.stashed.utils.PasswordUtils
import kotlinx.coroutines.launch

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}

class AuthViewModel(private val repository: StashedRepository) : ViewModel() {

    private val _authResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> = _authResult

    fun register(fullName: String, email: String, password: String) {
        _authResult.value = AuthResult.Loading
        viewModelScope.launch {
            try {
                // Validation
                if (fullName.isBlank()) { _authResult.value = AuthResult.Error("Name is required"); return@launch }
                if (!email.contains("@")) { _authResult.value = AuthResult.Error("Enter a valid email address"); return@launch }
                if (password.length < 8) { _authResult.value = AuthResult.Error("Password must be at least 8 characters"); return@launch }

                val existing = repository.getUserByEmail(email)
                if (existing != null) { _authResult.value = AuthResult.Error("An account with this email already exists"); return@launch }

                val hash = PasswordUtils.hashPassword(password)
                val user = User(fullName = fullName, email = email, passwordHash = hash)
                val id = repository.insertUser(user)
                val created = repository.getUserById(id.toInt())
                if (created != null) {
                    repository.seedDefaultCategories(created.userId)
                    _authResult.value = AuthResult.Success(created)
                } else {
                    _authResult.value = AuthResult.Error("Registration failed. Please try again.")
                }
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    fun login(email: String, password: String) {
        _authResult.value = AuthResult.Loading
        viewModelScope.launch {
            try {
                if (!email.contains("@")) { _authResult.value = AuthResult.Error("Enter a valid email address"); return@launch }
                if (password.isBlank()) { _authResult.value = AuthResult.Error("Password is required"); return@launch }

                val user = repository.getUserByEmail(email)
                if (user == null || !PasswordUtils.verifyPassword(password, user.passwordHash)) {
                    _authResult.value = AuthResult.Error("Incorrect email or password")
                    return@launch
                }
                _authResult.value = AuthResult.Success(user)
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error(e.message ?: "Login failed")
            }
        }
    }

    fun resetResult() { _authResult.value = null }
}
