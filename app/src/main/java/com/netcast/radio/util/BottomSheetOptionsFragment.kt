package com.netcast.radio.util

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.databinding.OptionLayoutBinding
import com.netcast.radio.ui.ui.settings.AlarmFragment
import com.netcast.radio.ui.ui.settings.SleepTimerFragment

class BottomSheetOptionsFragment(private var optionsClickListner: OptionsClickListner) : BottomSheetDialogFragment() {

    private var _binding: OptionLayoutBinding? = null
    private val optionLayoutBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = OptionLayoutBinding.inflate(inflater, container, false)
        optionLayoutBinding.tvSetalarm.setOnClickListener {
            startActivity(Intent(requireContext(), AlarmFragment::class.java))
            dismiss()
            optionsClickListner.onSetAlarm()
//            closePlayerandPanel()
//            dataBinding.slidingLayout.panelState=SlidingUpPanelLayout.PanelState.COLLAPSED
        }
        optionLayoutBinding.tvSetsleeptime.setOnClickListener {
//            navController.navigate(R.id.sleepTimerFragment)
            optionsClickListner.onSleepTimer()

            dismiss()
//            closePlayerandPanel()
//            dataBinding.slidingLayout.panelState=SlidingUpPanelLayout.PanelState.COLLAPSED
        }
        optionLayoutBinding.tvShare.setOnClickListener {
            optionsClickListner.onShare()
//           dismiss()
//            share(
//                "Checkout this link its amazing. ",
//                AppSingelton._radioSelectedChannel.value
//            )

        }
        optionLayoutBinding.tvFavourite.setOnClickListener {
            dismiss()
            optionsClickListner.onFavourite()
//            AppSingelton._radioSelectedChannel.value?.let { it1 ->
//                viewModel.addChannelToFavourites(
//                    it1
//                )
//            }

        }
        return optionLayoutBinding.root
    }
}