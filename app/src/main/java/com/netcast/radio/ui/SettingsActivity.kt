package com.netcast.radio.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.netcast.radio.R
import com.netcast.radio.databinding.ActivitySettingsBinding
import com.netcast.radio.ui.ui.settings.SettingsFragment

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Settings);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_settings)
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