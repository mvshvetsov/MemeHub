package ru.shvetsov.memehub.presentation.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.shvetsov.memehub.data.network.token.TokenStorage
import ru.shvetsov.memehub.databinding.ActivityEditProfileBinding
import ru.shvetsov.memehub.presentation.viewmodels.MainViewModel
import ru.shvetsov.memehub.utils.extentions.toDp
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var tokenStorage: TokenStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = tokenStorage.getUserId()
        viewModel.getUserProfile(userId)

        viewModel.userProfile.observe(this) { user ->
            Glide.with(this)
                .load(user.profilePicture)
                .override(150.toDp(this), 150.toDp(this))
                .circleCrop()
                .into(binding.avatar)

            binding.inputUsername.hint = user.username
            binding.inputLogin.hint = user.login
            binding.inputPassword.hint = user.password
        }
    }
}