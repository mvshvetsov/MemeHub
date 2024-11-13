package ru.shvetsov.memehub.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import ru.shvetsov.memehub.data.requests.RegisterRequest
import ru.shvetsov.memehub.databinding.ActivityRegistrationBinding
import ru.shvetsov.memehub.presentation.viewmodels.MainViewModel

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private val mainViewModel: MainViewModel by viewModels()
    private val loginIntent by lazy { Intent(this@RegistrationActivity, LoginActivity::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toLoginButton.setOnClickListener {
            loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(loginIntent)
        }

        binding.inputLogin.addTextChangedListener {
            binding.loginInputLayout.error = null
        }

        binding.inputUsername.addTextChangedListener {
            binding.usernameInputLayout.error = null
        }

        binding.inputPassword.addTextChangedListener {
            binding.passwordInputLayout.error = null
        }

        binding.signUpButton.setOnClickListener {
            val login = binding.inputLogin.text.toString()
            val username = binding.inputUsername.text.toString()
            val password = binding.inputPassword.text.toString()
            val registerRequest = RegisterRequest(login, username, password)
            var hasError = false

            if (login.isBlank()) {
                binding.loginInputLayout.error = "Empty field"
                hasError = true
            }

            if (username.isBlank()) {
                binding.usernameInputLayout.error = "Empty field"
                hasError = true
            }

            if (password.isBlank()) {
                binding.passwordInputLayout.error = "Empty field"
                hasError = true
            } else if (password.length < 8) {
                binding.passwordInputLayout.error = "Password is too small"
                hasError = true
            }

            if (!hasError) {
                mainViewModel.register(registerRequest)
            }
        }

        mainViewModel.registrationResult.observe(this) { message ->
            when {
                message.contains("login") -> {
                    binding.loginInputLayout.error = message
                }
                message.contains("Username") -> {
                    binding.usernameInputLayout.error = message
                }
                message.contains("Success") -> {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    loginIntent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(loginIntent)
                    finish()
                }
                else -> {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}