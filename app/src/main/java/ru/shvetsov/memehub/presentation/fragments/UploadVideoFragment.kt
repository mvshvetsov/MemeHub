package ru.shvetsov.memehub.presentation.fragments

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.shvetsov.memehub.R
import ru.shvetsov.memehub.data.network.token.TokenStorage
import ru.shvetsov.memehub.data.requests.UploadVideoRequest
import ru.shvetsov.memehub.databinding.FragmentUploadVideoBinding
import ru.shvetsov.memehub.presentation.viewmodels.VideoViewModel
import ru.shvetsov.memehub.utils.constants.Constants.VIDEO_UPLOAD_SUCCESSFULLY
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class UploadVideoFragment : Fragment() {

    private lateinit var binding: FragmentUploadVideoBinding
    private val videoViewModel: VideoViewModel by viewModels()
    private var videoFile: File? = null
    private var exoPlayer: ExoPlayer? = null
    private var currentVideoUri: Uri? = null

    @Inject
    lateinit var tokenStorage: TokenStorage

    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var videoCaptureLauncher: ActivityResultLauncher<Uri>
    private var tempVideoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { handleVideoUri(it) }
        }

        videoCaptureLauncher =
            registerForActivityResult(ActivityResultContracts.CaptureVideo()) {
                handleVideoUri(Uri.fromFile(tempVideoFile))
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addVideoButton.setOnClickListener {
            showVideoSourceDialog()
        }

        binding.doneButton.setOnClickListener {
            val tag = binding.inputTag.text.toString()
            val description = binding.inputDescription.text.toString()
            val finalTag = if (tag.isNotBlank() && !tag.startsWith("#")) {
                "#$tag"
            } else {
                tag
            }

            if (finalTag.isBlank() || description.isBlank() || videoFile == null) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.fill_all_fields_and_upload_a_video),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val uploadRequest = UploadVideoRequest(
                userId = tokenStorage.getUserId(),
                description = description,
                tag = finalTag
            )

            videoFile?.let {
                videoViewModel.uploadVideo(uploadRequest, it)
            }

            videoViewModel.uploadVideoResult.observe(viewLifecycleOwner) { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                if (message.contains(VIDEO_UPLOAD_SUCCESSFULLY)) {
                    resetUI()
                }
            }
        }

        currentVideoUri?.let { setupVideoPreviewWithExoPlayer(it) }
    }

    private fun showVideoSourceDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.select_video_source))
            setItems(arrayOf(getString(R.string.Gallery), getString(R.string.camera))) { _, which ->
                when (which) {
                    0 -> openGallery()
                    1 -> recordVideo()
                }
            }
            show()
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("video/*")
    }

    private fun recordVideo() {
        tempVideoFile = File(requireContext().externalCacheDir, "temp_video.mp4")
        val videoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            tempVideoFile!!
        )
        videoCaptureLauncher.launch(videoUri)
    }


    private fun handleVideoUri(uri: Uri) {
        currentVideoUri = uri
        videoFile = createFileFromUri(uri)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val thumbnailFile = generateThumbnailFromVideo(uri)
                videoViewModel.setThumbnailFile(thumbnailFile)
                setupVideoPreviewWithExoPlayer(uri)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Failed to generate thumbnail",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun createFileFromUri(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "temp_video.mp4")
        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        return file
    }

    private fun setupVideoPreviewWithExoPlayer(videoUri: Uri) {
        exoPlayer?.release()

        exoPlayer = ExoPlayer.Builder(requireContext()).build().apply {
            val mediaItem = MediaItem.fromUri(videoUri)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }

        binding.playerView.player = exoPlayer
        binding.playerView.visibility = View.VISIBLE
        binding.addVideoButton.visibility = View.GONE
    }

    private fun resetUI() {
        releaseExoPlayer()
        currentVideoUri = null
        videoFile = null

        binding.playerView.visibility = View.GONE
        binding.addVideoButton.visibility = View.VISIBLE
        binding.inputTag.text?.clear()
        binding.inputDescription.text?.clear()
    }

    private fun releaseExoPlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    private suspend fun generateThumbnailFromVideo(videoUri: Uri): File = withContext(Dispatchers.IO) {
        val bitmap = Glide.with(requireContext())
            .asBitmap()
            .load(videoUri)
            .submit()
            .get()

        val thumbnailFile = File(requireContext().cacheDir, "thumbnail.jpg")
        FileOutputStream(thumbnailFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        thumbnailFile
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releaseExoPlayer()
    }
}