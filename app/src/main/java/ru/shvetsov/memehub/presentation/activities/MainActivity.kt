package ru.shvetsov.memehub.presentation.activities

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import ru.shvetsov.memehub.R
import ru.shvetsov.memehub.databinding.ActivityMainBinding
import ru.shvetsov.memehub.presentation.fragments.ProfileFragment
import ru.shvetsov.memehub.presentation.fragments.UploadVideoFragment
import ru.shvetsov.memehub.presentation.fragments.VideoFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val profileFragment = ProfileFragment()
    private val uploadVideoFragment = UploadVideoFragment()
    private val videoFragment = VideoFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this) {
            moveTaskToBack(true)
        }
        setBottomNavigationListener()
    }

    private fun setBottomNavigationListener() {
        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_holder, videoFragment)
                        .commit()
                }

                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_holder, profileFragment)
                        .commit()
                }

                R.id.add -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_holder, uploadVideoFragment)
                        .commit()
                }
            }
            true
        }
    }
}