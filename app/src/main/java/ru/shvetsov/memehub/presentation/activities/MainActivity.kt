package ru.shvetsov.memehub.presentation.activities

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import ru.shvetsov.memehub.R
import ru.shvetsov.memehub.databinding.ActivityMainBinding
import ru.shvetsov.memehub.presentation.fragments.ProfileFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val profileFragment = ProfileFragment()

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
            when(it.itemId) {
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_holder, profileFragment)
                        .commit()
                }
            }
            true
        }
    }
}