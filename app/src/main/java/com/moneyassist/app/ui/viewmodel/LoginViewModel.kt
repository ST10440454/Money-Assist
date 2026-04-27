package com.moneyassist.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moneyassist.app.data.entity.User
import com.moneyassist.app.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppRepository(app)

    sealed class AuthResult {
        data class Success(val user: User) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }

    private val _authResult = MutableSharedFlow<AuthResult>()
    val authResult = _authResult.asSharedFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            viewModelScope.launch { _authResult.emit(AuthResult.Error("Username and password required.")) }
            return
        }
        viewModelScope.launch {
            val user = repo.login(username.trim(), password)
            if (user != null) _authResult.emit(AuthResult.Success(user))
            else _authResult.emit(AuthResult.Error("Invalid username or password."))
        }
    }

    fun register(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            viewModelScope.launch { _authResult.emit(AuthResult.Error("Username and password required.")) }
            return
        }
        viewModelScope.launch {
            val existing = repo.getUserByUsername(username.trim())
            if (existing != null) {
                _authResult.emit(AuthResult.Error("Username already exists."))
                return@launch
            }
            val id = repo.registerUser(User(username = username.trim(), password = password))
            if (id > 0) {
                val user = User(id.toInt(), username.trim(), password)
                _authResult.emit(AuthResult.Success(user))
            } else {
                _authResult.emit(AuthResult.Error("Registration failed. Try again."))
            }
        }
    }
}
