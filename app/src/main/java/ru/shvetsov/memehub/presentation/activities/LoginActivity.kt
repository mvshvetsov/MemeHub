package ru.shvetsov.memehub.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import ru.shvetsov.memehub.R
import ru.shvetsov.memehub.data.network.token.TokenStorage
import ru.shvetsov.memehub.data.requests.LoginRequest
import ru.shvetsov.memehub.databinding.LoginActivityBinding
import ru.shvetsov.memehub.presentation.viewmodels.UserViewModel
import ru.shvetsov.memehub.utils.constants.Constants.INCORRECT_PASSWORD
import ru.shvetsov.memehub.utils.constants.Constants.SUCCESS
import ru.shvetsov.memehub.utils.constants.Constants.TOKEN
import ru.shvetsov.memehub.utils.constants.Constants.USER_NOT_FOUND
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding
    private val userViewModel: UserViewModel by viewModels()
    private var hasError = false
    private val mainActivityIntent by lazy { Intent(this@LoginActivity, MainActivity::class.java) }

    @Inject
    lateinit var tokenStorage: TokenStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = tokenStorage.sharedPreferences.getString(TOKEN, null)
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
                binding.loginInputLayout.error = getString(R.string.empty_field)
                hasError = true
            }

            if (password.isBlank()) {
                binding.passwordInputLayout.error = getString(R.string.empty_field)
                hasError = true
            }

            if (!hasError) {
                userViewModel.login(loginRequest)
            }
        }

        userViewModel.loginResult.observe(this) { message ->
            when {
                message.contains(USER_NOT_FOUND) -> {
                    binding.loginInputLayout.error = message
                    hasError = true
                }

                message.contains(INCORRECT_PASSWORD) -> {
                    binding.passwordInputLayout.error = message
                    hasError = true
                }

                message.contains(SUCCESS) -> {
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