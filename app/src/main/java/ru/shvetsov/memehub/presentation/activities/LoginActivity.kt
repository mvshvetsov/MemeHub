package ru.shvetsov.memehub.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import ru.shvetsov.memehub.data.network.token.TokenStorage
import ru.shvetsov.memehub.data.requests.LoginRequest
import ru.shvetsov.memehub.databinding.LoginActivityBinding
import ru.shvetsov.memehub.presentation.viewmodels.MainViewModel
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding
    private val mainViewModel: MainViewModel by viewModels()
    private var hasError = false
    private val mainActivityIntent by lazy { Intent(this@LoginActivity, MainActivity::class.java) }

    @Inject
    lateinit var tokenStorage: TokenStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = tokenStorage.sharedPreferences.getString("token", null)
        if (token != null && !tokenStorage.isTokenExpired(token)) {
            mainActivityIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(mainActivityIntent)
            finish()
            return
        }

        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toSignUpButton.setOnClickListener {
            val registrationIntent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            registrationIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(registrationIntent)
        }

        binding.inputLogin.addTextChangedListener {
            binding.loginInputLayout.error = null
        }

        binding.inputPassword.addTextChangedListener {
            binding.passwordInputLayout.error = null
        }

        binding.loginButton.setOnClickListener {
            val login = binding.inputLogin.text.toString()
            val password = binding.inputPassword.text.toString()
            val loginRequest = LoginRequest(login, password)

            hasError = false

            if (login.isBlank()) {
                binding.loginInputLayout.error = "Empty field"
                hasError = true
            }

            if (password.isBlank()) {
                binding.passwordInputLayout.error = "Empty field"
                hasError = true
            }

            if (!hasError) {
                mainViewModel.login(loginRequest)
            }
        }

        mainViewModel.loginResult.observe(this) { message ->
            when {
                message.contains("User") -> {
                    binding.loginInputLayout.error = message
                    hasError = true
                }

                message.contains("password") -> {
                    binding.passwordInputLayout.error = message
                    hasError = true
                }

                message.contains("Success") -> {
                    mainActivityIntent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(mainActivityIntent)
                    finish()
                }

                else -> {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    hasError = true
                }
            }
        }
    }
}