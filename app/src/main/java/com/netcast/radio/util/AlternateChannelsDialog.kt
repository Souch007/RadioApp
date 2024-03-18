package com.netcast.radio.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.netcast.radio.MainViewModel
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.databinding.CustomDialogLayoutBinding
import com.netcast.radio.interfaces.OnDialogClose
import com.netcast.radio.ui.radio.data.temp.RadioLists

class AlternateChannelsDialog(
    context: Context,
    data: List<RadioLists>,
    mainViewModel: MainViewModel,
    onDialogClose: OnDialogClose?
) : Dialog(context) {
    private var moreradioAdapter: com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter

    init {
        val binding = CustomDialogLayoutBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val windowAttributes = window?.attributes
        val displayMetrics = context.resources.displayMetrics
        windowAttributes?.apply {
            width = (displayMetrics.widthPixels * 0.85f).toInt()
            height = (displayMetrics.heightPixels * 0.8f).toInt()
        }
        window?.attributes = windowAttributes

        binding.imgClose.setOnClickListener {
            onDialogClose?.onDialogClose()
            dismiss()
        }
        Glide.with(context)
            .load(AppSingelton.radioSelectedChannel?.value?.favicon)
            .error(R.drawable.logo).diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH).into(binding.imageView)

        val newalternatives =
           data.filter { it.name != AppSingelton.radioSelectedChannel.value?.name && !it.isBlocked }

        moreradioAdapter = com.netcast.radio.ui.radio.adapter.RadioFragmentAdapter(
            newalternatives, mainViewModel, "public"
        )

        binding.adapter = moreradioAdapter
        binding.rvSuggested.adapter = moreradioAdapter
//        binding.radioplayervm=viewmodel
    }


}