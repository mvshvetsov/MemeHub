package ru.shvetsov.memehub.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import ru.shvetsov.memehub.R
import ru.shvetsov.memehub.data.requests.RegisterRequest
import ru.shvetsov.memehub.databinding.ActivityRegistrationBinding
import ru.shvetsov.memehub.presentation.viewmodels.UserViewModel
import ru.shvetsov.memehub.utils.constants.Constants.SUCCESS
import ru.shvetsov.memehub.utils.constants.Constants.USERNAME_IS_ALREADY_TAKEN
import ru.shvetsov.memehub.utils.constants.Constants.USER_WITH_LOGIN_ALREADY_EXIST

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private val userViewModel: UserViewModel by viewModels()
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
                binding.loginInputLayout.error = getString(R.string.empty_field)
                hasError = true
            }

            if (username.isBlank()) {
                binding.usernameInputLayout.error = getString(R.string.empty_field)
                hasError = true
            }

            if (password.isBlank()) {
                binding.passwordInputLayout.error = getString(R.string.empty_field)
                hasError = true
            } else if (password.length < 8) {
                binding.passwordInputLayout.error = getString(R.string.password_is_too_small)
                hasError = true
            }

            if (!hasError) {
                userViewModel.register(registerRequest)
            }
        }

        userViewModel.registrationResult.observe(this) { message ->
            when {
                message.contains(USER_WITH_LOGIN_ALREADY_EXIST) -> {
                    binding.loginInputLayout.error = message
                }
                message.contains(USERNAME_IS_ALREADY_TAKEN) -> {
                    binding.usernameInputLayout.error = message
                }
                message.contains(SUCCESS) -> {
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