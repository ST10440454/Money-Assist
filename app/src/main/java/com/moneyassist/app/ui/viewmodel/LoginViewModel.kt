package com.moneyassist.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.moneyassist.app.data.db.AppDatabase
import com.moneyassist.app.data.entity.User
import com.moneyassist.app.util.PasswordUtils
import com.moneyassist.app.util.PrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val userId: Int, val needsOnboarding: Boolean) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Please fill in all fields.")
            return
        }
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            val user = withContext(Dispatchers.IO) { db.userDao().getUserByEmail(email.trim()) }
            when {
                user == null -> _loginState.value = LoginState.Error("No account found with that email.")
                !PasswordUtils.verify(password, user.passwordHash) ->
                    _loginState.value = LoginState.Error("Incorrect password.")
                else -> {
                    PrefsManager.saveLoggedInUserId(getApplication(), user.id)
                    val needsOnboarding = !PrefsManager.isOnboardingDone(getApplication())
                    _loginState.value = LoginState.Success(user.id, needsOnboarding)
                }
            }
        }
    }

    fun register(username: String, email: String, password: String, confirmPassword: String) {
        // ── Validate inputs ──────────────────────────────────────────
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("All fields are required."); return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginState.value = LoginState.Error("Please enter a valid email address."); return
        }
        if (password != confirmPassword) {
            _loginState.value = LoginState.Error("Passwords do not match."); return
        }
        val pwCheck = PasswordUtils.validate(password)
        if (pwCheck is PasswordUtils.ValidationResult.Fail) {
            _loginState.value = LoginState.Error(pwCheck.reason); return
        }

        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            val existing = withContext(Dispatchers.IO) { db.userDao().getUserByEmail(email.trim()) }
            if (existing != null) {
                _loginState.value = LoginState.Error("An account with this email already exists."); return@launch
            }
            // Hash password before storing — never plain text
            val hashed = PasswordUtils.hash(password)
            val user = User(username = username.trim(), email = email.trim(), passwordHash = hashed)
            val newId = withContext(Dispatchers.IO) { db.userDao().insertUser(user) }
            PrefsManager.saveLoggedInUserId(getApplication(), newId.toInt())
            _loginState.value = LoginState.Success(newId.toInt(), needsOnboarding = true)
        }
    }

    fun resetState() { _loginState.value = LoginState.Idle }
}
