package com.netcast.radio.ui.ui.settings

import android.content.*
import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.netcast.radio.R
import com.netcast.radio.databinding.FragmentSettingsBinding
import com.netcast.radio.databinding.LayoutAppmodeBinding
import com.netcast.radio.ui.ui.settings.adapter.AdapterSettings
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.netcast.radio.MainViewModel
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.request.AppConstants

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }
    private val timerReceiver = TimerReceiver()
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var layoutAppmodeBinding: LayoutAppmodeBinding
    private var bindingSettings: FragmentSettingsBinding? = null
    private val binding get() = bindingSettings!!
    private lateinit var adapterSettings: AdapterSettings
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPredEditor: SharedPreferences.Editor
    private val mainViewModel:MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this)[SettingsViewModel::class.java]
        bindingSettings = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = requireContext().getSharedPreferences("appData", Context.MODE_PRIVATE)
        sharedPredEditor = sharedPreferences.edit()
        settingsViewModel.getFavs(sharedPreferences)
        adapterSettings = AdapterSettings(settingsViewModel.getFavs(sharedPreferences))
        mainViewModel.radiotimer.observe(viewLifecycleOwner){
            binding.btnAlaram.text=it
        }
        binding.rvSettings.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = adapterSettings
        }
        binding.btnAlaram.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.settings_container, SleepTimerFragment())
                .commitNow()
        }
        adapterSettings.itemClickListener { pos, view, ischecked ->
            when (pos) {
                0 -> {
                    showBottomSheetDialog()
                }
                1 -> {
                    sharedPredEditor.putBoolean("stream_over_wifi", ischecked)
                    sharedPredEditor.apply()
                }
                2 -> {
                    sharedPredEditor.putBoolean("download_over_wifi", ischecked)
                    sharedPredEditor.apply()
                }
                4 -> {
                    setForwardBackwardTimeDialog()
                }
                7 -> {
                    openTermsandCons("https://baidu.eu/terms")
                }
                8, 9 -> {
                    openTermsandCons("https://baidu.eu/privacy")
                }
                else->{

                }

            }
//        setcurrentappmode(null)
        }
        AppSingelton._SleepTimer.observe(viewLifecycleOwner){
            it?.let {
                Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }

    private fun openTermsandCons(url: String) {
        startActivity(Intent(requireContext(), WebviewActivity::class.java).putExtra("url", url))
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
                    bottomSheetDialog.dismiss();
                    sharedPredEditor.putInt("App_Mode",0).apply()
                    adapterSettings.changemodetext("Dark")

                }
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    bottomSheetDialog.dismiss()
                    sharedPredEditor.putInt("App_Mode",1).apply()
                    adapterSettings.changemodetext("Light")

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

    private fun setForwardBackwardTimeDialog() {
        val alert = AlertDialog.Builder(requireContext())
        val edittext = EditText(requireContext())
        edittext.maxLines = 1
        edittext.inputType=InputType.TYPE_CLASS_NUMBER
        edittext.setText(sharedPreferences.getLong(AppConstants.PLAYER_SECS,15).toString())
        val layout = FrameLayout(requireContext())
        layout.setPaddingRelative(45, 15, 45, 0)
        alert.setTitle("Step back and forward in player (Seconds)")
        layout.addView(edittext)
        alert.setView(layout)
        alert.setPositiveButton(getString(R.string.ok)) { dialog, which ->
            run {
                sharedPreferences.edit().putLong(AppConstants.PLAYER_SECS,edittext.text.toString().toLong()).apply()
            }

        }
        alert.setNegativeButton(getString(R.string.cancel)) {

                dialog, which ->
            run {
                dialog.dismiss()
            }

        }

        alert.show()
    }

    override fun onResume() {
        super.onResume()
    }
    override fun onPause() {
//        requireContext().unregisterReceiver(timerReceiver)
        super.onPause()
    }

}