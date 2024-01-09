package com.netcast.baidutv.ui.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
import com.netcast.baidutv.base.AppSingelton
import com.netcast.baidutv.databinding.FragmentSleepTimerBinding


class SleepTimerFragment : AppCompatActivity() {
 /*   private var _binding: FragmentSleepTimerBinding? = null
    private val binding get() = _binding!!*/
 private lateinit var binding: FragmentSleepTimerBinding

    private var countdowntimer: CountDownTimer? = null
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPredEditor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSleepTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.header.tvTitle.text="Sleep Timer"
        binding.header.imgBack.setOnClickListener {
            finish()
        }
        sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE)
        sharedPredEditor = sharedPreferences.edit()

        AppSingelton._SleepTimer.observe(this) {
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

    }
 /*   override fun onCreateView(
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
    }*/

    private fun startService() {
        stopService()
        val intent = Intent(this, TimerService::class.java)
        intent.putExtra("time", binding.continuousSlider.value.toLong())
        startService(intent)
    }

    private fun stopService() {
        stopService(Intent(this, TimerService::class.java))
    }


}