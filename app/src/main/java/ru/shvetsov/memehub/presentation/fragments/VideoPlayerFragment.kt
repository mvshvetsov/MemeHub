package ru.shvetsov.memehub.presentation.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.shvetsov.memehub.data.models.VideoWithUserInfoModel
import ru.shvetsov.memehub.databinding.FragmentVideoPlayerBinding
import ru.shvetsov.memehub.presentation.adapters.VideoPagerAdapter
import ru.shvetsov.memehub.presentation.viewmodels.UserViewModel

@AndroidEntryPoint
class VideoPlayerFragment : Fragment() {

    private lateinit var binding: FragmentVideoPlayerBinding
    private lateinit var videoPagerAdapter: VideoPagerAdapter
    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val videoList = requireArguments().getParcelableArrayList("videoList", VideoWithUserInfoModel::class.java)
        val startIndex = requireArguments().getInt("startIndex")

        videoPagerAdapter = VideoPagerAdapter(videoList.orEmpty().toMutableList())

        binding.viewPager.apply {
            adapter = videoPagerAdapter
            setCurrentItem(startIndex, false)
            orientation = ViewPager2.ORIENTATION_VERTICAL
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }

    companion object {
        fun newInstance(videoList: List<VideoWithUserInfoModel>, startIndex: Int): VideoPlayerFragment {
            val fragment = VideoPlayerFragment()
            val args = Bundle().apply {
                putParcelableArrayList("videoList", ArrayList(videoList))
                putInt("startIndex", startIndex)
            }
            fragment.arguments = args
            return fragment
        }
    }
}