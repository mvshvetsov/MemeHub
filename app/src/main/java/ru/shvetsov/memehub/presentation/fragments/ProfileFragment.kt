package ru.shvetsov.memehub.presentation.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.shvetsov.memehub.data.network.token.TokenStorage
import ru.shvetsov.memehub.databinding.FragmentProfileBinding
import ru.shvetsov.memehub.presentation.activities.EditProfileActivity
import ru.shvetsov.memehub.presentation.activities.LoginActivity
import ru.shvetsov.memehub.presentation.viewmodels.UserViewModel
import ru.shvetsov.memehub.utils.extentions.toDp
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: UserViewModel by activityViewModels()

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
                .override(110.toDp(requireContext()), 110.toDp(requireContext()))
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

        binding.logout.setOnClickListener {
            viewModel.logout()
        }

        binding.editProfileButton.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val userId = tokenStorage.getUserId()
        viewModel.getUserProfile(userId)
    }
}