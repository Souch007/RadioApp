package com.netcast.radio.ui.ui.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.slider.Slider
import com.netcast.radio.MainViewModel
import com.netcast.radio.databinding.FragmentSleepTimerBinding
import java.util.concurrent.TimeUnit


class SleepTimerFragment : Fragment() {
    private var _binding: FragmentSleepTimerBinding? = null
    private val binding get() = _binding!!
    private var countdowntimer: CountDownTimer? = null
    private val mainViewModel: MainViewModel by activityViewModels()
    private val timerReceiver = TimerReceiver()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSleepTimerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.continuousSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
//              mainViewModel._timerradio.value=false
            }

            override fun onStopTrackingTouch(slider: Slider) {
                if (binding.switchTimer.isChecked) {
                    countdowntimer?.cancel()
//                    setTimer(slider.value.toLong())

                }
            }
        })
        binding.switchTimer.setOnCheckedChangeListener { compoundButton, b ->
            if (b)
            {
                requireContext().stopService(Intent(requireContext(), TimerService::class.java))
                val intent = Intent(requireContext(), TimerService::class.java)
                intent.putExtra("time",binding.continuousSlider.value.toLong())
                requireContext().startService(intent)
            }
//                setTimer(binding.continuousSlider.value.toLong())
            else
                countdowntimer?.cancel()
        }
        return root
    }

   /* private fun setTimer(time: Long) {
        countdowntimer = object : CountDownTimer(time * 1000 * 60, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimer.text = "" + String.format(
                    "%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            )
                )
                mainViewModel._timerradio.value=String.format(
                    "%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            )
                )

            }

            override fun onFinish() {

            }
        }
        (countdowntimer as CountDownTimer).start()
    }*/
    private inner class TimerReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return

            when (intent.action) {
                TimerService.ACTION_TICK -> {
                    val timeLeft = intent.getStringExtra(TimerService.TIME_LEFT_KEY)
                    binding.tvTimer.text=timeLeft
//                    updateUIForTick(timeLeft)
                }
                TimerService.ACTION_FINISHED -> {}/*updateUIForTimerFinished()*/
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(timerReceiver, IntentFilter(TimerService.ACTION_TICK))
         requireContext().registerReceiver(timerReceiver, IntentFilter(TimerService.ACTION_FINISHED))
    }
    override fun onPause() {
        requireContext().unregisterReceiver(timerReceiver)
        super.onPause()
    }
}