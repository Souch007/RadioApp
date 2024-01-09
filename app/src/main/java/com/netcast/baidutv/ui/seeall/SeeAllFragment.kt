package com.netcast.baidutv.ui.seeall

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.netcast.baidutv.MainViewModel
import com.netcast.baidutv.R
import com.netcast.baidutv.base.AppSingelton
import com.netcast.baidutv.base.BaseFragment
import com.netcast.baidutv.databinding.FragmentSeeAllBinding
import com.netcast.baidutv.ui.seeall.adapter.SeeAllAdapter
import com.netcast.baidutv.ui.seeall.adapter.SeeAllPodAdapter


class SeeAllFragment : BaseFragment<FragmentSeeAllBinding>(R.layout.fragment_see_all) {
    val seeAllViewModel: SeeAllViewModel by activityViewModels()
    private lateinit var mainActivityViewModel: MainViewModel
    private var seeAllAdapter : SeeAllAdapter?=null
    private var seeAllPodAdapter: SeeAllPodAdapter?  = null
    private var page = 1
    @SuppressLint("NotifyDataSetChanged")
    override fun FragmentSeeAllBinding.initialize() {
        binding.lifecycleOwner = this@SeeAllFragment
        binding.seeallviewmodel = seeAllViewModel

        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!)[MainViewModel::class.java]
        }
        binding.mainViewModel = mainActivityViewModel

        mainActivityViewModel._selectedSeeAllListRadio.observe(this@SeeAllFragment) {
            it?.let { radioList ->
                binding.radioRv.visibility = View.VISIBLE
                binding.podcastRv.visibility = View.GONE
                seeAllAdapter= SeeAllAdapter(
                    radioList,
                    mainActivityViewModel,
                    mainActivityViewModel._radioSelectedTitle.value
                )
                binding.seeallaadapter = seeAllAdapter
            }

        }

        mainActivityViewModel._selectedSeeAllPodcasts.observe(this@SeeAllFragment) {
            binding.podcastRv.visibility = View.VISIBLE
            binding.radioRv.visibility = View.GONE
            seeAllPodAdapter= SeeAllPodAdapter(
                it,
                mainActivityViewModel,
                mainActivityViewModel._radioSelectedTitle.value,
            )
            binding.seeallpodadapter = seeAllPodAdapter
        }

        AppSingelton._isFavUpdated.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    seeAllAdapter?.notifyDataSetChanged()
                    seeAllPodAdapter?.notifyDataSetChanged()
                }

            }
        }



        binding.ivBack.setOnClickListener {
            if (mainActivityViewModel._radioSeeAllSelected.value == "PODCAST")
                mainActivityViewModel._radioSeeAllSelected.value = "CLOSE_PODCAST"
            else
                mainActivityViewModel._radioSeeAllSelected.value = "CLOSE"
        }
        binding.tvChannelName.text = mainActivityViewModel._radioSelectedTitle.value
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