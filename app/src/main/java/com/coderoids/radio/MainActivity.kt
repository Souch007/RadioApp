package com.coderoids.radio

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.coderoids.radio.databinding.ActivityMainBinding
import com.coderoids.radio.ui.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
       navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_radio -> {
                    binding.tvRadio.text = "Radio"
                }
                R.id.navigation_podcast -> {
                    binding.tvRadio.text = "Podcast"

                }
                R.id.navigation_favourites -> {
                    binding.tvRadio.text = "Favourites"
                }
                R.id.navigation_search -> {
                    binding.tvRadio.text = "Search"

                }
            }
        }

        binding.ivSettings.setOnClickListener {
                Intent(this@MainActivity,SettingsActivity::class.java).apply {
                    startActivity(this)
                }
        }

    }
}