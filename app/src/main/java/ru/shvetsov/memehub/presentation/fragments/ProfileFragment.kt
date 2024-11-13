package ru.shvetsov.memehub.presentation.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
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
import ru.shvetsov.memehub.data.network.token.TokenStorage
import ru.shvetsov.memehub.databinding.FragmentProfileBinding
import ru.shvetsov.memehub.presentation.activities.EditProfileActivity
import ru.shvetsov.memehub.presentation.activities.LoginActivity
import ru.shvetsov.memehub.presentation.viewmodels.MainViewModel
import ru.shvetsov.memehub.utils.extentions.toDp
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: MainViewModel by activityViewModels()
    private var imageUri: Uri? = null
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
                imageUri?.let {
                    handleImageUri(it)
                }
            }
        }

    @Inject
    lateinit var tokenStorage: TokenStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userId = tokenStorage.getUserId()
        viewModel.getUserProfile(userId)
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loading.visibility = View.VISIBLE

        viewModel.userProfile.observe(viewLifecycleOwner) { user ->
            binding.loading.visibility = View.GONE

            binding.username.text = user.username
            Glide.with(this)
                .load(user.profilePicture)
                .override(100.toDp(requireContext()), 100.toDp(requireContext()))
                .circleCrop()
                .into(binding.avatar)
        }

        viewModel.logoutEvent.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                requireActivity().finish()
            }
        }

        binding.avatar.setOnClickListener {
            showImageSourceDialog()
        }

        binding.logout.setOnClickListener {
            viewModel.logout()
        }

        binding.editProfileButton.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageFromGalleryLauncher.launch(intent)
    }

    private fun takePicture() {
        imageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            File(requireContext().cacheDir, "temp_profile_picture.jpg")
        )
        takePictureLauncher.launch(imageUri)
    }

    private fun handleImageUri(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            val imageFile = createFileFromUri(uri)
            val compressedFile = compressImage(imageFile)
            withContext(Dispatchers.Main) {
                binding.loading.visibility = View.GONE
                viewModel.uploadProfileImage(compressedFile)
            }
        }
    }

    private fun createFileFromUri(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "profile_picture.jpg")
        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        return file
    }

    private fun showImageSourceDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Выбрать фото профиля")
            setItems(arrayOf("Галерея", "Камера")) { _, which ->
                when (which) {
                    0 -> openGallery()
                    1 -> takePicture()
                }
            }
            show()
        }
    }

    private suspend fun compressImage(file: File): File {
        return Compressor.compress(requireContext(), file) {
            resolution(640, 640)
            quality(80)
            format(Bitmap.CompressFormat.JPEG)
        }
    }
}

