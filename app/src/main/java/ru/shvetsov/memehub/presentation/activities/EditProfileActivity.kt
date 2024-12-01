package ru.shvetsov.memehub.presentation.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.shvetsov.memehub.R
import ru.shvetsov.memehub.data.network.token.TokenStorage
import ru.shvetsov.memehub.data.requests.UpdateRequest
import ru.shvetsov.memehub.databinding.ActivityEditProfileBinding
import ru.shvetsov.memehub.presentation.viewmodels.UserViewModel
import ru.shvetsov.memehub.utils.constants.Constants.FAILED_TO_UPDATE_USER_INFORMATION
import ru.shvetsov.memehub.utils.constants.Constants.INFORMATION_UPDATED_SUCCESSFULLY
import ru.shvetsov.memehub.utils.constants.Constants.USERNAME_IS_ALREADY_TAKEN
import ru.shvetsov.memehub.utils.constants.Constants.USER_WITH_LOGIN_ALREADY_EXIST
import ru.shvetsov.memehub.utils.extentions.toDp
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: UserViewModel by viewModels()
    private var hasError = false
    private lateinit var imageUri: Uri

    @Inject
    lateinit var tokenStorage: TokenStorage

    private val pickImageFromGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val uri = result.data?.data
            uri?.let {
                handleImageUri(it)
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                handleImageUri(imageUri)
            }
        }

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
            binding.inputPassword.setText(user.password)
        }

        binding.inputUsername.addTextChangedListener {
            binding.usernameInputLayout.error = null
        }

        binding.inputLogin.addTextChangedListener {
            binding.loginInputLayout.error = null
        }

        binding.inputPassword.addTextChangedListener {
            binding.passwordInputLayout.error = null
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.avatar.setOnClickListener {
            showImageSourceDialog()
        }

        binding.doneButton.setOnClickListener {
            val username = binding.inputUsername.text.toString().takeIf { it.isNotBlank() }
            val login = binding.inputLogin.text.toString().takeIf { it.isNotBlank() }
            val password = binding.inputPassword.text.toString().takeIf { it.isNotBlank() }
            val updateRequest = UpdateRequest(username, login, password)

            if (password?.length!! < 8) {
                binding.passwordInputLayout.error = getString(R.string.password_is_too_small)
                hasError = true
            }

            if (!hasError) viewModel.updateUserProfile(tokenStorage.getUserId(), updateRequest)
        }

        viewModel.updateProfileResult.observe(this) { message ->
            when {
                message.contains(FAILED_TO_UPDATE_USER_INFORMATION) -> {
                    Toast.makeText(this, FAILED_TO_UPDATE_USER_INFORMATION, Toast.LENGTH_SHORT).show()
                }

                message.contains(USER_WITH_LOGIN_ALREADY_EXIST) -> {
                    binding.loginInputLayout.error = message
                }

                message.contains(USERNAME_IS_ALREADY_TAKEN) -> {
                    binding.usernameInputLayout.error = message
                }

                message.contains(INFORMATION_UPDATED_SUCCESSFULLY) -> {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        pickImageFromGalleryLauncher.launch(intent)
    }

    private fun takePicture() {
        imageUri = FileProvider.getUriForFile(
            this,
            "${this.packageName}.fileprovider",
            File(this.cacheDir, "temp_profile_picture.jpg")
        )
        takePictureLauncher.launch(imageUri)
    }

    private fun handleImageUri(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            val imageFile = createFileFromUri(uri)
            val compressedFile = compressImage(imageFile)
            withContext(Dispatchers.Main) {
                viewModel.uploadProfileImage(compressedFile)
            }
        }
    }

    private fun createFileFromUri(uri: Uri): File {
        val inputStream = this.contentResolver.openInputStream(uri)
        val file = File(this.cacheDir, "profile_picture.jpg")
        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        return file
    }

    private fun showImageSourceDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.choose_profile_photo))
            setItems(arrayOf(getString(R.string.Gallery), getString(R.string.camera))) { _, which ->
                when (which) {
                    0 -> openGallery()
                    1 -> takePicture()
                }
            }
            show()
        }
    }

    private suspend fun compressImage(file: File): File {
        return Compressor.compress(this, file) {
            resolution(640, 640)
            quality(80)
            format(Bitmap.CompressFormat.JPEG)
        }
    }
}