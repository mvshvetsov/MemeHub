package ru.shvetsov.memehub.presentation.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import ru.shvetsov.memehub.data.models.VideoWithUserInfoModel
import ru.shvetsov.memehub.databinding.FragmentVideoBinding
import ru.shvetsov.memehub.presentation.adapters.VideoPagerAdapter
import ru.shvetsov.memehub.presentation.viewmodels.UserViewModel

@AndroidEntryPoint
class VideoFragment : Fragment() {

    private lateinit var binding: FragmentVideoBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var videoAdapter: VideoPagerAdapter
    private val videos = mutableListOf<VideoWithUserInfoModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.getVideos()
        binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupObserves()
        viewModel.videos.observe(viewLifecycleOwner) { newVideos ->
            if (newVideos.isNullOrEmpty()) {
                videos.addAll(newVideos)
                videoAdapter.addVideos(newVideos)
            } else {
                Log.d("VideoFragment", "No videos loaded from server")
            }
        }

        if (videos.isEmpty()) {
            viewModel.getVideos()
        }
    }

    private fun setupViewPager() {
        videoAdapter = VideoPagerAdapter(videos)

        binding.viewPager.apply {
            adapter = videoAdapter
            orientation = ViewPager2.ORIENTATION_VERTICAL
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == videos.size - 2) {
                    viewModel.getVideos()
                }
            }
        })
    }

    private fun setupObserves() {
        viewModel.videos.observe(viewLifecycleOwner) { newVideos ->
            if (newVideos.isNotEmpty()) {
                videos.addAll(newVideos)
                videoAdapter.addVideos(newVideos)
            }
        }
    }
}