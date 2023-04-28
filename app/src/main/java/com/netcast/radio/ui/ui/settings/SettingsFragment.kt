package com.netcast.radio.ui.ui.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.netcast.radio.MainViewModel
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.databinding.FragmentSettingsBinding
import com.netcast.radio.databinding.LayoutAppmodeBinding
import com.netcast.radio.request.AppConstants
import com.netcast.radio.ui.ui.settings.adapter.AdapterSettings
import java.util.*


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
    private val mainViewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        bindingSettings = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = requireContext().getSharedPreferences("appData", Context.MODE_PRIVATE)
        sharedPredEditor = sharedPreferences.edit()
        settingsViewModel.getFavs(sharedPreferences)
        adapterSettings = AdapterSettings(settingsViewModel.getFavs(sharedPreferences))
        mainViewModel.radiotimer.observe(viewLifecycleOwner) {
            binding.btnAlaram.text = it
        }
        binding.rvSettings.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = adapterSettings
        }
        setcurrentappmode(null)
        binding.btnAlaram.setOnClickListener {
            /* requireActivity().supportFragmentManager.beginTransaction()
                 .replace(R.id.settings_container, AlarmFragment()).commitNow()*/
            requireContext().startActivity(Intent(requireContext(), AlarmFragment::class.java))
        }

        binding.btnSleepTimer.setOnClickListener {
            /*requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.settings_container, SleepTimerFragment()).commitNow()*/
            requireContext().startActivity(Intent(requireContext(), SleepTimerFragment::class.java))

        }
        adapterSettings.itemClickListener { pos, view, ischecked ->
            when (pos) {
                0 -> {
                    showBottomSheetDialog()
                }
                1 -> {
                    sharedPredEditor.putBoolean("stream_over_wifi", ischecked)
                    sharedPredEditor.apply()
//                    adapterSettings.notifyDataSetChanged()
                }
                2 -> {
                    sharedPredEditor.putBoolean("download_over_wifi", ischecked)
                    sharedPredEditor.apply()
//                    adapterSettings.notifyDataSetChanged()
                }
                3 -> {
                    sharedPredEditor.putBoolean("delete_completed_episode", ischecked)
                    sharedPredEditor.apply()
//                    adapterSettings.notifyDataSetChanged()
                }
                4 -> {
                    sharedPredEditor.putBoolean(AppConstants.SKIP_SLIENCE, ischecked).apply()
                }
                5 -> {
                    sharedPredEditor.putBoolean(AppConstants.AUTO_PLAY_EPISODES, ischecked).apply()
                }

                6 -> {
                    setForwardBackwardTimeDialog()
                }
                8 -> {
                    startActivity(
                        Intent(requireContext(), FAQsActivity::class.java)
                            .putExtra("type", "IMPRINT")
                    )

                }

                9 -> {
                    openTermsandCons("https://baidu.eu/terms")
                }
                10 -> {
                    openTermsandCons("https://baidu.eu/privacy")
                }
                11 -> {
                    openTermsandCons("https://baidu.eu/privacy")
                }
                12 -> {
                    startActivity(
                        Intent(requireContext(), FAQsActivity::class.java)
                            .putExtra("type", "FAQS")
                    )
                }
                else -> {
//                    openTermsandCons("https://baidu.eu/privacy")

                    sendEmail()
                }

            }
//          setcurrentappmode(null)
        }
        AppSingelton._SleepTimer.observe(viewLifecycleOwner) {
            it?.let {
                binding.tvTimer.text = it
            }
        }
        return root
    }

    private fun sendEmail() {
        try {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val version = Build.VERSION.SDK_INT
            val versionRelease = Build.VERSION.RELEASE
            val lang = Locale.getDefault().getDisplayLanguage()
            val conntectionType = getConnectionType(requireContext())
            val operatorName =
                (requireContext().getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)?.networkOperatorName
                    ?: "unknown"
            var type = if (conntectionType == 2) "true" else "false"
            val connected = if (conntectionType == 0) "false" else "true"
            val text =
                "netCast\nBuild: freerelease" + "\n" + "Version: " + versionRelease + "\n" + "Locale: ${lang}\n" + "Free app without prime subscription" + "\n" +
                        manufacturer + " " + model + "\nConnected? ${connected}, Wifi? ${type} \n Provider (Network/Sim) ${operatorName}"

            val intent = Intent(Intent.ACTION_SEND)
            val recipients = arrayOf("legal@baidu.eu")
            intent.putExtra(Intent.EXTRA_EMAIL, recipients)
            intent.putExtra(Intent.EXTRA_SUBJECT, "FeedBack")
            intent.putExtra(Intent.EXTRA_TEXT, text)
            intent.type = "text/html"
            intent.setPackage("com.google.android.gm")
            startActivity(Intent.createChooser(intent, "Send mail"))
        } catch (e: ActivityNotFoundException) {
            //TODO smth
        }
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
                    sharedPredEditor.putInt("App_Mode", 0).apply()
                    adapterSettings.changemodetext("Dark")

                }
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    bottomSheetDialog.dismiss()
                    sharedPredEditor.putInt("App_Mode", 1).apply()
                    adapterSettings.changemodetext("Light")

                }

            }

        }

        bottomSheetDialog.show()
    }

    private fun setcurrentappmode(radioGroup: RadioGroup?) {
        val nightModeFlags =
            requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
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
        edittext.inputType = InputType.TYPE_CLASS_NUMBER
        edittext.setText(sharedPreferences.getLong(AppConstants.PLAYER_SECS, 15).toString())
        val layout = FrameLayout(requireContext())
        layout.setPaddingRelative(45, 15, 45, 0)
        alert.setTitle("Step back and forward in player (Seconds)")
        layout.addView(edittext)
        alert.setView(layout)
        alert.setPositiveButton(getString(R.string.ok)) { dialog, which ->
            run {
                sharedPreferences.edit()
                    .putLong(AppConstants.PLAYER_SECS, edittext.text.toString().toLong()).apply()
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
        super.onPause()
    }

    fun getConnectionType(context: Context): Int {
        var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities =
                cm.getNetworkCapabilities(cm.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = 2
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = 1
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    result = 3
                }
            }
        } else {
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null) {
                // connected to the internet
                if (activeNetwork.type === ConnectivityManager.TYPE_WIFI) {
                    result = 2
                } else if (activeNetwork.type === ConnectivityManager.TYPE_MOBILE) {
                    result = 1
                } else if (activeNetwork.type === ConnectivityManager.TYPE_VPN) {
                    result = 3
                }
            }
        }
        return result
    }

}