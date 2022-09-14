package com.coderoids.radio.ui.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.coderoids.radio.databinding.FragmentSettingsBinding
import com.coderoids.radio.ui.ui.settings.adapter.AdapterSettings

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var settingsViewModel: SettingsViewModel
    private var bindingSettings : FragmentSettingsBinding? = null;
    private val binding get() = bindingSettings!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        bindingSettings = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        settingsViewModel.getFavs()
        binding.rvSettings.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = AdapterSettings(settingsViewModel.getFavs())
        }
        return root
    }


}