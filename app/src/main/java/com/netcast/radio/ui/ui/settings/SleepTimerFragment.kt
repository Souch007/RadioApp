package com.netcast.radio.ui.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.slider.Slider
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.databinding.FragmentSleepTimerBinding


class SleepTimerFragment : Fragment() {
    private var _binding: FragmentSleepTimerBinding? = null
    private val binding get() = _binding!!
    private var countdowntimer: CountDownTimer? = null
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPredEditor: SharedPreferences.Editor
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSleepTimerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = requireContext().getSharedPreferences("appData", Context.MODE_PRIVATE)
        sharedPredEditor = sharedPreferences.edit()

        AppSingelton._SleepTimer.observe(viewLifecycleOwner) {
            it?.let {
                binding.tvTimer.text = it
            }
        }
        binding.continuousSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
//              mainViewModel._timerradio.value=false
            }

            override fun onStopTrackingTouch(slider: Slider) {
                sharedPredEditor.putFloat("sleep_timer", slider.value).commit()
                if (binding.switchTimer.isChecked) {
                    startService()
//                    setTimer(slider.value.toLong())

                }
            }
        })
        binding.switchTimer.setOnCheckedChangeListener { compoundButton, b ->
            sharedPredEditor.putBoolean("is_SleepTimeron", b).commit()
            if (b) {
                startService()
            } else {
                stopService()
                countdowntimer?.cancel()
            }
        }
        if (sharedPreferences.getBoolean("is_SleepTimeron", false)) {
            binding.switchTimer.isChecked = true
        }
        binding.continuousSlider.value = sharedPreferences.getFloat("sleep_timer", 5.0F)
        return root
    }

    private fun startService() {
        stopService()
        val intent = Intent(requireContext(), TimerService::class.java)
        intent.putExtra("time", binding.continuousSlider.value.toLong())
        requireContext().startService(intent)
    }

    private fun stopService() {
        requireContext().stopService(Intent(requireContext(), TimerService::class.java))
    }


}