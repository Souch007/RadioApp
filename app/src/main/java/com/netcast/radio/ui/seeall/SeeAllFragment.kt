package com.netcast.radio.ui.seeall

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.netcast.radio.MainViewModel
import com.netcast.radio.R
import com.netcast.radio.base.BaseFragment
import com.netcast.radio.databinding.FragmentSeeAllBinding
import com.netcast.radio.util.EndLessLoading


class SeeAllFragment : BaseFragment<FragmentSeeAllBinding>(R.layout.fragment_see_all) {
    val seeAllViewModel: SeeAllViewModel by activityViewModels()
    private lateinit var mainActivityViewModel: MainViewModel

    private var page = 1
    override fun FragmentSeeAllBinding.initialize() {
        binding.lifecycleOwner = this@SeeAllFragment
        binding.seeallviewmodel = seeAllViewModel

        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!)[MainViewModel::class.java]
        }
        binding.mainViewModel = mainActivityViewModel

        mainActivityViewModel._selectedSeeAllListRadio.observe(this@SeeAllFragment) {
            it?.let {radioList->
                binding.radioRv.visibility = View.VISIBLE
                binding.podcastRv.visibility = View.GONE
                binding.seeallaadapter = com.netcast.radio.ui.seeall.adapter.SeeAllAdapter(
                    radioList,
                    mainActivityViewModel
                )
            }

        }

        mainActivityViewModel._selectedSeeAllPodcasts.observe(this@SeeAllFragment) {
            binding.podcastRv.visibility = View.VISIBLE
            binding.radioRv.visibility = View.GONE
            binding.seeallpodadapter = com.netcast.radio.ui.seeall.adapter.SeeAllPodAdapter(
                it,
                mainActivityViewModel
            )
        }
        binding.ivBack.setOnClickListener {
            if (mainActivityViewModel._radioSeeAllSelected.value == "PODCAST")
                mainActivityViewModel._radioSeeAllSelected.value = "CLOSE_PODCAST"
            else
                mainActivityViewModel._radioSeeAllSelected.value = "CLOSE"
        }
        binding.tvChannelName.text= mainActivityViewModel._radioSelectedTitle.value
//        setRVscrolllistener()

    }

/*    private fun setRVscrolllistener() {
        binding.radioRv.apply {
            addOnScrollListener(object : EndLessLoading() {
                override fun onLoadMore() {
                    page += 1

                }
            })
        }

    }*/
}