package com.coderoids.radio.ui.ui.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.coderoids.radio.R
import com.coderoids.radio.databinding.FragmentSettingsBinding
import com.coderoids.radio.databinding.LayoutAppmodeBinding
import com.coderoids.radio.ui.ui.settings.adapter.AdapterSettings
import com.google.android.material.bottomsheet.BottomSheetDialog

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var settingsViewModel: SettingsViewModel
    private var bindingSettings: FragmentSettingsBinding? = null
    private lateinit var layoutAppmodeBinding: LayoutAppmodeBinding
    private val binding get() = bindingSettings!!
    private lateinit var adapterSettings: AdapterSettings

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        bindingSettings = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        settingsViewModel.getFavs()
        adapterSettings = AdapterSettings(settingsViewModel.getFavs())
        binding.rvSettings.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = adapterSettings
        }
        adapterSettings.itemClickListener { pos, view ->
            if (pos == 0) {
                showBottomSheetDialog()
            }

        }
        setcurrentappmode(null)
        return root
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        layoutAppmodeBinding = LayoutAppmodeBinding.inflate(layoutInflater, null, false)
        bottomSheetDialog.setContentView(layoutAppmodeBinding!!.root)
        setcurrentappmode(layoutAppmodeBinding.radioGroup)
        layoutAppmodeBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_dark -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    bottomSheetDialog.dismiss()
                    adapterSettings.notifyDataSetChanged()
                }
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    bottomSheetDialog.dismiss()
                    adapterSettings.notifyDataSetChanged()
                }

            }

        }

        bottomSheetDialog.show()
    }

    private fun setcurrentappmode(radioGroup: RadioGroup?) {
        val nightModeFlags = requireContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_NO -> {
                radioGroup?.check(radioGroup[0].id)
                adapterSettings.changemodetext("Light")
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                radioGroup?.check(radioGroup[1].id)
                adapterSettings.changemodetext("Dark")
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}
        }
    }


}