package com.netcast.radio.util

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.netcast.radio.databinding.OptionLayoutBinding
import com.netcast.radio.ui.ui.settings.AlarmFragment

class BottomSheetOptionsFragment(
    private var optionsClickListner: OptionsClickListner,
    private val hidefav: Boolean,
    private val isEpisode: Boolean
) :
    BottomSheetDialogFragment() {

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
            dismiss()
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
        if (hidefav)
            optionLayoutBinding.tvFavourite.visibility = View.GONE
        else
            optionLayoutBinding.tvFavourite.visibility = View.VISIBLE
        if (isEpisode)
            optionLayoutBinding.tvShare.text = "Share Podcast"

        return optionLayoutBinding.root
    }
}