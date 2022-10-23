package com.coderoids.radio.ui.seeall

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.coderoids.radio.MainViewModel
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseFragment
import com.coderoids.radio.databinding.FragmentSeeAllBinding
import com.coderoids.radio.ui.radio.RadioViewModel


class SeeAllFragment : BaseFragment<FragmentSeeAllBinding>(R.layout.fragment_see_all){
    val seeAllViewModel: SeeAllViewModel by activityViewModels()
    private lateinit var mainActivityViewModel : MainViewModel

    override fun FragmentSeeAllBinding.initialize() {
        binding.lifecycleOwner = this@SeeAllFragment
        binding.seeallviewmodel = seeAllViewModel
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!).get(MainViewModel::class.java)
        }
        binding.mainViewModel = mainActivityViewModel

        mainActivityViewModel._selectedSeeAllListRadio.observe(this@SeeAllFragment){
            binding.seeallaadapter = com.coderoids.radio.ui.seeall.adapter.SeeAllAdapter(
                it,
                mainActivityViewModel
            )
        }
        binding.ivBack.setOnClickListener {
            mainActivityViewModel._radioSeeAllSelected.value = "CLOSE"

        }

    }
}