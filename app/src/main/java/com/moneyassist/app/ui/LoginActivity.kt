package com.moneyassist.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.moneyassist.app.R
import com.moneyassist.app.ui.viewmodel.LoginViewModel
import com.moneyassist.app.util.PrefsManager

class LoginActivity : AppCompatActivity() {

    private val vm: LoginViewModel by viewModels()
    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Auto-skip to correct destination if already logged in
        val userId = PrefsManager.getLoggedInUserId(this)
        if (userId != -1) {
            navigateAfterAuth(needsOnboarding = !PrefsManager.isOnboardingDone(this))
            return
        }

        setContentView(R.layout.activity_login)

        val logo         = findViewById<ImageView>(R.id.iv_logo)
        val tvTitle      = findViewById<TextView>(R.id.tv_app_title)
        val tilUsername  = findViewById<View>(R.id.til_username)
        val etUsername   = findViewById<EditText>(R.id.et_username)
        val etEmail      = findViewById<EditText>(R.id.et_email)
        val etPassword   = findViewById<EditText>(R.id.et_password)
        val tilConfirm   = findViewById<View>(R.id.til_confirm_password)
        val etConfirm    = findViewById<EditText>(R.id.et_confirm_password)
        val btnAction    = findViewById<Button>(R.id.btn_login)
        val tvToggle     = findViewById<TextView>(R.id.tv_toggle_mode)
        val tvError      = findViewById<TextView>(R.id.tv_error)
        val progressBar  = findViewById<ProgressBar>(R.id.progress_bar)

        logo.setImageResource(R.drawable.logo_money_assist)

        tvToggle.setOnClickListener {
            isLoginMode = !isLoginMode
            tilUsername.visibility = if (isLoginMode) View.GONE else View.VISIBLE
            tilConfirm.visibility  = if (isLoginMode) View.GONE else View.VISIBLE
            btnAction.text        = if (isLoginMode) "Log In" else "Create Account"
            tvToggle.text         = if (isLoginMode) "Don't have an account? Sign up" else "Already have an account? Log in"
            tvError.visibility    = View.GONE
            vm.resetState()
        }

        btnAction.setOnClickListener {
            tvError.visibility = View.GONE
            val email    = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (isLoginMode) {
                vm.login(email, password)
            } else {
                vm.register(
                    username    = etUsername.text.toString(),
                    email       = email,
                    password    = password,
                    confirmPassword = etConfirm.text.toString()
                )
            }
        }

        vm.loginState.observe(this) { state ->
            progressBar.visibility = View.GONE
            when (state) {
                is LoginViewModel.LoginState.Loading -> progressBar.visibility = View.VISIBLE
                is LoginViewModel.LoginState.Success -> navigateAfterAuth(state.needsOnboarding)
                is LoginViewModel.LoginState.Error -> {
                    tvError.text = state.message
                    tvError.visibility = View.VISIBLE
                }
                else -> Unit
            }
        }
    }

    private fun navigateAfterAuth(needsOnboarding: Boolean) {
        val target = if (needsOnboarding) OnboardingActivity::class.java else MainActivity::class.java
        startActivity(Intent(this, target).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
