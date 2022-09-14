package com.coderoids.radio.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.coderoids.radio.R
import com.coderoids.radio.databinding.ActivityMainBinding
import com.coderoids.radio.databinding.ActivitySettingsBinding
import com.coderoids.radio.ui.ui.settings.SettingsFragment

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings_container, SettingsFragment.newInstance())
                .commitNow()
        }
        binding.ivBackSettingsAv.setOnClickListener {
            finish()
        }

    }
}